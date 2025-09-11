package com.codewithngoc.instagallery.data.model

import kotlinx.serialization.Serializable

@Serializable
data class LikeResponse (
    val userId: Int,
    val username: String,
    val fullName: String,
    val profilePictureUrl: String?
)