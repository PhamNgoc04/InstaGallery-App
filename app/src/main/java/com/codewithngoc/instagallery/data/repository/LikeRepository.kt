package com.codewithngoc.instagallery.data.repository

import com.codewithngoc.instagallery.data.InstaGalleryApi
import com.codewithngoc.instagallery.data.model.CheckLikedResponse
import com.codewithngoc.instagallery.data.model.LikeCountResponse
import com.codewithngoc.instagallery.data.model.LikeResponse
import com.codewithngoc.instagallery.data.model.MessageResponse
import com.codewithngoc.instagallery.data.remote.ApiResponse
import com.codewithngoc.instagallery.data.remote.safeApiCall
import javax.inject.Inject

class LikeRepository @Inject constructor(
    private val api: InstaGalleryApi
) {
    // Lấy danh sách người đã like bài viết
    suspend fun getLikes(postId: Int, page: Int, size: Int) : ApiResponse<List<LikeResponse>> {
        return safeApiCall { api.getLikes(postId, page, size) }
    }

    // Kiểm tra user hiện tại đã like chưa
    suspend fun checkLiked(postId: Int) : ApiResponse<CheckLikedResponse> {
        return safeApiCall { api.checkLiked(postId) }
    }

    // Like bài viết
    suspend fun likePost(postId: Int): ApiResponse<LikeCountResponse> {
        return safeApiCall { api.likePost(postId) }
    }

    // Unlike bài viết
    suspend fun unlikePost(postId: Int): ApiResponse<LikeCountResponse> {
        return safeApiCall { api.unlikePost(postId) }
    }

    suspend fun getLikeCount(postId: Int): ApiResponse<Int> {
        return safeApiCall {
            api.getLikeCount(postId)   // ← gọi Retrofit service
        }
    }

}