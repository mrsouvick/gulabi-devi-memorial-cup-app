package com.example.data.repository

import android.util.Log
import com.example.data.api.SupabaseApiService
import com.example.data.api.SupabaseRealtimeManager
import com.example.data.local.CachedTournamentContent
import com.example.data.local.TournamentDao
import com.example.data.model.TournamentContent
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class TournamentUiState(
    val isLoading: Boolean = true,
    val content: TournamentContent = TournamentContent(),
    val isOffline: Boolean = false,
    val errorMessage: String? = null,
    val isRealtimeConnected: Boolean = false,
    val lastSyncTimestamp: Long = 0L
)

class TournamentRepository(
    private val apiService: SupabaseApiService,
    private val tournamentDao: TournamentDao,
    private val externalScope: CoroutineScope
) {
    private val tag = "TournamentRepo"

    private val moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()
    private val contentAdapter = moshi.adapter(TournamentContent::class.java)

    private val _uiState = MutableStateFlow(TournamentUiState())
    val uiState: StateFlow<TournamentUiState> = _uiState.asStateFlow()

    private var realtimeManager: SupabaseRealtimeManager? = null

    init {
        // First load cached data immediately for instant offline-first display
        externalScope.launch {
            loadFromLocalCache()
            fetchFromRemote()
            setupRealtime()
        }
    }

    private suspend fun loadFromLocalCache() = withContext(Dispatchers.IO) {
        try {
            val cached = tournamentDao.getCachedContentDirect("main")
            if (cached != null) {
                val parsed = contentAdapter.fromJson(cached.jsonContent)
                if (parsed != null) {
                    _uiState.update { current ->
                        current.copy(
                            content = parsed,
                            isLoading = false,
                            lastSyncTimestamp = cached.lastUpdated
                        )
                    }
                    Log.d(tag, "Loaded content from local Room cache")
                }
            }
        } catch (e: Exception) {
            Log.w(tag, "Failed to load local cache: ${e.message}")
        }
    }

    suspend fun fetchFromRemote() = withContext(Dispatchers.IO) {
        try {
            _uiState.update { it.copy(isLoading = it.content.teams.isEmpty() && it.content.fixtures.isEmpty()) }
            val rows = apiService.getTournamentContent()
            if (rows.isNotEmpty() && rows[0].content != null) {
                val content = rows[0].content!!
                val jsonString = contentAdapter.toJson(content)
                tournamentDao.saveCache(
                    CachedTournamentContent(
                        id = "main",
                        jsonContent = jsonString,
                        lastUpdated = System.currentTimeMillis()
                    )
                )
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        content = content,
                        isOffline = false,
                        errorMessage = null,
                        lastSyncTimestamp = System.currentTimeMillis()
                    )
                }
                Log.d(tag, "Fetched live data from Supabase successfully")
            } else {
                _uiState.update { it.copy(isLoading = false) }
            }
        } catch (e: Exception) {
            Log.e(tag, "Failed to fetch from remote Supabase: ${e.message}")
            _uiState.update { current ->
                current.copy(
                    isLoading = false,
                    isOffline = true,
                    errorMessage = if (current.content.teams.isEmpty() && current.content.fixtures.isEmpty()) {
                        "Unable to connect to tournament server. Check your internet connection."
                    } else null
                )
            }
        }
    }

    private fun setupRealtime() {
        realtimeManager?.disconnect()
        realtimeManager = SupabaseRealtimeManager(
            onDataUpdated = { updatedContent ->
                externalScope.launch {
                    if (updatedContent != null) {
                        _uiState.update { current ->
                            current.copy(
                                content = updatedContent,
                                isOffline = false,
                                lastSyncTimestamp = System.currentTimeMillis()
                            )
                        }
                        try {
                            val jsonString = contentAdapter.toJson(updatedContent)
                            tournamentDao.saveCache(
                                CachedTournamentContent(
                                    id = "main",
                                    jsonContent = jsonString,
                                    lastUpdated = System.currentTimeMillis()
                                )
                            )
                        } catch (e: Exception) {
                            Log.w(tag, "Failed to persist realtime update to cache: ${e.message}")
                        }
                    } else {
                        // Partial event or signal to re-fetch
                        fetchFromRemote()
                    }
                }
            },
            onConnectionStateChanged = { isConnected ->
                _uiState.update { it.copy(isRealtimeConnected = isConnected) }
            }
        )
        realtimeManager?.connect()
    }

    fun reconnectRealtime() {
        realtimeManager?.connect()
    }

    fun disconnectRealtime() {
        realtimeManager?.disconnect()
    }
}
