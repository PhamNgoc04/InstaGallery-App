package com.codewithngoc.instagallery.data.model

data class PostResponse (
    val postId: Int,
    val author: AuthorInfoResponse,
    val caption: String?,
    val location: String?,
    val visibility: PostVisibility,
    val media: List<MediaResponse>,
    val likeCount: Int,
    val commentCount: Int
)

data class MediaResponse(
    val mediaId: Int,
    val mediaFileUrl: String,
    val thumbnailUrl: String? = null,
    val mediaType: MediaType,
    val position: Int,
    val filterId: Int? = null,
    val metadata: String? = null
)

data class AuthorInfoResponse(
    val userId: Int,
    val username: String,
    val profilePictureUrl: String? = null
)