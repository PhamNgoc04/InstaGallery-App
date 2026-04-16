package com.codewithngoc.instagallery.data.remote

import com.codewithngoc.instagallery.data.InstaGalleryApi
import com.codewithngoc.instagallery.data.InstaGallerySession
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject
import javax.inject.Provider

class TokenAuthenticator @Inject constructor(
    private val session: InstaGallerySession,
    // Provide lazy via Provider to avoid circular dependency in Dagger Hilt
    private val apiProvider: Provider<InstaGalleryApi>
) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        // Tránh vòng lặp vô hạn nếu refresh token cũng bị lỗi 401
        if (response.request.url.encodedPath.contains("/auth/refresh")) {
            session.clearTokens()
            return null
        }

        val refreshToken = session.getRefreshToken() ?: return null

        return synchronized(this) {
            val api = apiProvider.get()
            
            // Lấy token mới qua API
            var newAccessToken: String? = null
            runBlocking {
                try {
                    val refreshResponse = api.refreshToken(refreshToken)
                    if (refreshResponse.isSuccessful && refreshResponse.body() != null) {
                        val newTokens = refreshResponse.body()!!.data
                        if (newTokens != null) {
                            newAccessToken = newTokens.token
                            session.storeToken(newTokens.token)
                            if (newTokens.refreshToken != null) {
                                session.storeRefreshToken(newTokens.refreshToken)
                            }
                        } else {
                            session.clearTokens()
                        }
                    } else {
                        session.clearTokens()
                    }
                } catch (e: Exception) {
                    // Mất mạng hoặc API rớt mạng
                }
            }

            if (newAccessToken != null) {
                // Thử lại request cũ với token mới
                response.request.newBuilder()
                    .header("Authorization", "Bearer $newAccessToken")
                    .build()
            } else {
                null
            }
        }
    }
}
