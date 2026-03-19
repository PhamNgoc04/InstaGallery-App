package com.codewithngoc.instagallery.data.remote

import com.codewithngoc.instagallery.data.model.ApiResponseWrapper
import retrofit2.Response

// Lớp sealed đại diện cho các trạng thái API (success, error, exception)
sealed class ApiResponse<out T> {
    // API thành công
    data class Success<out T>(val data: T) : ApiResponse<T>()

    // API trả về lỗi (HTTP 400, 401, 500,...)
    data class Error(val code: Int, val message: String) : ApiResponse<Nothing>() {
        fun formatMsg(): String {
            return "Error $code: $message"
        }
    }

    // API không thành công do ngoại lệ (ví dụ: không kết nối mạng, timeout,...)
    data class Exception(val exception: kotlin.Exception) : ApiResponse<Nothing>()
}

/**
 * Gọi API an toàn với response được wrap trong ApiResponseWrapper.
 * Tự động unwrap `data` field từ ApiResponseWrapper trước khi trả về.
 */
suspend fun <T> safeApiCall(apiCall: suspend () -> Response<ApiResponseWrapper<T>>): ApiResponse<T> {
    return try {
        val res = apiCall.invoke()
        if (res.isSuccessful) {
            val wrapper = res.body()
            if (wrapper != null && wrapper.isSuccess && wrapper.data != null) {
                ApiResponse.Success(wrapper.data)
            } else if (wrapper != null && !wrapper.isSuccess) {
                val errorMsg = wrapper.error?.message ?: wrapper.message ?: "Unknown error"
                ApiResponse.Error(res.code(), errorMsg)
            } else {
                // Trường hợp data == null nhưng vẫn success (ví dụ: logout, delete)
                // Cần cast, vì T có thể là Unit
                @Suppress("UNCHECKED_CAST")
                ApiResponse.Success(Unit as T)
            }
        } else {
            ApiResponse.Error(res.code(), res.errorBody()?.string() ?: "Unknown error")
        }
    } catch (e: Exception) {
        ApiResponse.Exception(e)
    }
}

/**
 * Gọi API yêu cầu auth - token đã được tự động thêm qua interceptor trong AppModule,
 * nên hàm này chỉ kiểm tra token có tồn tại hay không.
 */
suspend fun <T> safeAuthApiCall(
    token: String?,
    apiCall: suspend () -> Response<ApiResponseWrapper<T>>
): ApiResponse<T> {
    if (token.isNullOrEmpty()) {
        return ApiResponse.Error(401, "User not authenticated")
    }
    return safeApiCall(apiCall)
}
