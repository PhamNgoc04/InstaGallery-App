package com.codewithngoc.instagallery.data.model

import kotlinx.serialization.Serializable

@Serializable
data class CommentResponse(
    val commentId: Int,
    val postId: Int,
    val author: AuthorInfoResponse,
    val content: String,
    val parentCommentId: Int? = null,
    val createdAt: String,
)

@Serializable
data class AddCommentRequest(
    val content: String,
    val parentCommentId: Int? = null
)