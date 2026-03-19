package com.codewithngoc.instagallery.data.model

import com.google.gson.annotations.SerializedName

/**
 * Bài đăng trong Feed - khớp với FeedPostDto của backend Ktor mới
 */
data class FeedPostResponse(
    @SerializedName("postId") val postId: Long,
    @SerializedName("userId") val userId: Long,
    @SerializedName("username") val username: String,
    @SerializedName("userAvatar") val userAvatar: String?,
    @SerializedName("caption") val caption: String?,
    @SerializedName("location") val location: String?,
    @SerializedName("likeCount") val likeCount: Int,
    @SerializedName("commentCount") val commentCount: Int,
    @SerializedName("createdAt") val createdAt: String,
    @SerializedName("media") val media: List<FeedMediaResponse>
)

/**
 * Bài đăng cơ bản - khớp với PostDto
 */
data class PostResponse(
    @SerializedName("postId") val postId: Long,
    @SerializedName("userId") val userId: Long,
    @SerializedName("caption") val caption: String?,
    @SerializedName("location") val location: String?,
    @SerializedName("visibility") val visibility: PostVisibility,
    @SerializedName("likeCount") val likeCount: Int,
    @SerializedName("commentCount") val commentCount: Int,
    @SerializedName("createdAt") val createdAt: String,
    @SerializedName("media") val media: List<FeedMediaResponse> = emptyList()
)

/**
 * Media trong bài đăng - khớp với FeedMediaDto
 */
data class FeedMediaResponse(
    @SerializedName("id") val id: Long,
    @SerializedName("url") val url: String,
    @SerializedName("type") val type: String,
    @SerializedName("orderIndex") val orderIndex: Int
)