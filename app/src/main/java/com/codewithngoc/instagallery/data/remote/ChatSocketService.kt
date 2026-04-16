package com.codewithngoc.instagallery.data.remote

import android.util.Log
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import okhttp3.*
import okio.ByteString

class ChatSocketService(
    client: OkHttpClient, // Original client
    private val hostUrl: String
) {
    // Add ping interval to keep connection alive
    private val socketClient = client.newBuilder()
        .pingInterval(30, java.util.concurrent.TimeUnit.SECONDS)
        .build()

    private var webSocket: WebSocket? = null
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var currentToken: String? = null
    private var isIntentionalClose = false

    // Luồng dữ liệu để phát event tin nhắn đến ViewModel
    private val _incomingMessages = MutableSharedFlow<String>(extraBufferCapacity = 100)
    val incomingMessages: SharedFlow<String> = _incomingMessages

    // State của connection
    private val _connectionState = MutableSharedFlow<Boolean>(replay = 1)
    val connectionState: SharedFlow<Boolean> = _connectionState

    fun connect(token: String) {
        if (webSocket != null) return
        this.currentToken = token
        this.isIntentionalClose = false

        // Thay http/https thành ws/wss
        val wsUrl = hostUrl.replace("http://", "ws://").replace("https://", "wss://")
        val request = Request.Builder()
            .url("${wsUrl}api/v1/ws/chat?token=$token")
            .build()

        webSocket = socketClient.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                Log.d("ChatSocket", "Connected to WebSocket")
                _connectionState.tryEmit(true)
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                Log.d("ChatSocket", "Received message: $text")
                _incomingMessages.tryEmit(text)
            }

            override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
                Log.d("ChatSocket", "Received bytes")
            }

            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                Log.d("ChatSocket", "Closing WebSocket: $reason")
                webSocket.close(1000, null)
                _connectionState.tryEmit(false)
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                Log.d("ChatSocket", "Closed WebSocket: $reason")
                this@ChatSocketService.webSocket = null
                _connectionState.tryEmit(false)
                
                if (!isIntentionalClose) {
                    scheduleReconnect()
                }
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                Log.e("ChatSocket", "WebSocket Error: ${t.message}")
                _connectionState.tryEmit(false)
                this@ChatSocketService.webSocket = null
                
                if (!isIntentionalClose) {
                    scheduleReconnect()
                }
            }
        })
    }

    private fun scheduleReconnect() {
        Log.d("ChatSocket", "Attempting to reconnect in 5 seconds...")
        serviceScope.launch {
            delay(5000)
            currentToken?.let { connect(it) }
        }
    }

    fun sendMessage(messageJson: String) {
        webSocket?.send(messageJson) ?: Log.e("ChatSocket", "WebSocket not connected. Cannot send.")
    }

    fun disconnect() {
        isIntentionalClose = true
        webSocket?.close(1000, "User requested close")
        webSocket = null
        _connectionState.tryEmit(false)
    }
}
