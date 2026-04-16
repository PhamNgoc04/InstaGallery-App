package com.codewithngoc.instagallery.data.repository

import com.codewithngoc.instagallery.data.InstaGalleryApi
import com.codewithngoc.instagallery.data.model.*
import com.codewithngoc.instagallery.data.remote.ApiResponse
import com.codewithngoc.instagallery.data.remote.safeApiCall
import javax.inject.Inject

class PostRepository @Inject constructor(
    private val api: InstaGalleryApi
) {

    // ── Feed & Post CRUD ───────────────────────────────────────

    suspend fun getFeed(page: Int = 1, limit: Int = 10): ApiResponse<PaginatedFeedResponse> =
        safeApiCall { api.getFeed(page, limit) }

    suspend fun getUserPosts(userId: Long, page: Int = 1, limit: Int = 20): ApiResponse<List<FeedPostResponse>> {
        return when (val res = safeApiCall { api.getUserPosts(userId, page, limit) }) {
            is ApiResponse.Success -> ApiResponse.Success(res.data.posts)
            is ApiResponse.Error -> ApiResponse.Error(res.code, res.message)
            is ApiResponse.Exception -> ApiResponse.Exception(res.exception)
        }
    }

    suspend fun getPostDetail(postId: Long): ApiResponse<FeedPostResponse> =
        safeApiCall { api.getPostDetail(postId) }

    suspend fun createPost(request: CreatePostRequest): ApiResponse<PostResponse> =
        safeApiCall { api.createPost(request) }

    suspend fun updatePost(postId: Long, request: UpdatePostRequest): ApiResponse<Unit> =
        safeApiCall { api.updatePost(postId, request) }

    suspend fun deletePost(postId: Long): ApiResponse<Unit> =
        safeApiCall { api.deletePost(postId) }

    // ── Like & Save ────────────────────────────────────────────

    suspend fun toggleLike(postId: Long): ApiResponse<ToggleLikeResponse> =
        safeApiCall { api.toggleLike(postId) }

    suspend fun toggleSave(postId: Long): ApiResponse<ToggleSaveResponse> =
        safeApiCall { api.toggleSave(postId) }

    // ── Comments ───────────────────────────────────────────────

    suspend fun getComments(
        postId: Long,
        page: Int = 1,
        limit: Int = 20
    ): ApiResponse<PaginatedCommentsResponse> =
        safeApiCall { api.getCommentsForPost(postId, page, limit) }

    suspend fun addComment(postId: Long, content: String): ApiResponse<CommentResponse> =
        safeApiCall { api.addComment(postId, AddCommentRequest(content)) }

    suspend fun updateComment(commentId: Long, content: String): ApiResponse<CommentResponse> =
        safeApiCall { api.updateComment(commentId, AddCommentRequest(content)) }

    suspend fun deleteComment(commentId: Long): ApiResponse<Unit> =
        safeApiCall { api.deleteComment(commentId) }

    suspend fun toggleCommentLike(commentId: Long): ApiResponse<ToggleLikeResponse> =
        safeApiCall { api.toggleCommentLike(commentId) }

    // ── Media Management ───────────────────────────────────────

    suspend fun getPresignedUrl(request: PresignedUrlRequest): ApiResponse<PresignedUrlResponse> =
        safeApiCall { api.getPresignedUrl(request) }

    suspend fun addMediaToPost(
        postId: Long,
        request: AddMediaToPostRequest
    ): ApiResponse<MediaItemResponse> =
        safeApiCall { api.addMediaToPost(postId, request) }

    suspend fun deleteMedia(mediaId: Long): ApiResponse<Unit> =
        safeApiCall { api.deleteMedia(mediaId) }

    suspend fun reorderMedia(postId: Long, orderedIds: List<Long>): ApiResponse<Unit> =
        safeApiCall { api.reorderMedia(postId, ReorderMediaRequest(orderedIds)) }
}