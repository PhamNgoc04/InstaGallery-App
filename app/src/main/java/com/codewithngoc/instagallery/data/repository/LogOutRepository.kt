package com.codewithngoc.instagallery.data.repository

import com.codewithngoc.instagallery.data.InstaGallerySession
import com.codewithngoc.instagallery.data.InstaGalleryApi
import com.codewithngoc.instagallery.data.remote.ApiResponse
import com.codewithngoc.instagallery.data.remote.safeApiCall
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LogOutRepository @Inject constructor(
    private val api: InstaGalleryApi,
    private val session: InstaGallerySession
) {
    /**
     * Đăng xuất: gọi API logout với X-Refresh-Token header, sau đó xóa session local.
     * Backend mới: POST /api/v1/auth/logout (yêu cầu header X-Refresh-Token)
     */
    suspend fun logout(): ApiResponse<Unit> {
        val refreshToken = session.getRefreshToken()

        // Nếu có refreshToken => gọi API để xóa session trên server
        if (!refreshToken.isNullOrEmpty()) {
            val result = safeApiCall { api.logout(refreshToken) }
            // Dù thành công hay thất bại, vẫn xóa token local
            session.clearTokens()
            return result
        }

        // Nếu không có refreshToken => chỉ xóa local
        session.clearTokens()
        return ApiResponse.Success(Unit)
    }
}
