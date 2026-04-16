package com.codewithngoc.instagallery.data.model

import com.google.gson.annotations.SerializedName

// ==================== AUTH EXTRA — RESET PASSWORD ====================

/**
 * Request đặt lại mật khẩu qua OTP email
 */
data class ResetPasswordRequest(
    @SerializedName("otp") val otp: String,
    @SerializedName("newPassword") val newPassword: String
)

// ==================== GOOGLE LOGIN ====================

/**
 * Request đăng nhập Google - gửi Google ID Token lên server
 */
data class GoogleLoginRequest(
    @SerializedName("googleTokenId") val googleTokenId: String
)

// ==================== REFRESH TOKEN ====================

/**
 * Request refresh access token (gửi qua Header X-Refresh-Token, không cần body)
 * Class này dự phòng nếu backend đổi sang body-based
 */
data class RefreshTokenRequest(
    @SerializedName("refreshToken") val refreshToken: String
)

// ==================== 2FA ====================

/**
 * Response khi setup 2FA - trả về QR code và secret để quét G-Auth
 */
data class TwoFaSetupResponse(
    @SerializedName("secret") val secret: String = "",
    @SerializedName("qrCodeUrl") val qrCodeUrl: String = "",
    @SerializedName("backupCodes") val backupCodes: List<String> = emptyList()
)

/**
 * Request xác minh mã OTP để kích hoạt 2FA hoặc đăng nhập với 2FA
 */
data class TwoFaVerifyRequest(
    @SerializedName("otpCode") val otpCode: String
)

/**
 * Request verify-login với 2FA (cần userId + otpCode)
 */
data class TwoFaLoginVerifyRequest(
    @SerializedName("userId") val userId: Long,
    @SerializedName("otpCode") val otpCode: String
)

/**
 * Response khi enable 2FA thành công
 */
data class TwoFaEnableResponse(
    @SerializedName("isEnabled") val isEnabled: Boolean = false,
    @SerializedName("message") val message: String? = null
)
