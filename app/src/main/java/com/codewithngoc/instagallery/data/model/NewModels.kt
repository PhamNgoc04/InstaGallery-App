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
data class ConversationResponse(
    val id: Long = 0,
    val title: String? = null,
    val type: String = "DIRECT", // DIRECT, GROUP
    val lastMessage: ChatMessageResponse? = null,
    val unreadCount: Int = 0,
    val members: List<ConversationMember> = emptyList(),
    val createdAt: String? = null,
    val updatedAt: String? = null
)

data class ConversationMember(
    val userId: Long = 0,
    val username: String = "",
    val fullName: String = "",
    val avatar: String? = null,
    val role: String = "MEMBER"
)

data class ChatMessageResponse(
    val id: Long = 0,
    val conversationId: Long = 0,
    val senderId: Long = 0,
    val senderUsername: String? = null,
    val senderAvatar: String? = null,
    val content: String? = null,
    val messageType: String = "TEXT", // TEXT, IMAGE, VIDEO, FILE, SYSTEM
    val mediaUrl: String? = null,
    val replyToId: Long? = null,
    val isDeleted: Boolean = false,
    val createdAt: String? = null
)

data class PaginatedMessagesResponse(
    val messages: List<ChatMessageResponse> = emptyList(),
    val meta: PaginationMeta = PaginationMeta()
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
    val id: Long = 0,
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
