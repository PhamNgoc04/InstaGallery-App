package com.codewithngoc.instagallery.data.model

import com.google.gson.annotations.SerializedName

/**
 * User profile từ backend - khớp với UserDto
 */
data class UserProfileResponse(
    @SerializedName("id") val id: Long = 0,
    @SerializedName("username") val username: String = "",
    @SerializedName("email") val email: String = "",
    @SerializedName("fullName") val fullName: String = "",
    @SerializedName("profilePictureUrl") val profilePictureUrl: String? = null,
    @SerializedName("role") val role: String = "USER",
    @SerializedName("userType") val userType: String = "ENTHUSIAST",
    @SerializedName("isActive") val isActive: Boolean = true,
    @SerializedName("isVerified") val isVerified: Boolean = false
)

sealed class ProfileUiState {
    object Loading : ProfileUiState()
    data class Success(
        val user: User,
        val states: List<UserState>,
        val posts: List<FeedPostResponse>
    ) : ProfileUiState()

    data class Error(val message: String) : ProfileUiState()
}

data class UserState(
    val count: String,
    val label: String
)

data class User(
    val userId: Long,
    val username: String,
    val fullName: String,
    val profilePictureUrl: String?,
    val bio: String?,
    val website: String?,
    val gender: String?,
    val dateOfBirth: String?,
    val location: String?,
    val isVerified: Boolean,
    val postCount: Int,
    val followerCount: Int,
    val followingCount: Int
)

// Follow models matching backend FollowDto
data class FollowDto(
    val id: Long = 0,
    val username: String = "",
    val fullName: String = "",
    val avatar: String? = null,
    val role: String = "USER",
    val userType: String = "ENTHUSIAST",
    val isFollowing: Boolean? = null
)

data class PaginatedFollowsResponse(
    val users: List<FollowDto> = emptyList(),
    val meta: FollowPaginationMeta = FollowPaginationMeta()
)

data class FollowPaginationMeta(
    val currentPage: Int = 1,
    val totalPages: Int = 0,
    val totalRecords: Int = 0
)