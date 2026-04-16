package com.codewithngoc.instagallery.data.remote

import com.codewithngoc.instagallery.data.InstaGalleryApi
import com.codewithngoc.instagallery.data.InstaGallerySession
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeoutOrNull
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
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

    // ✅ FIX #5: Mutex ngăn nhiều request cùng refresh token cùng lúc (thundering herd)
    private val refreshMutex = Mutex()

    override fun authenticate(route: Route?, response: Response): Request? {
        // Tránh vòng lặp vô hạn nếu refresh token cũng bị lỗi 401
        if (response.request.url.encodedPath.contains("/auth/refresh")) {
            session.clearTokens()
            return null
        }

        val refreshToken = session.getRefreshToken() ?: return null

        // ✅ FIX: withTimeoutOrNull(10_000) → tối đa 10 giây, tránh ANR
        return runBlocking {
            refreshMutex.withLock {
                // Double-check: nếu token đã được refresh bởi coroutine khác
                val currentToken = session.getToken()
                if (!currentToken.isNullOrEmpty() && currentToken != response.request.header("Authorization")?.removePrefix("Bearer ")) {
                    return@runBlocking response.request.newBuilder()
                        .header("Authorization", "Bearer $currentToken")
                        .build()
                }

                var newAccessToken: String? = null
                withTimeoutOrNull(10_000L) {
                    try {
                        val api = apiProvider.get()
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
                        // Mất mạng hoặc timeout
                    }
                }

                if (newAccessToken != null) {
                    response.request.newBuilder()
                        .header("Authorization", "Bearer $newAccessToken")
                        .build()
                } else {
                    null
                }
            }
        }
    }
}

