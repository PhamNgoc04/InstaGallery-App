package com.codewithngoc.instagallery.data.model

import com.google.gson.annotations.SerializedName

/**
 * Response cho toggle like - khớp với ToggleLikeResponse của backend Ktor mới
 * Backend dùng 1 endpoint POST /{id}/like để toggle (like/unlike)
 */
data class ToggleLikeResponse(
    @SerializedName("isLiked") val isLiked: Boolean,
    @SerializedName("totalLikes") val totalLikes: Int
)

/**
 * Response cho toggle save/bookmark
 */
data class ToggleSaveResponse(
    @SerializedName("isSaved") val isSaved: Boolean
)
