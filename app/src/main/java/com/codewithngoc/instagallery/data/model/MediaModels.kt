package com.codewithngoc.instagallery.data.model

import com.google.gson.annotations.SerializedName

// ==================== MEDIA MANAGEMENT ====================

/**
 * Request thêm một file media vào bài đăng đã tồn tại
 */
data class AddMediaToPostRequest(
    @SerializedName("mediaFileUrl") val mediaFileUrl: String,
    @SerializedName("thumbnailUrl") val thumbnailUrl: String? = null,
    @SerializedName("mediaType") val mediaType: String = "IMAGE", // "IMAGE" | "VIDEO"
    @SerializedName("width") val width: Int? = null,
    @SerializedName("height") val height: Int? = null,
    @SerializedName("duration") val duration: Int? = null // giây, dành cho video
)

/**
 * Request sắp xếp lại thứ tự media trong bài đăng
 * orderedMediaIds: danh sách ID media theo thứ tự mới
 */
data class ReorderMediaRequest(
    @SerializedName("orderedMediaIds") val orderedMediaIds: List<Long>
)

/**
 * Response sau khi thêm media vào bài đăng — khớp với MediaDto backend
 */
data class MediaItemResponse(
    @SerializedName("id") val id: Long = 0,
    @SerializedName("postId") val postId: Long = 0,
    @SerializedName("mediaFileUrl") val mediaFileUrl: String = "",
    @SerializedName("thumbnailUrl") val thumbnailUrl: String? = null,
    @SerializedName("mediaType") val mediaType: String = "IMAGE",
    @SerializedName("orderIndex") val orderIndex: Int = 0,
    @SerializedName("width") val width: Int? = null,
    @SerializedName("height") val height: Int? = null,
    @SerializedName("duration") val duration: Int? = null
)
