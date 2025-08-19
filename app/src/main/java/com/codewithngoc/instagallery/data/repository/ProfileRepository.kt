package com.codewithngoc.instagallery.data.repository

import com.codewithngoc.instagallery.data.InstaGalleryApi
import com.codewithngoc.instagallery.data.InstaGallerySession
import com.codewithngoc.instagallery.data.model.AuthResponse
import com.codewithngoc.instagallery.data.model.PostResponse
import com.codewithngoc.instagallery.data.model.UserProfileResponse
import com.codewithngoc.instagallery.data.remote.ApiResponse
import com.codewithngoc.instagallery.data.remote.safeAuthApiCall
import javax.inject.Inject

class ProfileRepository @Inject constructor(
    private val api: InstaGalleryApi,
    private val session: InstaGallerySession
) {

    suspend fun getUserProfile(userId: Int): ApiResponse<UserProfileResponse> {
        val token = session.getToken() ?: return ApiResponse.Error(401, "Token is missing")
        return safeAuthApiCall(token) { authToken ->
            api.getUserProfile(authToken, userId)
        }
    }

    suspend fun getUserPosts(userId: Int): ApiResponse<List<PostResponse>> {
        val token = session.getToken() ?: return ApiResponse.Error(401, "Token is missing")
        return safeAuthApiCall(token) { authToken ->
            api.getUserPosts(authToken, userId)
        }
    }
}
