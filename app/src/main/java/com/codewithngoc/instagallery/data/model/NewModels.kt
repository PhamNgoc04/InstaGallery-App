package com.codewithngoc.instagallery.data.model

/**
 * Models cho Notification, Conversation, Message, Booking, Rating, Portfolio, Search
 */

// ==================== NOTIFICATIONS ====================
data class NotificationResponse(
    val id: Long = 0,
    val userId: Long = 0,
    val senderId: Long? = null,
    val senderUsername: String? = null,
    val senderAvatar: String? = null,
    val type: String = "", // NEW_LIKE, NEW_COMMENT, NEW_FOLLOWER, BOOKING_REQUEST, etc.
    val targetType: String? = null, // POST, COMMENT, USER, BOOKING
    val targetId: Long? = null,
    val title: String? = null,
    val body: String? = null,
    val isRead: Boolean = false,
    val createdAt: String? = null
)

data class PaginatedNotificationsResponse(
    val notifications: List<NotificationResponse> = emptyList(),
    val meta: PaginationMeta = PaginationMeta()
)

data class UnreadCountResponse(
    val count: Int = 0
)

// ==================== CONVERSATIONS ====================

/** Wrapper khớp với shape JSON: { "data": { "conversations": [...] } } */
data class ConversationsData(
    val conversations: List<ConversationResponse> = emptyList()
)

data class ConversationResponse(
    val id: Long = 0,
    val title: String? = null,
    val type: String = "DIRECT",        // "DIRECT" or "GROUP"
    // Fields cho DIRECT conversation (backend trả simplified partner info)
    val partnerId: Long? = null,
    val partnerName: String? = null,
    val partnerAvatar: String? = null,
    // lastMessage là String (preview text), lastMessageTime là ISO string
    val lastMessage: String? = null,
    val lastMessageTime: String? = null,
    // Optional fields (có thể null nếu backend không gửi)
    val unreadCount: Int = 0
)

data class ConversationMember(
    val userId: Long = 0,
    val username: String = "",
    val fullName: String = "",
    val avatar: String? = null,
    val role: String = "MEMBER"
)

data class ChatMessageResponse(
    @com.google.gson.annotations.SerializedName(value = "id", alternate = ["messageId"]) val id: Long = 0,
    @com.google.gson.annotations.SerializedName("conversationId") val conversationId: Long = 0,
    @com.google.gson.annotations.SerializedName("senderId") val senderId: Long = 0,
    @com.google.gson.annotations.SerializedName("senderUsername") val senderUsername: String? = null,
    @com.google.gson.annotations.SerializedName("senderAvatar") val senderAvatar: String? = null,
    @com.google.gson.annotations.SerializedName("content") val content: String? = null,
    @com.google.gson.annotations.SerializedName(value = "messageType", alternate = ["type"]) val messageType: String = "TEXT",
    @com.google.gson.annotations.SerializedName("mediaUrl") val mediaUrl: String? = null,
    @com.google.gson.annotations.SerializedName("replyToId") val replyToId: Long? = null,
    @com.google.gson.annotations.SerializedName("isDeleted") val isDeleted: Boolean = false,
    @com.google.gson.annotations.SerializedName("createdAt") val createdAt: String? = null
)

data class PaginatedMessagesResponse(
    @com.google.gson.annotations.SerializedName("messages") val messages: List<ChatMessageResponse> = emptyList(),
    @com.google.gson.annotations.SerializedName("meta") val meta: PaginationMeta = PaginationMeta()
)

data class SendMessageRequest(
    val content: String? = null,
    val messageType: String = "TEXT",
    val mediaUrl: String? = null,
    val replyToId: Long? = null
)

data class CreateConversationRequest(
    val memberIds: List<Long>,
    val type: String = "DIRECT",
    val title: String? = null
)

// ==================== BOOKINGS ====================
data class BookingResponse(
    val id: Long = 0,
    val clientId: Long = 0,
    val clientUsername: String? = null,
    val clientAvatar: String? = null,
    val photographerId: Long = 0,
    val photographerUsername: String? = null,
    val photographerAvatar: String? = null,
    val bookingDate: String? = null,
    val durationHours: Double? = null,
    val location: String? = null,
    val details: String? = null,
    val price: Double? = null,
    val currency: String = "VND",
    val status: String = "PENDING", // PENDING, CONFIRMED, IN_PROGRESS, COMPLETED, CANCELLED
    val cancellationReason: String? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null
)

