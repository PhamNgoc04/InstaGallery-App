package com.codewithngoc.instagallery.data

import com.codewithngoc.instagallery.data.model.AuthResponse
import com.codewithngoc.instagallery.data.model.SignInRequest
import com.codewithngoc.instagallery.data.model.SignUpRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface InstaGalleryApi {
    @POST("api/auth/login")
    suspend fun signIn(@Body request: SignInRequest): Response<AuthResponse>

    @POST("api/auth/register")
    suspend fun signUp(@Body request: SignUpRequest): Response<AuthResponse>
}