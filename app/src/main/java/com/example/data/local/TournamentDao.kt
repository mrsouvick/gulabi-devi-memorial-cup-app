package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TournamentDao {

    @Query("SELECT * FROM tournament_cache WHERE id = :id LIMIT 1")
    fun getCachedContentFlow(id: String = "main"): Flow<CachedTournamentContent?>

    @Query("SELECT * FROM tournament_cache WHERE id = :id LIMIT 1")
    suspend fun getCachedContentDirect(id: String = "main"): CachedTournamentContent?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveCache(cache: CachedTournamentContent)
}
