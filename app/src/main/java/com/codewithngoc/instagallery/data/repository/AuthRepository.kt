package com.codewithngoc.instagallery.data.repository

import com.codewithngoc.instagallery.data.InstaGalleryApi
import com.codewithngoc.instagallery.data.model.AuthResponse
import com.codewithngoc.instagallery.data.model.SignInRequest
import com.codewithngoc.instagallery.data.model.SignUpRequest
import com.codewithngoc.instagallery.data.remote.ApiResponse
import com.codewithngoc.instagallery.data.remote.safeApiCall
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val api: InstaGalleryApi
) {
    suspend fun signIn(request: SignInRequest) : ApiResponse<AuthResponse> {
        return safeApiCall { api.signIn(request) }
    }

    suspend fun signup(request: SignUpRequest) : ApiResponse<AuthResponse> {
        return safeApiCall { api.signUp(request) }
    }
}