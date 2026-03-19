package com.codewithngoc.instagallery.data.model

import com.google.gson.annotations.SerializedName

/**
 * Response cho đăng nhập - khớp với LoginResponse của backend Ktor mới
 */
data class LoginResponse(
    @SerializedName("userId") val userId: Long,
    @SerializedName("email") val email: String,
    @SerializedName("username") val username: String,
    @SerializedName("role") val role: String,
    @SerializedName("token") val token: String,
    @SerializedName("refreshToken") val refreshToken: String
)

/**
 * Response cho đăng ký - khớp với RegisterResponse của backend Ktor mới
 */
data class RegisterResponse(
    @SerializedName("userId") val userId: Long,
    @SerializedName("email") val email: String,
    @SerializedName("username") val username: String,
    @SerializedName("token") val token: String,
    @SerializedName("refreshToken") val refreshToken: String
)

/**
 * Response cho refresh token
 */
data class TokenRefreshResponse(
    @SerializedName("token") val token: String,
    @SerializedName("refreshToken") val refreshToken: String,
    @SerializedName("expiresAt") val expiresAt: Long
)