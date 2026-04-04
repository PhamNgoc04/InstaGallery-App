package com.codewithngoc.instagallery.data.repository

import com.codewithngoc.instagallery.data.InstaGalleryApi
import com.codewithngoc.instagallery.data.model.CreatePostRequest
import com.codewithngoc.instagallery.data.model.FeedPostResponse
import com.codewithngoc.instagallery.data.model.PaginatedFeedResponse
import com.codewithngoc.instagallery.data.model.PostResponse
import com.codewithngoc.instagallery.data.remote.ApiResponse
import com.codewithngoc.instagallery.data.remote.safeApiCall
import javax.inject.Inject

class PostRepository @Inject constructor(
    private val api: InstaGalleryApi
) {
    // Lấy feed (có phân trang)
    suspend fun getFeed(page: Int = 1, limit: Int = 10): ApiResponse<PaginatedFeedResponse> {
        return safeApiCall { api.getFeed(page, limit) }
    }

    // Tạo bài đăng mới
    suspend fun createPost(request: CreatePostRequest): ApiResponse<PostResponse> {
        return safeApiCall { api.createPost(request) }
    }

    // Xóa bài đăng
    suspend fun deletePost(postId: Long): ApiResponse<Unit> {
        return safeApiCall { api.deletePost(postId) }
    }

    // Xem chi tiết bài đăng
    suspend fun getPostDetail(postId: Long): ApiResponse<FeedPostResponse> {
        return safeApiCall { api.getPostDetail(postId) }
    }
}