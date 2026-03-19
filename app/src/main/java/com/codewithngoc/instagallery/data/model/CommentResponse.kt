package com.codewithngoc.instagallery.data.model

import com.google.gson.annotations.SerializedName

/**
 * Comment - khớp với CommentDto của backend Ktor mới
 */
data class CommentResponse(
    @SerializedName("commentId") val commentId: Long,
    @SerializedName("postId") val postId: Long,
    @SerializedName("userId") val userId: Long,
    @SerializedName("username") val username: String,
    @SerializedName("avatar") val avatar: String?,
    @SerializedName("content") val content: String,
    @SerializedName("parentId") val parentId: Long? = null,
    @SerializedName("replyCount") val replyCount: Int = 0,
    @SerializedName("createdAt") val createdAt: String,
)

/**
 * Request tạo comment - khớp với CreateCommentRequest
 */
data class AddCommentRequest(
    @SerializedName("content") val content: String,
    @SerializedName("parentId") val parentId: Long? = null
)