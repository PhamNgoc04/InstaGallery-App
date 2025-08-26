package com.codewithngoc.instagallery.data

import com.codewithngoc.instagallery.data.model.AddCommentRequest
import com.codewithngoc.instagallery.data.model.AuthResponse
import com.codewithngoc.instagallery.data.model.CommentResponse
import com.codewithngoc.instagallery.data.model.CreatePostRequest
import com.codewithngoc.instagallery.data.model.PostResponse
import com.codewithngoc.instagallery.data.model.SignInRequest
import com.codewithngoc.instagallery.data.model.SignUpRequest
import com.codewithngoc.instagallery.data.model.UploadResponse
import com.codewithngoc.instagallery.data.model.UserProfileResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface InstaGalleryApi {
    // Đăng nhập
    @POST("api/auth/login")
    suspend fun signIn(@Body request: SignInRequest): Response<AuthResponse>

    // Đăng ký
    @POST("api/auth/register")
    suspend fun signUp(@Body request: SignUpRequest): Response<AuthResponse>

    // ✅ Đăng xuất
    @POST("api/auth/logout")
    suspend fun logout(
        @Header("Authorization") token: String,
        @Body body: Map<String, String>
    ): Response<Unit>

    // Tạo bài đăng
    @POST("api/posts")
    suspend fun createPost(@Body request: CreatePostRequest): Response<PostResponse>

    @Multipart
    @POST("api/upload")
    suspend fun uploadFile(@Part file: MultipartBody.Part): Response<UploadResponse>

    // Lấy tất cả bài đăng
    @GET("api/posts")
    suspend fun getAllPosts(): Response<List<PostResponse>>

    // ✅ API để thêm bình luận
    @POST("api/posts/{postId}/comments")
    suspend fun addComment(
        @Path("postId") postId: Int,
        @Body request: AddCommentRequest
    ): Response<CommentResponse>

    // ✅ API để lấy danh sách bình luận
    @GET("api/posts/{postId}/comments")
    suspend fun getCommentsForPost(
        @Path("postId") postId: Int,
        @Query("page") page: Int,
        @Query("size") size: Int
    ): Response<List<CommentResponse>>

    // ✅ API để xóa bình luận

    // ✅ API để like bài đăng

    // ✅ API để unlike bài đăng

    // 👤 Lấy thông tin người dùng
    @GET("api/auth/profile/{id}")
    suspend fun getUserProfile(
        @Header("Authorization") token: String,
        @Path("id") userId: Int
    ): Response<UserProfileResponse>

    // 📸 Lấy danh sách bài đăng của 1 người dùng
    @GET("api/posts/user/{id}")
    suspend fun getUserPosts(
        @Header("Authorization") token: String,
        @Path("id") userId: Int
    ): Response<List<PostResponse>>

}