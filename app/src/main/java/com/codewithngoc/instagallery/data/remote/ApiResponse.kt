package com.codewithngoc.instagallery.data.remote

import retrofit2.Response

// Lớp sealed đại diện cho các trạng thái API (success, error, exception)
sealed class ApiResponse<out T> {
    // API thành công (HTTP 200)
    data class Success<out T>(val data: T) : ApiResponse<T>()

    // API trả về lỗi (HTTP 400, 401, 500,...)
    data class Error(val code: Int, val message: String) : ApiResponse<Nothing>() {
        fun formatMsg() : String {
            return "Error $code: $message"
        }
    }

    // API không thành công do ngoại lệ (ví dụ: không kết nối mạng, timeout,...)
    data class Exception(val exception: kotlin.Exception) : ApiResponse<Nothing>()
}

// Hàm mở rộng để xử lý các API
suspend fun <T> safeApiCall(apiCall: suspend () -> Response<T>) : ApiResponse<T> {
    return try {
        val res = apiCall.invoke()
        if (res.isSuccessful) {
            res.body()?.let { ApiResponse.Success(it) }
                ?: ApiResponse.Error(res.code(), "API call successful but returned null body")
        } else {
            ApiResponse.Error(res.code(), res.errorBody()?.string() ?: "Unknown error")
        }
    } catch (e: Exception) {
        ApiResponse.Exception(e)
    }
}