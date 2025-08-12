package com.codewithngoc.instagallery.data

import com.codewithngoc.instagallery.data.model.AuthResponse
import com.codewithngoc.instagallery.data.model.CreatePostRequest
import com.codewithngoc.instagallery.data.model.PostResponse
import com.codewithngoc.instagallery.data.model.SignInRequest
import com.codewithngoc.instagallery.data.model.SignUpRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface InstaGalleryApi {
    // Đăng nhập
    @POST("api/auth/login")
    suspend fun signIn(@Body request: SignInRequest): Response<AuthResponse>

    // Đăng ký
    @POST("api/auth/register")
    suspend fun signUp(@Body request: SignUpRequest): Response<AuthResponse>

    // Tạo bài đăng
    @POST("api/posts")
    suspend fun createPost(@Body request: CreatePostRequest): Response<PostResponse>

    // Lấy tất cả bài đăng
    @GET("api/admin/posts")
    suspend fun getAllPosts(): Response<List<PostResponse>>

}