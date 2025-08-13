package com.codewithngoc.instagallery.data.model

import kotlinx.serialization.Serializable

@Serializable
data class SignUpRequest(
    val username: String,
    val fullName: String,
    val email: String,
    val password: String,
)