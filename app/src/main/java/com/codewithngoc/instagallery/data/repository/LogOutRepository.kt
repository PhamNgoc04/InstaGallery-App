package com.codewithngoc.instagallery.data.repository

import com.codewithngoc.instagallery.data.InstaGallerySession
import com.codewithngoc.instagallery.data.InstaGalleryApi
import com.codewithngoc.instagallery.data.local.dao.ChatDao
import com.codewithngoc.instagallery.data.remote.ApiResponse
import com.codewithngoc.instagallery.data.remote.ChatSocketService
import com.codewithngoc.instagallery.data.remote.safeApiCall
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LogOutRepository @Inject constructor(
    private val api: InstaGalleryApi,
    private val session: InstaGallerySession,
    private val chatDao: ChatDao,
    // ✅ FIX #8: Inject ChatSocketService để disconnect WebSocket khi logout
    private val chatSocketService: ChatSocketService
) {
    /**
     * Đăng xuất:
     * 1. Ngắt WebSocket — user cũ không nhận được message của user mới
     * 2. Xóa toàn bộ chat cache trong Room (conversations + messages)
     * 3. Xóa session/token local
     */
    suspend fun logout(): ApiResponse<Unit> {
        val refreshToken = session.getRefreshToken()

        // ✅ FIX #8: Ngắt WebSocket trước
        chatSocketService.disconnect()

        // Xóa toàn bộ chat data trong Room
        chatDao.clearConversations()
        chatDao.clearAllMessages()

        if (!refreshToken.isNullOrEmpty()) {
            val result = safeApiCall { api.logout(refreshToken) }
            session.clearTokens()
            return result
        }

        session.clearTokens()
        return ApiResponse.Success(Unit)
    }
}
