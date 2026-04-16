package com.codewithngoc.instagallery.data

import com.codewithngoc.instagallery.data.model.*
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

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

    @POST("api/v1/auth/refresh")
    suspend fun refreshToken(
        @Header("X-Refresh-Token") refreshToken: String
    ): Response<ApiResponseWrapper<TokenRefreshResponse>>

    @POST("api/v1/auth/forgot-password")
    suspend fun forgotPassword(@Body request: ForgotPasswordRequest): Response<ApiResponseWrapper<Unit>>

    @POST("api/v1/auth/reset-password")
    suspend fun resetPassword(@Body request: ResetPasswordRequest): Response<ApiResponseWrapper<Unit>>

    @POST("api/v1/auth/google")
    suspend fun googleLogin(@Body request: GoogleLoginRequest): Response<ApiResponseWrapper<LoginResponse>>

    @PUT("api/v1/auth/change-password")
    suspend fun changePassword(@Body request: ChangePasswordRequest): Response<ApiResponseWrapper<Unit>>

    /** Bước 1: Gen secret + QR Code để user quét vào Google Authenticator */
    @POST("api/v1/auth/2fa/setup")
    suspend fun setup2FA(): Response<ApiResponseWrapper<TwoFaSetupResponse>>

    /** Bước 2: Xác nhận mã OTP từ G-Auth để kích hoạt 2FA */
    @POST("api/v1/auth/2fa/enable")
    suspend fun enable2FA(@Body request: TwoFaVerifyRequest): Response<ApiResponseWrapper<TwoFaEnableResponse>>

    /** Đăng nhập với 2FA — xác thực mã OTP sau khi nhận 401 requires_2fa */
    @POST("api/v1/auth/2fa/verify-login")
    suspend fun verify2FALogin(@Body request: TwoFaLoginVerifyRequest): Response<ApiResponseWrapper<LoginResponse>>

    // ==================== USER PROFILE ====================

    @GET("api/v1/users/me")
    suspend fun getCurrentUser(): Response<ApiResponseWrapper<UserProfileResponse>>

    @PUT("api/v1/users/me")
    suspend fun updateProfile(@Body request: UpdateUserProfileRequest): Response<ApiResponseWrapper<UserProfileResponse>>

    /** Upload ảnh đại diện dạng Multipart */
    @Multipart
    @PUT("api/v1/users/me/avatar")
    suspend fun updateAvatar(
        @Part file: MultipartBody.Part
    ): Response<ApiResponseWrapper<UserProfileResponse>>

    @POST("api/v1/users/me/deactivate")
    suspend fun deactivateAccount(): Response<ApiResponseWrapper<Unit>>

    // ==================== SESSIONS ====================

    @GET("api/v1/users/me/sessions")
    suspend fun getSessions(): Response<ApiResponseWrapper<List<SessionResponse>>>

    @DELETE("api/v1/users/me/sessions/{id}")
    suspend fun deleteSession(@Path("id") sessionId: Long): Response<ApiResponseWrapper<Unit>>

    // ==================== SOCIAL: FOLLOW / BLOCK / MUTE ====================

    @GET("api/v1/users/{id}")
    suspend fun getUserProfile(@Path("id") userId: Long): Response<ApiResponseWrapper<UserProfileResponse>>

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

    @POST("api/v1/users/{id}/follow")
    suspend fun toggleFollow(@Path("id") userId: Long): Response<ApiResponseWrapper<Map<String, Boolean>>>

    @GET("api/v1/users/me/follow-requests")
    suspend fun getFollowRequests(): Response<ApiResponseWrapper<List<FollowRequestResponse>>>

    /** action = "accept" hoặc "reject" */
    @POST("api/v1/users/me/follow-requests/{followerId}/{action}")
    suspend fun handleFollowRequest(
        @Path("followerId") followerId: Long,
        @Path("action") action: String
    ): Response<ApiResponseWrapper<Unit>>

    @POST("api/v1/users/{id}/block")
    suspend fun toggleBlock(@Path("id") userId: Long): Response<ApiResponseWrapper<BlockMuteResponse>>

    @POST("api/v1/users/{id}/mute")
    suspend fun toggleMute(@Path("id") userId: Long): Response<ApiResponseWrapper<BlockMuteResponse>>

    // ==================== POSTS ====================

    @POST("api/v1/posts")
    suspend fun createPost(@Body request: CreatePostRequest): Response<ApiResponseWrapper<PostResponse>>

    @GET("api/v1/posts/{id}")
    suspend fun getPostDetail(@Path("id") postId: Long): Response<ApiResponseWrapper<FeedPostResponse>>

    @GET("api/v1/users/{id}/posts")
    suspend fun getUserPosts(
        @Path("id") userId: Long,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): Response<ApiResponseWrapper<PaginatedFeedResponse>>

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

    // ==================== INTERACTIONS: LIKE, SAVE, COMMENT ====================

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

    @PUT("api/v1/comments/{id}")
    suspend fun updateComment(
        @Path("id") commentId: Long,
        @Body request: AddCommentRequest
    ): Response<ApiResponseWrapper<CommentResponse>>

    @DELETE("api/v1/comments/{id}")
    suspend fun deleteComment(@Path("id") commentId: Long): Response<ApiResponseWrapper<Unit>>

    @POST("api/v1/comments/{id}/like")
    suspend fun toggleCommentLike(@Path("id") commentId: Long): Response<ApiResponseWrapper<ToggleLikeResponse>>

    // ==================== MEDIA ====================

    @POST("api/v1/media/presigned-url")
    suspend fun getPresignedUrl(@Body request: PresignedUrlRequest): Response<ApiResponseWrapper<PresignedUrlResponse>>

    @POST("api/v1/posts/{postId}/media")
    suspend fun addMediaToPost(
        @Path("postId") postId: Long,
        @Body request: AddMediaToPostRequest
    ): Response<ApiResponseWrapper<MediaItemResponse>>

    @DELETE("api/v1/posts/media/{mediaId}")
    suspend fun deleteMedia(@Path("mediaId") mediaId: Long): Response<ApiResponseWrapper<Unit>>

    @PUT("api/v1/posts/{postId}/media/reorder")
    suspend fun reorderMedia(
        @Path("postId") postId: Long,
        @Body request: ReorderMediaRequest
    ): Response<ApiResponseWrapper<Unit>>

    // ==================== ALBUMS ====================

    @GET("api/v1/albums")
    suspend fun getAlbums(): Response<ApiResponseWrapper<List<AlbumResponse>>>

    @POST("api/v1/albums")
    suspend fun createAlbum(@Body request: CreateAlbumRequest): Response<ApiResponseWrapper<AlbumResponse>>

    @GET("api/v1/albums/{id}")
    suspend fun getAlbumDetail(@Path("id") albumId: Long): Response<ApiResponseWrapper<AlbumDetailResponse>>

    @PUT("api/v1/albums/{id}")
    suspend fun updateAlbum(
        @Path("id") albumId: Long,
        @Body request: UpdateAlbumRequest
    ): Response<ApiResponseWrapper<AlbumResponse>>

    @DELETE("api/v1/albums/{id}")
    suspend fun deleteAlbum(@Path("id") albumId: Long): Response<ApiResponseWrapper<Unit>>

    @POST("api/v1/albums/{id}/media")
    suspend fun addPostToAlbum(
        @Path("id") albumId: Long,
        @Body request: AddPostToAlbumRequest
    ): Response<ApiResponseWrapper<Unit>>

    @DELETE("api/v1/albums/{id}/media/{mediaId}")
    suspend fun removePostFromAlbum(
        @Path("id") albumId: Long,
        @Path("mediaId") mediaId: Long
    ): Response<ApiResponseWrapper<Unit>>

    // ==================== EXPLORE & SEARCH ====================

    @GET("api/v1/explore")
    suspend fun getExplore(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): Response<ApiResponseWrapper<ExploreResponse>>

    @GET("api/v1/explore/trending")
    suspend fun getTrendingTags(): Response<ApiResponseWrapper<List<TagResult>>>

    @GET("api/v1/search")
    suspend fun search(
        @Query("q") query: String,
        @Query("type") type: String? = null // "users", "posts", "tags"
    ): Response<ApiResponseWrapper<SearchResponse>>

    @GET("api/v1/search/history")
    suspend fun getSearchHistory(): Response<ApiResponseWrapper<List<SearchHistoryItem>>>

    @DELETE("api/v1/search/history")
    suspend fun clearSearchHistory(): Response<ApiResponseWrapper<Unit>>

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

    // ==================== CHAT ====================

    /** Lấy danh sách hội thoại — path đúng là /chat/conversations */
    @GET("api/v1/chat/conversations")
    suspend fun getConversations(): Response<ApiResponseWrapper<ConversationsData>>

    /** Lấy lịch sử tin nhắn của một cuộc hội thoại */
    @GET("api/v1/chat/conversations/{id}/messages")
    suspend fun getMessages(
        @Path("id") conversationId: Long,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 30
    ): Response<ApiResponseWrapper<PaginatedMessagesResponse>>

    /** Gửi tin nhắn mới — lưu vào DB backend */
    @POST("api/v1/chat/conversations/{id}/messages")
    suspend fun sendMessage(
        @Path("id") conversationId: Long,
        @Body request: SendMessageRequest
    ): Response<ApiResponseWrapper<ChatMessageResponse>>

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

    @GET("api/v1/portfolios/me")
    suspend fun getMyPortfolio(): Response<ApiResponseWrapper<PortfolioResponse>>

    @PUT("api/v1/portfolios/me")
    suspend fun updateMyPortfolio(@Body request: UpdatePortfolioRequest): Response<ApiResponseWrapper<PortfolioResponse>>

    @POST("api/v1/portfolios/me/availability")
    suspend fun updateAvailability(@Body request: PortfolioAvailabilityRequest): Response<ApiResponseWrapper<PortfolioAvailabilityResponse>>

    @GET("api/v1/portfolios/me/availability")
    suspend fun getMyAvailability(): Response<ApiResponseWrapper<PortfolioAvailabilityResponse>>

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

    @PUT("api/v1/bookings/{id}/status")
    suspend fun updateBookingStatus(
        @Path("id") bookingId: Long,
        @Body request: Map<String, String>
    ): Response<ApiResponseWrapper<BookingResponse>>

    // ==================== RATINGS ====================

    @GET("api/v1/users/{photographerId}/ratings")
    suspend fun getPhotographerRatings(
        @Path("photographerId") photographerId: Long
    ): Response<ApiResponseWrapper<PhotographerRatingsResponse>>

    @POST("api/v1/ratings")
    suspend fun createRating(@Body request: CreateRatingRequest): Response<ApiResponseWrapper<RatingResponse>>

    @DELETE("api/v1/ratings/{ratingId}")
    suspend fun deleteRating(@Path("ratingId") ratingId: Long): Response<ApiResponseWrapper<Unit>>

    // ==================== REPORTS ====================

    @POST("api/v1/reports")
    suspend fun createReport(@Body request: Map<String, String>): Response<ApiResponseWrapper<Unit>>

    // ==================== ADMIN ====================

    @GET("api/v1/admin/stats")
    suspend fun getAdminStats(): Response<ApiResponseWrapper<AdminStatsResponse>>

    @GET("api/v1/admin/stats/growth")
    suspend fun getAdminGrowth(
        @Query("period") period: String = "30d"
    ): Response<ApiResponseWrapper<AdminGrowthResponse>>

    @PUT("api/v1/admin/users/{userId}/ban")
    suspend fun banUser(
        @Path("userId") userId: Long,
        @Body request: BanUserRequest
    ): Response<ApiResponseWrapper<Unit>>

    @DELETE("api/v1/admin/posts/{postId}")
    suspend fun adminDeletePost(@Path("postId") postId: Long): Response<ApiResponseWrapper<Unit>>

    @DELETE("api/v1/admin/comments/{commentId}")
    suspend fun adminDeleteComment(@Path("commentId") commentId: Long): Response<ApiResponseWrapper<Unit>>

    @GET("api/v1/admin/reports")
    suspend fun getAdminReports(): Response<ApiResponseWrapper<List<AdminReportResponse>>>

    @PUT("api/v1/admin/reports/{reportId}")
    suspend fun resolveReport(
        @Path("reportId") reportId: Long,
        @Body request: ResolveReportRequest
    ): Response<ApiResponseWrapper<AdminReportResponse>>
}