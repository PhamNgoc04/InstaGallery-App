package com.codewithngoc.instagallery.data.repository

import com.codewithngoc.instagallery.data.InstaGalleryApi
import com.codewithngoc.instagallery.data.model.*
import com.codewithngoc.instagallery.data.remote.ApiResponse
import com.codewithngoc.instagallery.data.remote.safeApiCall
import javax.inject.Inject

/**
 * Quản lý đánh giá (Rating/Review) dành cho Nhiếp Ảnh Gia
 */
class RatingRepository @Inject constructor(
    private val api: InstaGalleryApi
) {

    /**
     * Lấy danh sách đánh giá của một nhiếp ảnh gia — public endpoint
     */
    suspend fun getPhotographerRatings(photographerId: Long): ApiResponse<PhotographerRatingsResponse> =
        safeApiCall { api.getPhotographerRatings(photographerId) }

    /**
     * Gửi đánh giá sau khi hoàn tất Booking
     * Yêu cầu bookingId + score (1-5) + comment tuỳ chọn
     */
    suspend fun createRating(request: CreateRatingRequest): ApiResponse<RatingResponse> =
        safeApiCall { api.createRating(request) }

    /**
     * Xoá đánh giá đã gửi (của chính mình)
     */
    suspend fun deleteRating(ratingId: Long): ApiResponse<Unit> =
        safeApiCall { api.deleteRating(ratingId) }
}
