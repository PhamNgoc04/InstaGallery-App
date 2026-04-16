package com.codewithngoc.instagallery.data.repository

import com.codewithngoc.instagallery.data.InstaGalleryApi
import com.codewithngoc.instagallery.data.model.*
import com.codewithngoc.instagallery.data.remote.ApiResponse
import com.codewithngoc.instagallery.data.remote.safeApiCall
import javax.inject.Inject

/**
 * Admin Repository — chỉ dành cho users có role ADMIN.
 * Backend sẽ tự kiểm tra role, nếu không đủ quyền sẽ trả 403 Forbidden.
 */
class AdminRepository @Inject constructor(
    private val api: InstaGalleryApi
) {

    // ── Dashboard Stats ────────────────────────────────────────

    suspend fun getStats(): ApiResponse<AdminStatsResponse> =
        safeApiCall { api.getAdminStats() }

    suspend fun getGrowthStats(period: String = "30d"): ApiResponse<AdminGrowthResponse> =
        safeApiCall { api.getAdminGrowth(period) }

    // ── User Moderation ────────────────────────────────────────

    /**
     * Ban hoặc Unban một user
     * @param isActive true = unban, false = ban
     */
    suspend fun setBanStatus(
        userId: Long,
        isActive: Boolean,
        reason: String? = null
    ): ApiResponse<Unit> =
        safeApiCall { api.banUser(userId, BanUserRequest(isActive, reason)) }

    // ── Content Moderation ─────────────────────────────────────

    suspend fun deletePost(postId: Long): ApiResponse<Unit> =
        safeApiCall { api.adminDeletePost(postId) }

    suspend fun deleteComment(commentId: Long): ApiResponse<Unit> =
        safeApiCall { api.adminDeleteComment(commentId) }

    // ── Reports ────────────────────────────────────────────────

    suspend fun getReports(): ApiResponse<List<AdminReportResponse>> =
        safeApiCall { api.getAdminReports() }

    /**
     * Xử lý báo cáo vi phạm
     * @param action "BAN" | "DISMISS" | "DELETE"
     */
    suspend fun resolveReport(reportId: Long, action: String): ApiResponse<AdminReportResponse> =
        safeApiCall { api.resolveReport(reportId, ResolveReportRequest(action)) }
}
