package com.codewithngoc.instagallery.data.repository

import com.codewithngoc.instagallery.data.InstaGalleryApi
import com.codewithngoc.instagallery.data.InstaGallerySession
import com.codewithngoc.instagallery.data.model.PaginatedFollowsResponse
import com.codewithngoc.instagallery.data.model.UserProfileResponse
import com.codewithngoc.instagallery.data.remote.ApiResponse
import com.codewithngoc.instagallery.data.remote.safeApiCall
import com.codewithngoc.instagallery.data.remote.safeAuthApiCall
import javax.inject.Inject

class ProfileRepository @Inject constructor(
    private val api: InstaGalleryApi,
    private val session: InstaGallerySession
) {

    /**
     * Lấy thông tin user theo ID (public endpoint)
     */
    suspend fun getUserProfile(userId: Long): ApiResponse<UserProfileResponse> {
        val token = session.getToken()
        return safeAuthApiCall(token) {
            api.getUserProfile(userId)
        }
    }

    /**
     * Lấy thông tin user hiện tại
     */
    suspend fun getCurrentUser(): ApiResponse<UserProfileResponse> {
        val token = session.getToken()
        return safeAuthApiCall(token) {
            api.getCurrentUser()
        }
    }

    /**
     * Cập nhật thông tin user hiện tại
     */
    suspend fun updateProfile(request: com.codewithngoc.instagallery.data.model.UpdateUserProfileRequest): ApiResponse<UserProfileResponse> {
        val token = session.getToken()
        return safeAuthApiCall(token) {
            api.updateProfile(request)
        }
    }

    /**
     * Lấy followers (chỉ cần totalRecords từ meta)
     */
    suspend fun getFollowers(userId: Long): ApiResponse<PaginatedFollowsResponse> {
        return safeApiCall {
            api.getFollowers(userId, page = 1, limit = 1)
        }
    }

    /**
     * Lấy following (chỉ cần totalRecords từ meta)
     */
    suspend fun getFollowing(userId: Long): ApiResponse<PaginatedFollowsResponse> {
        return safeApiCall {
            api.getFollowing(userId, page = 1, limit = 1)
        }
    }

    /**
     * Toggle follow/unfollow user
     */
    suspend fun toggleFollow(userId: Long): ApiResponse<Map<String, Boolean>> {
        return safeApiCall {
            api.toggleFollow(userId)
        }
    }
}
