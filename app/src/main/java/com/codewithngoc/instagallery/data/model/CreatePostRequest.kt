package com.codewithngoc.instagallery.data.model

import com.google.gson.annotations.SerializedName

/**
 * Request tạo bài đăng - khớp với CreatePostRequest của backend Ktor mới
 */
data class CreatePostRequest(
    @SerializedName("caption") val caption: String?,
    @SerializedName("location") val location: String? = null,
    @SerializedName("visibility") val visibility: PostVisibility = PostVisibility.PUBLIC,
    @SerializedName("media") val media: List<CreateMediaItemRequest>,
    @SerializedName("tags") val tags: List<String>? = null
)

/**
 * Media item trong CreatePostRequest - khớp với CreateMediaItemRequest
 */
data class CreateMediaItemRequest(
    @SerializedName("mediaFileUrl") val mediaFileUrl: String,
    @SerializedName("thumbnailUrl") val thumbnailUrl: String? = null,
    @SerializedName("mediaType") val mediaType: MediaType = MediaType.IMAGE,
    @SerializedName("width") val width: Int? = null,
    @SerializedName("height") val height: Int? = null,
    @SerializedName("duration") val duration: Int? = null
)

/**
 * Request cập nhật bài đăng
 */
data class UpdatePostRequest(
    @SerializedName("caption") val caption: String? = null,
    @SerializedName("location") val location: String? = null,
    @SerializedName("visibility") val visibility: PostVisibility? = null,
    @SerializedName("tags") val tags: List<String>? = null
)

/**
 * Request presigned URL cho upload media
 */
data class PresignedUrlRequest(
    @SerializedName("fileName") val fileName: String,
    @SerializedName("contentType") val contentType: String,
    @SerializedName("fileSize") val fileSize: Long
)

/**
 * Response presigned URL
 */
data class PresignedUrlResponse(
    @SerializedName("uploadUrl") val uploadUrl: String,
    @SerializedName("fileUrl") val fileUrl: String,
    @SerializedName("expiresAt") val expiresAt: Long
)

enum class PostVisibility { PUBLIC, PRIVATE, FRIENDS_ONLY }

enum class MediaType { IMAGE, VIDEO }
