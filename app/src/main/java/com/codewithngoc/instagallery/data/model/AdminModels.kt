package com.codewithngoc.instagallery.data.model

import com.google.gson.annotations.SerializedName

// ==================== ADMIN STATS ====================

/**
 * Thông số tổng quan hệ thống - khớp với AdminStatsDto backend
 */
data class AdminStatsResponse(
    @SerializedName("totalUsers") val totalUsers: Int = 0,
    @SerializedName("totalPosts") val totalPosts: Int = 0,
    @SerializedName("totalActiveUsers") val totalActiveUsers: Int = 0,
    @SerializedName("totalReports") val totalReports: Int = 0,
    @SerializedName("totalBookings") val totalBookings: Int = 0,
    @SerializedName("newUsersToday") val newUsersToday: Int = 0,
    @SerializedName("newPostsToday") val newPostsToday: Int = 0
)

/**
 * Điểm dữ liệu biểu đồ tăng trưởng theo ngày
 */
data class GrowthDataPoint(
    @SerializedName("date") val date: String = "",
    @SerializedName("newUsers") val newUsers: Int = 0,
    @SerializedName("newPosts") val newPosts: Int = 0
)

/**
 * Response biểu đồ tăng trưởng - khớp với AdminGrowthDto backend
 */
data class AdminGrowthResponse(
    @SerializedName("period") val period: String = "", // "7d", "30d", "90d"
    @SerializedName("data") val data: List<GrowthDataPoint> = emptyList()
)

// ==================== ADMIN REPORTS ====================

/**
 * Một báo cáo vi phạm — khớp với ReportDto backend
 */
data class AdminReportResponse(
    @SerializedName("id") val id: Long = 0,
    @SerializedName("reporterId") val reporterId: Long = 0,
    @SerializedName("reporterUsername") val reporterUsername: String? = null,
    @SerializedName("targetType") val targetType: String = "", // POST, COMMENT, USER
    @SerializedName("targetId") val targetId: Long = 0,
    @SerializedName("reason") val reason: String = "",
    @SerializedName("status") val status: String = "PENDING", // PENDING, RESOLVED, DISMISSED
    @SerializedName("resolvedBy") val resolvedBy: Long? = null,
    @SerializedName("resolvedAction") val resolvedAction: String? = null, // BAN, DISMISS, DELETE
    @SerializedName("createdAt") val createdAt: String? = null,
    @SerializedName("resolvedAt") val resolvedAt: String? = null
)

/**
 * Request xử lý báo cáo vi phạm
 */
data class ResolveReportRequest(
    @SerializedName("action") val action: String // "BAN", "DISMISS", "DELETE"
)

// ==================== ADMIN USER BAN ====================

/**
 * Request ban / unban user
 */
data class BanUserRequest(
    @SerializedName("isActive") val isActive: Boolean, // false = ban, true = unban
    @SerializedName("reason") val reason: String? = null
)
