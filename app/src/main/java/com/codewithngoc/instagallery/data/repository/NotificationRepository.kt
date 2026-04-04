package com.codewithngoc.instagallery.data.repository

import com.codewithngoc.instagallery.data.InstaGalleryApi
import com.codewithngoc.instagallery.data.model.NotificationResponse
import com.codewithngoc.instagallery.data.model.PaginatedNotificationsResponse
import com.codewithngoc.instagallery.data.model.UnreadCountResponse
import com.codewithngoc.instagallery.data.model.ApiResponseWrapper

class NotificationRepository(private val api: InstaGalleryApi) {

    suspend fun getNotifications(page: Int = 1, limit: Int = 20): Result<PaginatedNotificationsResponse> {
        return try {
            val response = api.getNotifications(page, limit)
            if (response.isSuccessful) {
                response.body()?.data?.let {
                    Result.success(it)
                } ?: Result.failure(Exception(response.body()?.message ?: "Unknown error"))
            } else {
                Result.failure(Exception("Failed to fetch notifications: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun markNotificationRead(notificationId: Long): Result<Unit> {
        return try {
            val response = api.markNotificationRead(notificationId)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Failed to mark notification as read"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun markAllNotificationsRead(): Result<Unit> {
        return try {
            val response = api.markAllNotificationsRead()
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Failed to mark all notifications as read"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUnreadNotificationCount(): Result<Int> {
        return try {
            val response = api.getUnreadNotificationCount()
            if (response.isSuccessful) {
                response.body()?.data?.let {
                    Result.success(it.count)
                } ?: Result.success(0)
            } else {
                Result.failure(Exception("Failed to fetch unread count"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
