package com.codewithngoc.instagallery.data.model

import com.google.gson.annotations.SerializedName

// ==================== ALBUMS ====================

/**
 * Thông tin một Album (thư mục ảnh) - khớp với AlbumDto backend
 */
data class AlbumResponse(
    @SerializedName("id") val id: Long = 0,
    @SerializedName("userId") val userId: Long = 0,
    @SerializedName("title") val title: String = "",
    @SerializedName("description") val description: String? = null,
    @SerializedName("coverImageUrl") val coverImageUrl: String? = null,
    @SerializedName("isPrivate") val isPrivate: Boolean = false,
    @SerializedName("mediaCount") val mediaCount: Int = 0,
    @SerializedName("createdAt") val createdAt: String? = null,
    @SerializedName("updatedAt") val updatedAt: String? = null
)

/**
 * Request tạo Album mới
 */
data class CreateAlbumRequest(
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String? = null,
    @SerializedName("isPrivate") val isPrivate: Boolean = false
)

/**
 * Request cập nhật Album
 */
data class UpdateAlbumRequest(
    @SerializedName("title") val title: String? = null,
    @SerializedName("description") val description: String? = null,
    @SerializedName("coverImageUrl") val coverImageUrl: String? = null,
    @SerializedName("isPrivate") val isPrivate: Boolean? = null
)

/**
 * Request thêm Post vào Album
 */
data class AddPostToAlbumRequest(
    @SerializedName("postId") val postId: Long
)

/**
 * Chi tiết Album bao gồm danh sách bài đăng bên trong
 */
data class AlbumDetailResponse(
    @SerializedName("id") val id: Long = 0,
    @SerializedName("userId") val userId: Long = 0,
    @SerializedName("title") val title: String = "",
    @SerializedName("description") val description: String? = null,
    @SerializedName("coverImageUrl") val coverImageUrl: String? = null,
    @SerializedName("isPrivate") val isPrivate: Boolean = false,
    @SerializedName("posts") val posts: List<FeedPostResponse> = emptyList(),
    @SerializedName("mediaCount") val mediaCount: Int = 0,
    @SerializedName("createdAt") val createdAt: String? = null,
    @SerializedName("updatedAt") val updatedAt: String? = null
)
