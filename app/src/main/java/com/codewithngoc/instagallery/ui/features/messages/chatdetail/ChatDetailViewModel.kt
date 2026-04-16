package com.codewithngoc.instagallery.ui.features.messages.chatdetail

import android.os.Build
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codewithngoc.instagallery.data.InstaGallerySession
import com.codewithngoc.instagallery.data.model.ChatMessageResponse
import com.codewithngoc.instagallery.data.remote.ApiResponse
import com.codewithngoc.instagallery.data.remote.ChatSocketService
import com.codewithngoc.instagallery.data.repository.ChatRepository
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.Instant
import javax.inject.Inject

@HiltViewModel
class ChatDetailViewModel @Inject constructor(
    private val chatRepository: ChatRepository,
    private val chatSocketService: ChatSocketService,
    private val session: InstaGallerySession,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val conversationId: Long = savedStateHandle.get<Long>("conversationId") ?: 0L

    /** userId của người dùng hiện tại — dùng để phân biệt isMe trong ChatBubble */
    val currentUserId: Long = session.getUserId() ?: 0L

    private val _messages = MutableStateFlow<List<ChatMessageResponse>>(emptyList())
    val messages: StateFlow<List<ChatMessageResponse>> = _messages.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _partnerName = MutableStateFlow<String?>(null)
    val partnerName: StateFlow<String?> = _partnerName.asStateFlow()

    private val _partnerAvatar = MutableStateFlow<String?>(null)
    val partnerAvatar: StateFlow<String?> = _partnerAvatar.asStateFlow()

    init {
        loadPartnerInfo()
        observeMessages()
        loadHistory()
        connectWebSocket()
        observeIncomingMessages()
    }

    private fun loadPartnerInfo() {
        viewModelScope.launch {
            val conv = chatRepository.getConversationById(conversationId)
            _partnerName.value = conv?.partnerName
            _partnerAvatar.value = conv?.partnerAvatar
        }
    }

    private fun observeMessages() {
        viewModelScope.launch {
            chatRepository.getMessagesFlow(conversationId).collect { list ->
                // ✅ Sử dụng ID để sắp xếp tuyệt đối: 
                // - ID từ server luôn tăng dần (1, 2, 3...), đảm bảo thứ tự cũ đến mới.
                // - Tin nhắn tạm (Optimistic) có ID âm (< 0), hoặc lỗi trả về ID = 0, ép xuống vị trí dưới cùng (MAX_VALUE).
                _messages.value = list
                    .filter { !it.content.isNullOrBlank() || !it.mediaUrl.isNullOrBlank() } // Dọn dẹp Ghost Bubbles do WebSocket map sai
                    .sortedBy { msg ->
                        if (msg.id <= 0L) Long.MAX_VALUE else msg.id
                    }
            }
        }
    }

    private fun loadHistory() {
        if (conversationId == 0L) {
            _isLoading.value = false
            return
        }
        viewModelScope.launch {
            _isLoading.value = true
            chatRepository.refreshMessages(conversationId)
            _isLoading.value = false
        }
    }

    private fun connectWebSocket() {
        // ✅ FIX #10: Không kết nối WebSocket nếu chưa có conversationId hợp lệ
        if (conversationId == 0L) return
        val token = session.getToken()
        if (!token.isNullOrEmpty()) {
            chatSocketService.connect(token)
        }
    }

    private fun observeIncomingMessages() {
        viewModelScope.launch {
            chatSocketService.incomingMessages.collect { msgJson ->
                try {
                    // ✅ Signal-to-Sync Architecture: 
                    // Bất cứ khi nào Socket "ting" một phát (dù khác format hay dư thừa), 
                    // ta ngầm kích hoạt đồng bộ hóa API nền để tải chính xác gói tin nhắn chuẩn mực 
                    // từ Backend về (giống hệt thao tác "Thoát ra vào lại" nhưng tự động 100%).
                    chatRepository.refreshMessages(conversationId)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    fun sendMessage(content: String) {
        val senderId = currentUserId
        if (senderId == 0L || content.isBlank()) return

        val now = nowIso()
        val tempId = -System.currentTimeMillis()  // ID âm → không đụng với ID server

        // ── Bước 1: Optimistic UI — hiện ngay cho user ──────────────────────
        val tempMsg = ChatMessageResponse(
            id = tempId,
            conversationId = conversationId,
            senderId = senderId,
            senderAvatar = null,
            content = content,
            messageType = "TEXT",
            createdAt = now  // ✅ ISO timestamp thực → sort xuống cuối list
        )
        viewModelScope.launch {
            chatRepository.insertMessageLocal(tempMsg)

            // Cập nhật preview MessagesScreen ngay lập tức (optimistic)
            chatRepository.updateConversationPreview(conversationId, content, now)

            // ── Bước 2: Gửi REST API → persist chính thức vào backend DB ────
            val result = chatRepository.sendMessageToBackend(conversationId, content)

            if (result is ApiResponse.Success) {
                val serverMsg = result.data
                // Bước 3: Xóa temp, Room đã có bản thực với ID + timestamp từ server
                chatRepository.deleteTempMessage(tempId)
                // Cập nhật preview với timestamp chính xác từ server
                chatRepository.updateConversationPreview(
                    conversationId,
                    serverMsg.content,
                    serverMsg.createdAt ?: now
                )
            }

            // ── Bước 4: WebSocket notify — real-time cho phía bên kia VÀ lưu vào Backend DB ───────
            val payload = mapOf(
                "action" to "SEND_MESSAGE",
                "conversationId" to conversationId,
                "senderId" to senderId,
                "content" to content,
                "messageType" to "TEXT"
            )
            chatSocketService.sendMessage(Gson().toJson(payload))
        }
    }

    /** Trả về ISO 8601 timestamp hiện tại (tương thích API & sort) */
    private fun nowIso(): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // FIX: Backend đang trả về giờ Local nhưng bị format "Z" (ví dụ 16:00 Local lại bị gắn là 16:00Z).
            // Để tin nhắn gửi đi (Optimistic UI) nằm đúng dưới cùng và không bị trôi lên đầu
            // ta cũng tạo ra giờ Local và gắn mác "Z" cho đồng bộ với Server.
            val dtf = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
            java.time.LocalDateTime.now().format(dtf)
        } else {
            java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", java.util.Locale.US).format(java.util.Date())
        }
    }

    override fun onCleared() {
        super.onCleared()
        chatSocketService.disconnect()
    }
}
