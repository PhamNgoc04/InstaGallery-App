package com.codewithngoc.instagallery.data

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Lưu trữ session tokens và thông tin user.
 * Cập nhật cho backend Ktor mới: userId dạng Long, lưu thêm refreshToken, username.
 */
@Singleton
class InstaGallerySession @Inject constructor(
    @ApplicationContext context: Context
) {
    val sharedPres: SharedPreferences =
        context.getSharedPreferences("insta_gallery_session", Context.MODE_PRIVATE)

    // ========== Token ==========

    fun storeToken(token: String) {
        sharedPres.edit().putString("token", token).apply()
    }

    fun getToken(): String? {
        return sharedPres.getString("token", null)
    }

    fun storeRefreshToken(refreshToken: String) {
        sharedPres.edit().putString("refreshToken", refreshToken).apply()
    }

    fun getRefreshToken(): String? {
        return sharedPres.getString("refreshToken", null)
    }

    // ========== User Info ==========

    fun storeUserId(userId: Long) {
        sharedPres.edit().putLong("userId", userId).apply()
    }

    fun getUserId(): Long {
        return sharedPres.getLong("userId", -1L)
    }

    fun storeUsername(username: String) {
        sharedPres.edit().putString("username", username).apply()
    }

    fun getUsername(): String? {
        return sharedPres.getString("username", null)
    }

    // ========== Clear ==========

    fun clearToken() {
        clearTokens()
    }

    fun clearTokens() {
        sharedPres.edit()
            .remove("token")
            .remove("userId")
            .remove("refreshToken")
            .remove("username")
            .apply()
    }
}