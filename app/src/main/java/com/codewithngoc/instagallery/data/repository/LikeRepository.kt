package com.codewithngoc.instagallery.data.repository

import com.codewithngoc.instagallery.data.InstaGalleryApi
import com.codewithngoc.instagallery.data.model.ToggleLikeResponse
import com.codewithngoc.instagallery.data.model.ToggleSaveResponse
import com.codewithngoc.instagallery.data.remote.ApiResponse
import com.codewithngoc.instagallery.data.remote.safeApiCall
import javax.inject.Inject

class LikeRepository @Inject constructor(
    private val api: InstaGalleryApi
) {
    /**
     * Toggle like: like nếu chưa like, unlike nếu đã like.
     * Backend trả về isLiked (true/false) và totalLikes.
     */
    suspend fun toggleLike(postId: Long): ApiResponse<ToggleLikeResponse> {
        return safeApiCall { api.toggleLike(postId) }
    }

    /**
     * Toggle save/bookmark bài viết.
     */
    suspend fun toggleSave(postId: Long): ApiResponse<ToggleSaveResponse> {
        return safeApiCall { api.toggleSave(postId) }
    }
}