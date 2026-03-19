package com.codewithngoc.instagallery.data.repository

import com.codewithngoc.instagallery.data.InstaGalleryApi
import com.codewithngoc.instagallery.data.model.AddCommentRequest
import com.codewithngoc.instagallery.data.model.CommentResponse
import com.codewithngoc.instagallery.data.model.PaginatedCommentsResponse
import com.codewithngoc.instagallery.data.remote.ApiResponse
import com.codewithngoc.instagallery.data.remote.safeApiCall
import javax.inject.Inject

class CommentRepository @Inject constructor(
    private val api: InstaGalleryApi
) {
    suspend fun getCommentsForPost(postId: Long, page: Int = 1, limit: Int = 20): ApiResponse<PaginatedCommentsResponse> {
        return safeApiCall { api.getCommentsForPost(postId, page, limit) }
    }

    suspend fun addComment(postId: Long, request: AddCommentRequest): ApiResponse<CommentResponse> {
        return safeApiCall { api.addComment(postId, request) }
    }
}
