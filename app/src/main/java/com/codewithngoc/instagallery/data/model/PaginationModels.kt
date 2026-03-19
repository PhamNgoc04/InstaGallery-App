package com.codewithngoc.instagallery.data.model

import com.google.gson.annotations.SerializedName

/**
 * Model cho phân trang từ backend mới
 */
data class PaginationMeta(
    @SerializedName("currentPage") val currentPage: Int = 1,
    @SerializedName("totalPages") val totalPages: Int = 0,
    @SerializedName("hasNext") val hasNext: Boolean = false
)

data class PaginatedFeedResponse(
    @SerializedName("posts") val posts: List<FeedPostResponse>,
    @SerializedName("meta") val meta: PaginationMeta
)

data class PaginatedCommentsResponse(
    @SerializedName("comments") val comments: List<CommentResponse>,
    @SerializedName("meta") val meta: PaginationMeta
)
