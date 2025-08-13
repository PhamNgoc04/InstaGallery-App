package com.codewithngoc.instagallery.data.repository

import com.codewithngoc.instagallery.data.InstaGalleryApi
import com.codewithngoc.instagallery.data.model.AddCommentRequest
import com.codewithngoc.instagallery.data.model.CommentResponse
import com.codewithngoc.instagallery.data.remote.ApiResponse
import com.codewithngoc.instagallery.data.remote.safeApiCall
import javax.inject.Inject

class CommentRepository @Inject constructor(
    private val api: InstaGalleryApi
) {
    suspend fun getCommentsForPost(postId: Int, page: Int = 0, size: Int = 20): ApiResponse<List<CommentResponse>> {
        return safeApiCall { api.getCommentsForPost(postId, page, size) }
    }

    suspend fun addComment(postId: Int, request: AddCommentRequest): ApiResponse<CommentResponse> {
        return safeApiCall { api.addComment(postId, request) }
    }
}
