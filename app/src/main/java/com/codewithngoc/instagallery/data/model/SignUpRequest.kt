package com.codewithngoc.instagallery.data.model

data class SignUpRequest(
    val username: String,
    val fullName: String,
    val email: String,
    val password: String,
)