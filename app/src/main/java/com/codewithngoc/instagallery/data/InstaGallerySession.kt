package com.codewithngoc.instagallery.data

import android.content.Context
import android.content.SharedPreferences

// Lưu token để xác thực với
class InstaGallerySession(val context : Context) {
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
}