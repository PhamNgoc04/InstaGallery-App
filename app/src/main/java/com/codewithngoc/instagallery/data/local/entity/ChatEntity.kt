package com.codewithngoc.instagallery.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.codewithngoc.instagallery.data.model.ChatMessageResponse
import com.codewithngoc.instagallery.data.model.ConversationResponse
import com.google.gson.Gson

@Entity(tableName = "conversations")
data class ConversationEntity(
    @PrimaryKey
    val id: Long,
    val title: String?,
    val type: String,
    val partnerId: Long?,
    val partnerName: String?,
    val partnerAvatar: String?,
    val lastMessage: String?,
    val lastMessageTime: String?,
    val unreadCount: Int
) {
    fun toConversationResponse(gson: Gson): ConversationResponse {
        return ConversationResponse(
            id = id,
            title = title,
            type = type,
            partnerId = partnerId,
            partnerName = partnerName,
            partnerAvatar = partnerAvatar,
            lastMessage = lastMessage,
            lastMessageTime = lastMessageTime,
            unreadCount = unreadCount
        )
    }

    companion object {
        fun fromConversationResponse(resp: ConversationResponse, gson: Gson): ConversationEntity {
            return ConversationEntity(
                id = resp.id,
                title = resp.title,
                type = resp.type,
                partnerId = resp.partnerId,
                partnerName = resp.partnerName,
                partnerAvatar = resp.partnerAvatar,
                lastMessage = resp.lastMessage,
                lastMessageTime = resp.lastMessageTime,
                unreadCount = resp.unreadCount
            )
        }
    }
}


@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
    @PrimaryKey
    val id: Long,
    val conversationId: Long,
    val senderId: Long,
    val senderUsername: String?,
    val senderAvatar: String?,
    val content: String?,
    val messageType: String,
    val mediaUrl: String?,
    val replyToId: Long?,
    val isDeleted: Boolean,
    val createdAt: String?
) {
    fun toChatMessageResponse(): ChatMessageResponse {
        return ChatMessageResponse(
            id = id,
            conversationId = conversationId,
            senderId = senderId,
            senderUsername = senderUsername,
            senderAvatar = senderAvatar,
            content = content,
            messageType = messageType,
            mediaUrl = mediaUrl,
            replyToId = replyToId,
            isDeleted = isDeleted,
            createdAt = createdAt
        )
    }

    companion object {
        fun fromChatMessageResponse(resp: ChatMessageResponse): ChatMessageEntity {
            return ChatMessageEntity(
                id = resp.id,
                conversationId = resp.conversationId,
                senderId = resp.senderId,
                senderUsername = resp.senderUsername,
                senderAvatar = resp.senderAvatar,
                content = resp.content,
                messageType = resp.messageType,
                mediaUrl = resp.mediaUrl,
                replyToId = resp.replyToId,
                isDeleted = resp.isDeleted,
                createdAt = resp.createdAt
            )
        }
    }
}
