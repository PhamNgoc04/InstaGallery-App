package com.codewithngoc.instagallery.data.repository

import com.codewithngoc.instagallery.data.InstaGalleryApi
import com.codewithngoc.instagallery.data.model.*
import com.codewithngoc.instagallery.data.remote.ApiResponse
import com.codewithngoc.instagallery.data.remote.safeApiCall
import javax.inject.Inject

/**
 * Quản lý Portfolio của Nhiếp Ảnh Gia và lịch rảnh (Availability)
 */
class PortfolioRepository @Inject constructor(
    private val api: InstaGalleryApi
) {

    // ── Public Discovery ───────────────────────────────────────

    /**
     * Tìm kiếm nhiếp ảnh gia theo địa điểm, chuyên môn, giá
     */
    suspend fun getPortfolios(
        location: String? = null,
        specialty: String? = null,
        page: Int = 1,
        limit: Int = 20
    ): ApiResponse<PaginatedPortfoliosResponse> =
        safeApiCall { api.getPortfolios(location, specialty, page, limit) }

    /**
     * Xem hồ sơ công khai của một nhiếp ảnh gia theo userId
     */
    suspend fun getUserPortfolio(userId: Long): ApiResponse<PortfolioResponse> =
        safeApiCall { api.getUserPortfolio(userId) }

    // ── Photographer Self Management ───────────────────────────

    suspend fun getMyPortfolio(): ApiResponse<PortfolioResponse> =
        safeApiCall { api.getMyPortfolio() }

    suspend fun updateMyPortfolio(request: UpdatePortfolioRequest): ApiResponse<PortfolioResponse> =
        safeApiCall { api.updateMyPortfolio(request) }

    // ── Availability (Lịch rảnh) ───────────────────────────────

    suspend fun updateAvailability(slots: List<AvailabilitySlot>): ApiResponse<PortfolioAvailabilityResponse> =
        safeApiCall { api.updateAvailability(PortfolioAvailabilityRequest(slots)) }

    suspend fun getMyAvailability(): ApiResponse<PortfolioAvailabilityResponse> =
        safeApiCall { api.getMyAvailability() }
}
