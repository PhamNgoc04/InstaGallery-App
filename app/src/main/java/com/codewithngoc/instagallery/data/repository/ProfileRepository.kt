package com.codewithngoc.instagallery.data.repository

import com.codewithngoc.instagallery.data.InstaGalleryApi
import com.codewithngoc.instagallery.data.InstaGallerySession
import com.codewithngoc.instagallery.data.model.*
import com.codewithngoc.instagallery.data.remote.ApiResponse
import com.codewithngoc.instagallery.data.remote.safeApiCall
import com.codewithngoc.instagallery.data.remote.safeAuthApiCall
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import javax.inject.Inject

class ProfileRepository @Inject constructor(
    private val api: InstaGalleryApi,
    private val session: InstaGallerySession
) {

    // ── Profile ────────────────────────────────────────────────

    suspend fun getCurrentUser(): ApiResponse<UserProfileResponse> {
        val token = session.getToken()
        return safeAuthApiCall(token) { api.getCurrentUser() }
    }

    suspend fun getUserProfile(userId: Long): ApiResponse<UserProfileResponse> =
        safeApiCall { api.getUserProfile(userId) }

    suspend fun updateProfile(request: UpdateUserProfileRequest): ApiResponse<UserProfileResponse> {
        val token = session.getToken()
        return safeAuthApiCall(token) { api.updateProfile(request) }
    }

    /**
     * Upload ảnh đại diện — nhận File từ gallery/camera, tự tạo MultipartBody.Part
     */
    suspend fun updateAvatar(imageFile: File): ApiResponse<UserProfileResponse> {
        val token = session.getToken()
        val requestFile = imageFile.asRequestBody("image/*".toMediaTypeOrNull())
        val part = MultipartBody.Part.createFormData("file", imageFile.name, requestFile)
        return safeAuthApiCall(token) { api.updateAvatar(part) }
    }

    suspend fun deactivateAccount(): ApiResponse<Unit> {
        val token = session.getToken()
        return safeAuthApiCall(token) { api.deactivateAccount() }
    }

    // ── Sessions ───────────────────────────────────────────────

    suspend fun getSessions(): ApiResponse<List<SessionResponse>> {
        val token = session.getToken()
        return safeAuthApiCall(token) { api.getSessions() }
    }

    suspend fun deleteSession(sessionId: Long): ApiResponse<Unit> {
        val token = session.getToken()
        return safeAuthApiCall(token) { api.deleteSession(sessionId) }
    }

    // ── Follow ─────────────────────────────────────────────────

    suspend fun toggleFollow(userId: Long): ApiResponse<Map<String, Boolean>> =
        safeApiCall { api.toggleFollow(userId) }

    suspend fun getFollowers(
        userId: Long,
        page: Int = 1,
        limit: Int = 20
    ): ApiResponse<PaginatedFollowsResponse> =
        safeApiCall { api.getFollowers(userId, page, limit) }

    suspend fun getFollowing(
        userId: Long,
        page: Int = 1,
        limit: Int = 20
    ): ApiResponse<PaginatedFollowsResponse> =
        safeApiCall { api.getFollowing(userId, page, limit) }

    suspend fun getSuggestions(limit: Int = 10): ApiResponse<List<SearchUserResult>> =
        safeApiCall { api.getSuggestions(limit) }
}
