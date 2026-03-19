package com.codewithngoc.instagallery.data

import com.codewithngoc.instagallery.data.model.*
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.DELETE
import retrofit2.http.Path
import retrofit2.http.Query

interface InstaGalleryApi {

    // ==================== AUTH ====================

    @POST("api/v1/auth/login")
    suspend fun signIn(@Body request: SignInRequest): Response<ApiResponseWrapper<LoginResponse>>

    @POST("api/v1/auth/register")
    suspend fun signUp(@Body request: SignUpRequest): Response<ApiResponseWrapper<RegisterResponse>>

    @POST("api/v1/auth/logout")
    suspend fun logout(
        @Header("X-Refresh-Token") refreshToken: String
    ): Response<ApiResponseWrapper<Unit>>

    @POST("api/v1/auth/forgot-password")
    suspend fun forgotPassword(@Body request: ForgotPasswordRequest): Response<ApiResponseWrapper<Unit>>

    @PUT("api/v1/auth/change-password")
    suspend fun changePassword(@Body request: ChangePasswordRequest): Response<ApiResponseWrapper<Unit>>

    // ==================== POSTS ====================

    @POST("api/v1/posts")
    suspend fun createPost(@Body request: CreatePostRequest): Response<ApiResponseWrapper<PostResponse>>

    @GET("api/v1/posts/feed")
    suspend fun getFeed(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 10
    ): Response<ApiResponseWrapper<PaginatedFeedResponse>>

    @PUT("api/v1/posts/{id}")
    suspend fun updatePost(
        @Path("id") postId: Long,
        @Body request: UpdatePostRequest
    ): Response<ApiResponseWrapper<Unit>>

    @DELETE("api/v1/posts/{id}")
    suspend fun deletePost(@Path("id") postId: Long): Response<ApiResponseWrapper<Unit>>

    // ==================== INTERACTIONS ====================

    @POST("api/v1/posts/{id}/like")
    suspend fun toggleLike(@Path("id") postId: Long): Response<ApiResponseWrapper<ToggleLikeResponse>>

    @POST("api/v1/posts/{id}/save")
    suspend fun toggleSave(@Path("id") postId: Long): Response<ApiResponseWrapper<ToggleSaveResponse>>

    @POST("api/v1/posts/{id}/comments")
    suspend fun addComment(
        @Path("id") postId: Long,
        @Body request: AddCommentRequest
    ): Response<ApiResponseWrapper<CommentResponse>>

    @GET("api/v1/posts/{id}/comments")
    suspend fun getCommentsForPost(
        @Path("id") postId: Long,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): Response<ApiResponseWrapper<PaginatedCommentsResponse>>

    // ==================== USERS ====================

    @GET("api/v1/users/me")
    suspend fun getCurrentUser(): Response<ApiResponseWrapper<UserProfileResponse>>

    @PUT("api/v1/users/me")
    suspend fun updateProfile(@Body request: UpdateUserProfileRequest): Response<ApiResponseWrapper<UserProfileResponse>>

    @GET("api/v1/users/{id}")
    suspend fun getUserProfile(@Path("id") userId: Long): Response<ApiResponseWrapper<UserProfileResponse>>

    @POST("api/v1/users/{id}/follow")
    suspend fun toggleFollow(@Path("id") userId: Long): Response<ApiResponseWrapper<Map<String, Boolean>>>

    @GET("api/v1/users/{id}/followers")
    suspend fun getFollowers(
        @Path("id") userId: Long,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): Response<ApiResponseWrapper<PaginatedFollowsResponse>>

    @GET("api/v1/users/{id}/following")
    suspend fun getFollowing(
        @Path("id") userId: Long,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): Response<ApiResponseWrapper<PaginatedFollowsResponse>>

    @GET("api/v1/users/suggestions")
    suspend fun getSuggestions(
        @Query("limit") limit: Int = 10
    ): Response<ApiResponseWrapper<List<SearchUserResult>>>

    // ==================== SEARCH ====================

    @GET("api/v1/search")
    suspend fun search(
        @Query("q") query: String,
        @Query("type") type: String? = null // users, posts, tags
    ): Response<ApiResponseWrapper<SearchResponse>>

    // ==================== NOTIFICATIONS ====================

    @GET("api/v1/notifications")
    suspend fun getNotifications(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): Response<ApiResponseWrapper<PaginatedNotificationsResponse>>

    @PUT("api/v1/notifications/{id}/read")
    suspend fun markNotificationRead(@Path("id") notificationId: Long): Response<ApiResponseWrapper<Unit>>

    @PUT("api/v1/notifications/read-all")
    suspend fun markAllNotificationsRead(): Response<ApiResponseWrapper<Unit>>

    @GET("api/v1/notifications/unread-count")
    suspend fun getUnreadNotificationCount(): Response<ApiResponseWrapper<UnreadCountResponse>>

    // ==================== CONVERSATIONS ====================

    @GET("api/v1/conversations")
    suspend fun getConversations(): Response<ApiResponseWrapper<List<ConversationResponse>>>

    @POST("api/v1/conversations")
    suspend fun createConversation(@Body request: CreateConversationRequest): Response<ApiResponseWrapper<ConversationResponse>>

    @GET("api/v1/conversations/{id}/messages")
    suspend fun getMessages(
        @Path("id") conversationId: Long,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 30
    ): Response<ApiResponseWrapper<PaginatedMessagesResponse>>

    @POST("api/v1/conversations/{id}/messages")
    suspend fun sendMessage(
        @Path("id") conversationId: Long,
        @Body request: SendMessageRequest
    ): Response<ApiResponseWrapper<ChatMessageResponse>>

    @PUT("api/v1/conversations/{id}/read")
    suspend fun markConversationRead(@Path("id") conversationId: Long): Response<ApiResponseWrapper<Unit>>

    // ==================== PORTFOLIOS ====================

    @GET("api/v1/portfolios")
    suspend fun getPortfolios(
        @Query("location") location: String? = null,
        @Query("specialty") specialty: String? = null,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): Response<ApiResponseWrapper<PaginatedPortfoliosResponse>>

    @GET("api/v1/portfolios/users/{userId}")
    suspend fun getUserPortfolio(@Path("userId") userId: Long): Response<ApiResponseWrapper<PortfolioResponse>>

    // ==================== BOOKINGS ====================

    @POST("api/v1/bookings")
    suspend fun createBooking(@Body request: CreateBookingRequest): Response<ApiResponseWrapper<BookingResponse>>

    @GET("api/v1/bookings")
    suspend fun getBookings(
        @Query("status") status: String? = null,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): Response<ApiResponseWrapper<PaginatedBookingsResponse>>

    @GET("api/v1/bookings/{id}")
    suspend fun getBookingDetail(@Path("id") bookingId: Long): Response<ApiResponseWrapper<BookingResponse>>

    @PUT("api/v1/bookings/{id}")
    suspend fun updateBookingStatus(
        @Path("id") bookingId: Long,
        @Body request: Map<String, String>
    ): Response<ApiResponseWrapper<BookingResponse>>

    // ==================== RATINGS ====================

    @POST("api/v1/ratings")
    suspend fun createRating(@Body request: CreateRatingRequest): Response<ApiResponseWrapper<RatingResponse>>

    // ==================== MEDIA ====================

    @POST("api/v1/media/presigned-url")
    suspend fun getPresignedUrl(@Body request: PresignedUrlRequest): Response<ApiResponseWrapper<PresignedUrlResponse>>

    // ==================== REPORTS ====================

    @POST("api/v1/reports")
    suspend fun createReport(@Body request: Map<String, String>): Response<ApiResponseWrapper<Unit>>
}