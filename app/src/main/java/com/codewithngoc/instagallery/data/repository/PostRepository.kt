package com.codewithngoc.instagallery.data.repository

import com.codewithngoc.instagallery.data.InstaGalleryApi
import com.codewithngoc.instagallery.data.model.PostResponse
import com.codewithngoc.instagallery.data.remote.ApiResponse
import com.codewithngoc.instagallery.data.remote.safeApiCall
import javax.inject.Inject

class PostRepository @Inject constructor(
    private val api: InstaGalleryApi
) {
    // Hàm lấy bài viết theo ID
    suspend fun getPostById(postId: Int): ApiResponse<PostResponse> {
        return safeApiCall { api.getPostById(postId) }
    }

    // Hàm lấy tất cả bài viết
    suspend fun getAllPosts(): ApiResponse<List<PostResponse>> {
        return safeApiCall { api.getAllPosts() }
    }
}