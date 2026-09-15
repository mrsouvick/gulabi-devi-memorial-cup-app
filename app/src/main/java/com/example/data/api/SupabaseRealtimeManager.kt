package com.example.data.api

import android.util.Log
import com.example.data.model.TournamentContent
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class SupabaseRealtimeManager(
    private val onDataUpdated: (TournamentContent?) -> Unit,
    private val onConnectionStateChanged: (Boolean) -> Unit = {}
) {
    private val tag = "SupabaseRealtime"
    private var webSocket: WebSocket? = null
    private var heartbeatJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.IO + Job())
    private var isSubscribed = false

    private val moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()
    private val contentAdapter = moshi.adapter(TournamentContent::class.java)

    private val client = OkHttpClient.Builder()
        .readTimeout(0, TimeUnit.MILLISECONDS)
        .pingInterval(20, TimeUnit.SECONDS)
        .build()

    fun connect() {
        if (webSocket != null) return
        val request = Request.Builder()
            .url(SupabaseConfig.WS_URL)
            .build()

        webSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(ws: WebSocket, response: Response) {
                Log.d(tag, "WebSocket Connected successfully")
                onConnectionStateChanged(true)
                isSubscribed = true
                sendJoin(ws)
                startHeartbeat(ws)
            }

            override fun onMessage(ws: WebSocket, text: String) {
                try {
                    val root = JSONObject(text)
                    val event = root.optString("event")
                    val topic = root.optString("topic")

                    if (topic.contains("tournament_content")) {
                        if (event == "postgres_changes" || event == "UPDATE" || event == "INSERT") {
                            val payload = root.optJSONObject("payload")
                            val record = payload?.optJSONObject("data")
                                ?: payload?.optJSONObject("record")
                                ?: payload?.optJSONObject("new")

                            val contentJson = record?.optJSONObject("content")
                            if (contentJson != null) {
                                val parsed = contentAdapter.fromJson(contentJson.toString())
                                if (parsed != null) {
                                    onDataUpdated(parsed)
                                    return
                                }
                            }
                            // If partial or nested, trigger re-fetch
                            onDataUpdated(null)
                        }
                    }
                } catch (e: Exception) {
                    Log.e(tag, "Error parsing realtime message: ${e.message}")
                    onDataUpdated(null)
                }
            }

            override fun onClosing(ws: WebSocket, code: Int, reason: String) {
                Log.d(tag, "WebSocket Closing: $reason")
                onConnectionStateChanged(false)
            }

            override fun onClosed(ws: WebSocket, code: Int, reason: String) {
                Log.d(tag, "WebSocket Closed: $reason")
                onConnectionStateChanged(false)
                cleanUp()
            }

            override fun onFailure(ws: WebSocket, t: Throwable, response: Response?) {
                Log.e(tag, "WebSocket Failure: ${t.message}")
                onConnectionStateChanged(false)
                cleanUp()
                // Reconnect after delay if desired
                if (isSubscribed) {
                    scope.launch {
                        delay(5000)
                        if (isSubscribed) connect()
                    }
                }
            }
        })
    }

    private fun sendJoin(ws: WebSocket) {
        val joinMsg = JSONObject().apply {
            put("topic", "realtime:public:tournament_content")
            put("event", "phx_join")
            put("ref", "1")
            val payload = JSONObject().apply {
                val config = JSONObject().apply {
                    val change = JSONObject().apply {
                        put("event", "*")
                        put("schema", "public")
                        put("table", "tournament_content")
                    }
                    put("postgres_changes", org.json.JSONArray().put(change))
                }
                put("config", config)
            }
            put("payload", payload)
        }
        ws.send(joinMsg.toString())
        Log.d(tag, "Phoenix join channel sent")
    }

    private fun startHeartbeat(ws: WebSocket) {
        heartbeatJob?.cancel()
        heartbeatJob = scope.launch {
            while (isActive) {
                delay(25000)
                try {
                    val hb = JSONObject().apply {
                        put("topic", "phoenix")
                        put("event", "heartbeat")
                        put("payload", JSONObject())
                        put("ref", "hb")
                    }
                    ws.send(hb.toString())
                } catch (e: Exception) {
                    Log.w(tag, "Failed to send heartbeat: ${e.message}")
                }
            }
        }
    }

    private fun cleanUp() {
        heartbeatJob?.cancel()
        heartbeatJob = null
        webSocket = null
    }

    fun disconnect() {
        isSubscribed = false
        cleanUp()
        webSocket?.close(1000, "App paused or backgrounded")
        webSocket = null
        onConnectionStateChanged(false)
    }
}
