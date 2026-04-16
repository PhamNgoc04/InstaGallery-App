package com.codewithngoc.instagallery.data.repository

import com.codewithngoc.instagallery.data.InstaGalleryApi
import com.codewithngoc.instagallery.data.model.*
import com.codewithngoc.instagallery.data.remote.ApiResponse
import com.codewithngoc.instagallery.data.remote.safeApiCall
import javax.inject.Inject

/**
 * Quản lý Booking (đặt lịch chụp ảnh)
 */
class BookingRepository @Inject constructor(
    private val api: InstaGalleryApi
) {

    /**
     * Đặt lịch chụp ảnh với nhiếp ảnh gia
     */
    suspend fun createBooking(request: CreateBookingRequest): ApiResponse<BookingResponse> =
        safeApiCall { api.createBooking(request) }

    /**
     * Lấy danh sách tất cả booking (dành cho cả khách và nhiếp ảnh gia)
     * @param status null = all | "PENDING" | "CONFIRMED" | "IN_PROGRESS" | "COMPLETED" | "CANCELLED"
     */
    suspend fun getBookings(status: String? = null): ApiResponse<PaginatedBookingsResponse> =
        safeApiCall { api.getBookings(status) }

    /**
     * Xem chi tiết một booking (Invoice)
     */
    suspend fun getBookingDetail(bookingId: Long): ApiResponse<BookingResponse> =
        safeApiCall { api.getBookingDetail(bookingId) }

    /**
     * Nhiếp ảnh gia xác nhận hoặc từ chối booking
     * @param status "CONFIRM" | "CANCEL"
     */
    suspend fun updateBookingStatus(bookingId: Long, status: String): ApiResponse<BookingResponse> =
        safeApiCall { api.updateBookingStatus(bookingId, mapOf("status" to status)) }
}
