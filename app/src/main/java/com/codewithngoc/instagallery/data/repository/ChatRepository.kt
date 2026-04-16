package com.codewithngoc.instagallery.data.repository

import com.codewithngoc.instagallery.data.InstaGalleryApi
import com.codewithngoc.instagallery.data.local.dao.ChatDao
import com.codewithngoc.instagallery.data.local.entity.ChatMessageEntity
import com.codewithngoc.instagallery.data.local.entity.ConversationEntity
import com.codewithngoc.instagallery.data.model.*
import com.codewithngoc.instagallery.data.remote.ApiResponse
import com.codewithngoc.instagallery.data.remote.safeApiCall
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ChatRepository @Inject constructor(
    private val api: InstaGalleryApi,
    private val chatDao: ChatDao
) {
    private val gson = Gson()

    fun getConversationsFlow(): Flow<List<ConversationResponse>> {
        return chatDao.getConversations().map { list ->
            list.map { it.toConversationResponse(gson) }
        }
    }

    suspend fun getConversationById(conversationId: Long): ConversationResponse? {
        return chatDao.getConversationById(conversationId)?.toConversationResponse(gson)
    }

    suspend fun refreshConversations(): ApiResponse<Unit> {
        val res = safeApiCall { api.getConversations() }
        if (res is ApiResponse.Success) {
            val actData = res.data
            val entities = actData.conversations.map { ConversationEntity.fromConversationResponse(it, gson) }
            // ✅ Xóa data cũ trước khi insert để tránh lẫn data giữa các user
            chatDao.clearConversations()
            chatDao.insertConversations(entities)
            return ApiResponse.Success(Unit)
        }
        if (res is ApiResponse.Error) return ApiResponse.Error(res.code, res.message)
        if (res is ApiResponse.Exception) return ApiResponse.Exception(res.exception)
        return ApiResponse.Error(0, "Unknown Error")
    }

    fun getMessagesFlow(conversationId: Long): Flow<List<ChatMessageResponse>> {
        return chatDao.getMessagesForConversation(conversationId, 200).map { list ->
            list.map { it.toChatMessageResponse() }
        }
    }

    suspend fun refreshMessages(
        conversationId: Long,
        page: Int = 1,
        limit: Int = 30
    ): ApiResponse<Unit> {
        val res = safeApiCall { api.getMessages(conversationId, page, limit) }
        if (res is ApiResponse.Success) {
            chatDao.clearMessagesForConversation(conversationId)
            val actData = res.data
            val entities = actData.messages.map { 
                val entity = ChatMessageEntity.fromChatMessageResponse(it)
                if (entity.conversationId == 0L) entity.copy(conversationId = conversationId) else entity
            }
            if (entities.isNotEmpty()) chatDao.insertMessages(entities)
            return ApiResponse.Success(Unit)
        }
        if (res is ApiResponse.Error) return ApiResponse.Error(res.code, res.message)
        if (res is ApiResponse.Exception) return ApiResponse.Exception(res.exception)
        return ApiResponse.Error(0, "Unknown Error")
    }
    
    suspend fun insertMessageLocal(message: ChatMessageResponse) {
        chatDao.insertMessage(ChatMessageEntity.fromChatMessageResponse(message))
    }

    suspend fun deleteTempMessage(tempId: Long) {
        chatDao.deleteMessageById(tempId)
    }

    suspend fun updateConversationPreview(conversationId: Long, content: String?, timestamp: String?) {
        chatDao.updateConversationLastMessage(conversationId, content, timestamp)
    }

    suspend fun receiveIncomingMessage(message: ChatMessageResponse) {
        chatDao.insertMessage(ChatMessageEntity.fromChatMessageResponse(message))
        chatDao.updateConversationLastMessage(
            conversationId = message.conversationId,
            lastMessage = message.content,
            lastMessageTime = message.createdAt
        )
    }

    suspend fun sendMessageToBackend(
        conversationId: Long,
        content: String
    ): ApiResponse<ChatMessageResponse> {
        val res = safeApiCall {
            api.sendMessage(
                conversationId, 
                SendMessageRequest(content = content, messageType = "TEXT")
            ) 
        }
        if (res is ApiResponse.Success) {
            val backendMessage = res.data
            val entity = ChatMessageEntity.fromChatMessageResponse(backendMessage)
            val finalEntity = if (entity.conversationId == 0L) entity.copy(conversationId = conversationId) else entity
            chatDao.insertMessage(finalEntity)
            return ApiResponse.Success(backendMessage)
        }
        if (res is ApiResponse.Error) return ApiResponse.Error(res.code, res.message)
        if (res is ApiResponse.Exception) return ApiResponse.Exception(res.exception)
        return ApiResponse.Error(0, "Unknown Error")
    }

    /**
     * Tìm conversation DIRECT với partnerId trong Room.
     * Nếu không có → gọi API tạo mới → cache vào Room → trả về conversationId.
     */
    suspend fun getOrCreateDirectConversation(partnerId: Long): ApiResponse<Long> {
        // 1. Tìm trong Room local trước
        val existing = chatDao.getDirectConversationByPartnerId(partnerId)
        if (existing != null) {
            return ApiResponse.Success(existing.id)
        }

        // 2. Thử refresh từ backend (có thể đã tồn tại nhưng chưa cache)
        refreshConversations()
        val afterRefresh = chatDao.getDirectConversationByPartnerId(partnerId)
        if (afterRefresh != null) {
            return ApiResponse.Success(afterRefresh.id)
        }

        // 3. Tạo conversation mới
        val res = safeApiCall {
            api.createConversation(
                CreateConversationRequest(memberIds = listOf(partnerId), type = "DIRECT")
            )
        }
        return when (res) {
            is ApiResponse.Success -> {
                val conv = res.data
                chatDao.insertConversation(ConversationEntity.fromConversationResponse(conv, gson))
                ApiResponse.Success(conv.id)
            }
            is ApiResponse.Error -> ApiResponse.Error(res.code, res.message)
            is ApiResponse.Exception -> ApiResponse.Exception(res.exception)
        }
    }
}
