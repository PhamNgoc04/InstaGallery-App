package com.codewithngoc.instagallery.data.model

data class SignUpRequest(
    val username: String,
    val email: String,
    val password: String,
    val fullName: String
)