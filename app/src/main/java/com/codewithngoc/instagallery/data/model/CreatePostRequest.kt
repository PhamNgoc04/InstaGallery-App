package com.codewithngoc.instagallery.data.model

import kotlinx.serialization.Serializable

@Serializable
data class CreatePostRequest(
    val caption: String?,
    val visibility: PostVisibility,
    val location: String?,
    val media: List<MediaItem>
)

@Serializable
data class MediaItem(
    val mediaFileUrl: String,
    val thumbnailUrl: String? = null,
    val mediaType: MediaType,
    val position: Int,
    val filterId: Int? = null,
    val metadata: String? = null
)


enum class PostVisibility { PUBLIC, PRIVATE, FRIENDS_ONLY }

enum class MediaType { IMAGE, VIDEO }
