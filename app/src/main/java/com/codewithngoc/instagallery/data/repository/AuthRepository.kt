package com.codewithngoc.instagallery.data.repository

import com.codewithngoc.instagallery.data.InstaGalleryApi
import com.codewithngoc.instagallery.data.InstaGallerySession
import com.codewithngoc.instagallery.data.model.*
import com.codewithngoc.instagallery.data.remote.ApiResponse
import com.codewithngoc.instagallery.data.remote.safeApiCall
import com.codewithngoc.instagallery.data.remote.safeAuthApiCall
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val api: InstaGalleryApi,
    private val session: InstaGallerySession
) {

    // ── Standard Auth ──────────────────────────────────────────

    suspend fun signIn(request: SignInRequest): ApiResponse<LoginResponse> =
        safeApiCall { api.signIn(request) }

    suspend fun signup(request: SignUpRequest): ApiResponse<RegisterResponse> =
        safeApiCall { api.signUp(request) }

    suspend fun logout(refreshToken: String): ApiResponse<Unit> =
        safeApiCall { api.logout(refreshToken) }

    suspend fun refreshToken(refreshToken: String): ApiResponse<TokenRefreshResponse> =
        safeApiCall { api.refreshToken(refreshToken) }

    suspend fun forgotPassword(email: String): ApiResponse<Unit> =
        safeApiCall { api.forgotPassword(ForgotPasswordRequest(email)) }

    suspend fun resetPassword(otp: String, newPassword: String): ApiResponse<Unit> =
        safeApiCall { api.resetPassword(ResetPasswordRequest(otp, newPassword)) }

    suspend fun googleLogin(googleTokenId: String): ApiResponse<LoginResponse> =
        safeApiCall { api.googleLogin(GoogleLoginRequest(googleTokenId)) }

    suspend fun changePassword(request: ChangePasswordRequest): ApiResponse<Unit> {
        val token = session.getToken()
        return safeAuthApiCall(token) { api.changePassword(request) }
    }

    // ── 2FA ────────────────────────────────────────────────────

    /**
     * Bước 1: Lấy QR Code + Secret để cài G-Auth
     */
    suspend fun setup2FA(): ApiResponse<TwoFaSetupResponse> {
        val token = session.getToken()
        return safeAuthApiCall(token) { api.setup2FA() }
    }

    /**
     * Bước 2: Kích hoạt 2FA bằng mã OTP từ G-Auth
     */
    suspend fun enable2FA(otpCode: String): ApiResponse<TwoFaEnableResponse> {
        val token = session.getToken()
        return safeAuthApiCall(token) { api.enable2FA(TwoFaVerifyRequest(otpCode)) }
    }

    /**
     * Đăng nhập với tài khoản bật 2FA — xác thực mã OTP
     */
    suspend fun verify2FALogin(userId: Long, otpCode: String): ApiResponse<LoginResponse> =
        safeApiCall { api.verify2FALogin(TwoFaLoginVerifyRequest(userId, otpCode)) }
}