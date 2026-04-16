package com.codewithngoc.instagallery.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.codewithngoc.instagallery.data.local.dao.ChatDao
import com.codewithngoc.instagallery.data.local.entity.ChatMessageEntity
import com.codewithngoc.instagallery.data.local.entity.ConversationEntity

@Database(entities = [ConversationEntity::class, ChatMessageEntity::class], version = 2, exportSchema = false)
abstract class InstaGalleryDatabase : RoomDatabase() {
    abstract fun chatDao(): ChatDao
}
