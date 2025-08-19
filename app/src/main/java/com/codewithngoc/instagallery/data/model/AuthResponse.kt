package com.codewithngoc.instagallery.data.model

import kotlinx.serialization.Serializable

@Serializable
data class AuthResponse (
    val userId: String,
    val token: String
)