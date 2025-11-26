package com.codewithngoc.instagallery.data

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

// Lưu token để xác thực với...
@Singleton
class InstaGallerySession @Inject constructor(
    @ApplicationContext context: Context
)  {
    // Tạo đối tượng SharedPreferences với tên là "insta_gallery_session" để lưu trữ token
    // dưới dạng key-value
    val sharedPres : SharedPreferences =
        context.getSharedPreferences(("insta_gallery_session"), Context.MODE_PRIVATE) // MODE_PRIVATE chỉ cho phép ứng dụng này truy cập

    // Lưu token vào SharedPreferences
    // Hàm này sẽ được gọi khi người dùng đăng nhập thành công để lưu token vào
    fun storeToken(token: String) {
        sharedPres.edit().putString("token", token).apply()
    }

    // Lấy token từ SharedPreferences
    fun getToken() : String? {
        sharedPres.getString("token", null)?.let {
            return it
        }
        return null // Nếu không có token thì trả về null
    }

    fun storeUserId(userId: String) {
        sharedPres.edit().putString("userId", userId).apply()
    }

    fun getUserId(): String? {
        return sharedPres.getString("userId", null)
    }

    fun clearToken() {
        sharedPres.edit()
            .remove("token")
            .remove("userId")
            .apply()
    }

    // ✅ Thêm hàm để lưu refresh token
    fun storeRefreshToken(refreshToken: String) {
        sharedPres.edit().putString("refreshToken", refreshToken).apply()
    }

    // ✅ Thêm hàm để lấy refresh token
    fun getRefreshToken(): String? {
        return sharedPres.getString("refreshToken", null)
    }

    // ✅ Cập nhật hàm clearToken để xóa cả token và refreshToken
    fun clearTokens() {
        sharedPres.edit()
            .remove("token")
            .remove("userId")
            .remove("refreshToken")
            .apply()
    }
}