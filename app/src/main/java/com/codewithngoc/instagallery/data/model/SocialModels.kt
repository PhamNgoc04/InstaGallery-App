package com.codewithngoc.instagallery.data.model

import com.google.gson.annotations.SerializedName

// ==================== SESSIONS ====================

/**
 * Thông tin một phiên đăng nhập (thiết bị) - khớp với SessionDto backend
 */
data class SessionResponse(
    @SerializedName("id") val id: Long = 0,
    @SerializedName("deviceName") val deviceName: String? = null,
    @SerializedName("deviceType") val deviceType: String? = null, // ANDROID, IOS, WEB
    @SerializedName("ipAddress") val ipAddress: String? = null,
    @SerializedName("userAgent") val userAgent: String? = null,
    @SerializedName("isCurrentSession") val isCurrentSession: Boolean = false,
    @SerializedName("lastActiveAt") val lastActiveAt: String? = null,
    @SerializedName("createdAt") val createdAt: String? = null
)

// ==================== FOLLOW REQUESTS ====================

/**
 * Yêu cầu theo dõi đang chờ duyệt - khớp với FollowRequestDto backend
 */
data class FollowRequestResponse(
    @SerializedName("id") val id: Long = 0,
    @SerializedName("followerId") val followerId: Long = 0,
    @SerializedName("followerUsername") val followerUsername: String = "",
    @SerializedName("followerFullName") val followerFullName: String = "",
    @SerializedName("followerAvatar") val followerAvatar: String? = null,
    @SerializedName("requestedAt") val requestedAt: String? = null
)

// ==================== BLOCK / MUTE ====================

/**
 * Response khi block hoặc mute user
 */
data class BlockMuteResponse(
    @SerializedName("userId") val userId: Long = 0,
    @SerializedName("isBlocked") val isBlocked: Boolean? = null,
    @SerializedName("isMuted") val isMuted: Boolean? = null,
    @SerializedName("message") val message: String? = null
)
