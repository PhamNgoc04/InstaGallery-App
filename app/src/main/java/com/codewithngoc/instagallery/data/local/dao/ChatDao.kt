package com.codewithngoc.instagallery.data.local.dao

import androidx.room.*
import com.codewithngoc.instagallery.data.local.entity.ChatMessageEntity
import com.codewithngoc.instagallery.data.local.entity.ConversationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatDao {

    // === Conversations ===

    @Query("SELECT * FROM conversations ORDER BY lastMessageTime DESC")
    fun getConversations(): Flow<List<ConversationEntity>>

    @Query("SELECT * FROM conversations WHERE id = :conversationId LIMIT 1")
    suspend fun getConversationById(conversationId: Long): ConversationEntity?

    @Query("UPDATE conversations SET lastMessage = :lastMessage, lastMessageTime = :lastMessageTime WHERE id = :conversationId")
    suspend fun updateConversationLastMessage(conversationId: Long, lastMessage: String?, lastMessageTime: String?)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConversations(conversations: List<ConversationEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConversation(conversation: ConversationEntity)

    @Query("DELETE FROM conversations")
    suspend fun clearConversations()

    // === Messages ===

    @Query("SELECT * FROM chat_messages WHERE conversationId = :conversationId ORDER BY createdAt ASC LIMIT :limit")
    fun getMessagesForConversation(conversationId: Long, limit: Int = 200): Flow<List<ChatMessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessages(messages: List<ChatMessageEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: ChatMessageEntity)

    @Query("DELETE FROM chat_messages WHERE conversationId = :conversationId")
    suspend fun clearMessagesForConversation(conversationId: Long)

    @Query("DELETE FROM chat_messages WHERE id = :messageId")
    suspend fun deleteMessageById(messageId: Long)
}