data class CreateBookingRequest(
    val photographerId: Long,
    val bookingDate: String, // ISO format
    val durationHours: Double? = null,
    val location: String? = null,
    val details: String? = null,
    val price: Double? = null,
    val currency: String = "VND"
)

data class PaginatedBookingsResponse(
    val bookings: List<BookingResponse> = emptyList(),
    val meta: PaginationMeta = PaginationMeta()
)

// ==================== RATINGS ====================
data class RatingResponse(
    val id: Long = 0,
    val bookingId: Long = 0,
    val raterId: Long = 0,
    val rateeId: Long = 0,
    val ratingValue: Int = 0,
    val comment: String? = null,
    val createdAt: String? = null
)

data class CreateRatingRequest(
    val bookingId: Long,
    val ratingValue: Int, // 1-5
    val comment: String? = null
)

// ==================== PORTFOLIO ====================
data class PortfolioResponse(
    val id: Long = 0,
    val userId: Long = 0,
    val username: String? = null,
    val fullName: String? = null,
    val avatar: String? = null,
    val title: String? = null,
    val description: String? = null,
    val specialties: List<String>? = null,
    val hourlyRate: Double? = null,
    val currency: String = "VND",
    val serviceArea: String? = null,
    val isAvailable: Boolean = true,
    val ratingAvg: Double = 0.0,
    val reviewCount: Int = 0
)

data class PaginatedPortfoliosResponse(
    val portfolios: List<PortfolioResponse> = emptyList(),
    val meta: PaginationMeta = PaginationMeta()
)

// ==================== SEARCH ====================
data class SearchResponse(
    val users: List<SearchUserResult> = emptyList(),
    val posts: List<FeedPostResponse> = emptyList(),
    val tags: List<TagResult> = emptyList()
)

data class SearchUserResult(
    @com.google.gson.annotations.SerializedName(value = "userId", alternate = ["id", "accountId"]) val id: Long = 0,
    val username: String = "",
    val fullName: String = "",
    val profilePictureUrl: String? = null,
    val userType: String = "ENTHUSIAST",
    val isFollowing: Boolean = false
)

data class TagResult(
    val id: Long = 0,
    val name: String = "",
    val usageCount: Int = 0
)

// ==================== AUTH EXTRAS ====================
data class ForgotPasswordRequest(
    val email: String
)

data class ChangePasswordRequest(
    val currentPassword: String,
    val newPassword: String
)

data class UpdateUserProfileRequest(
    val fullName: String? = null,
    val username: String? = null,
    val bio: String? = null,
    val website: String? = null,
    val gender: String? = null,
    val phoneNumber: String? = null,
    val dateOfBirth: String? = null,
    val location: String? = null
)

// ==================== PORTFOLIO AVAILABILITY ====================

/**
 * Một khung giờ rảnh của nhiếp ảnh gia
 */
data class AvailabilitySlot(
    val dayOfWeek: String = "", // "MONDAY", "TUESDAY", ...
    val startTime: String = "", // "08:00"
    val endTime: String = ""    // "17:00"
)

/**
 * Request cập nhật lịch rảnh
 */
data class PortfolioAvailabilityRequest(
    val slots: List<AvailabilitySlot>
)

/**
 * Response lịch rảnh của nhiếp ảnh gia
 */
data class PortfolioAvailabilityResponse(
    val userId: Long = 0,
    val slots: List<AvailabilitySlot> = emptyList(),
    val updatedAt: String? = null
)

/**
 * Request cập nhật thông tin Portfolio
 */
data class UpdatePortfolioRequest(
    val bio: String? = null,
    val specialties: List<String>? = null,
    val hourlyRate: Double? = null,
    val currency: String? = null,
    val serviceArea: String? = null,
    val isAvailable: Boolean? = null
)

// ==================== PHOTOGRAPHER RATINGS (LIST) ====================

/**
 * Response danh sách đánh giá của một Nhiếp Ảnh Gia
 */
data class PhotographerRatingsResponse(
    val photographerId: Long = 0,
    val averageRating: Double = 0.0,
    val totalReviews: Int = 0,
    val ratings: List<RatingResponse> = emptyList()
)

// ==================== EXPLORE ====================

/**
 * Response bài viết khám phá (Explore feed)
 */
data class ExploreResponse(
    val posts: List<FeedPostResponse> = emptyList(),
    val meta: PaginationMeta = PaginationMeta()
)

// ==================== SEARCH HISTORY ====================

/**
 * Một từ khoá đã tìm kiếm - khớp với SearchHistoryDto
 */
data class SearchHistoryItem(
    val id: Long = 0,
    val query: String = "",
    val searchedAt: String? = null
)

