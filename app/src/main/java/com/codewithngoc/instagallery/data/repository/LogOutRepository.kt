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
    // Cách 1: Gọi API logout để xoá session trên server
//    suspend fun logout(): ApiResponse<Unit> {
//        val token = session.getToken() ?: return ApiResponse.Error(401, "❌ No token found")
//        val refreshToken = session.getRefreshToken() ?: return ApiResponse.Error(400, "❌ No refresh token found")
//
//        val result = safeAuthApiCall(token) {
//            api.logout(mapOf("refreshToken" to refreshToken))
//        }
//
//        if (result is ApiResponse.Success) {
//            session.clearTokens() // ✅ chỉ clear khi server xác nhận đã xoá session
//        }
//
//        return result
//    }
////
//    // Cách 2: Không gọi API logout, chỉ xoá token + refreshToken khỏi session
    suspend fun logout(): ApiResponse<Unit> {
        // Xoá token + refreshToken khỏi session
        session.clearTokens()

        // Trả về success luôn
        return ApiResponse.Success(Unit)
    }
}
