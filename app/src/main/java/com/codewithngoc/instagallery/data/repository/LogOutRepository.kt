package com.codewithngoc.instagallery.data.repository

import com.codewithngoc.instagallery.data.InstaGallerySession
import com.codewithngoc.instagallery.data.InstaGalleryApi
import com.codewithngoc.instagallery.data.remote.ApiResponse
import com.codewithngoc.instagallery.data.remote.safeAuthApiCall
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LogOutRepository @Inject constructor(
    private val api: InstaGalleryApi,
    private val session: InstaGallerySession
) {
    suspend fun logout(): ApiResponse<Unit> {
        val token = session.getToken() ?: return ApiResponse.Error(401, "No token found")
        val refreshToken =
            session.getRefreshToken() ?: return ApiResponse.Error(400, "No refresh token found")

        // Gọi API logout với refreshToken trong body
        val result = safeAuthApiCall(token) { authToken ->
            api.logout(authToken, mapOf("refreshToken" to refreshToken))
        }

        // Xóa token + refreshToken khỏi session
        session.clearTokens()

        return result
    }
}
