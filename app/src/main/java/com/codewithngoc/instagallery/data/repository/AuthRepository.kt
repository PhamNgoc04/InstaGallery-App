package com.codewithngoc.instagallery.data.repository

import com.codewithngoc.instagallery.data.InstaGalleryApi
import com.codewithngoc.instagallery.data.model.LoginResponse
import com.codewithngoc.instagallery.data.model.RegisterResponse
import com.codewithngoc.instagallery.data.model.SignInRequest
import com.codewithngoc.instagallery.data.model.SignUpRequest
import com.codewithngoc.instagallery.data.remote.ApiResponse
import com.codewithngoc.instagallery.data.remote.safeApiCall
import javax.inject.Inject

import com.codewithngoc.instagallery.data.model.ChangePasswordRequest
import com.codewithngoc.instagallery.data.remote.safeAuthApiCall
import com.codewithngoc.instagallery.data.InstaGallerySession

class AuthRepository @Inject constructor(
    private val api: InstaGalleryApi,
    private val session: InstaGallerySession
) {
    suspend fun signIn(request: SignInRequest): ApiResponse<LoginResponse> {
        return safeApiCall { api.signIn(request) }
    }

    suspend fun signup(request: SignUpRequest): ApiResponse<RegisterResponse> {
        return safeApiCall { api.signUp(request) }
    }

    suspend fun changePassword(request: ChangePasswordRequest): ApiResponse<Unit> {
        val token = session.getToken()
        return safeAuthApiCall(token) {
            api.changePassword(request)
        }
    }
}