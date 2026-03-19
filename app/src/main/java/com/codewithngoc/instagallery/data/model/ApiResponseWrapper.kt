package com.codewithngoc.instagallery.data.model

import com.google.gson.annotations.SerializedName

/**
 * Wrapper cho response từ backend Ktor mới.
 * Backend trả response dạng: { "status": "SUCCESS", "data": ..., "error": ..., "message": ... }
 */
data class ApiResponseWrapper<T>(
    @SerializedName("status") val status: String,
    @SerializedName("data") val data: T? = null,
    @SerializedName("error") val error: ErrorDetails? = null,
    @SerializedName("message") val message: String? = null
) {
    val isSuccess: Boolean get() = status == "SUCCESS"
}

data class ErrorDetails(
    @SerializedName("code") val code: String,
    @SerializedName("message") val message: String,
    @SerializedName("details") val details: List<String>? = null
)
