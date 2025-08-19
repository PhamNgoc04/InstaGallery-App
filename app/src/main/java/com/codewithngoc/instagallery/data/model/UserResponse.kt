package com.codewithngoc.instagallery.data.model

import kotlinx.serialization.Serializable

@Serializable
data class UserProfileResponse(
    val userId: Int = 0,
    val username: String = "",
    val fullName: String = "",
    val profilePictureUrl: String? = null,
    val bio: String? = null,
    val website: String? = null,
    val gender: String? = null,
    val dateOfBirth: String? = null,
    val location: String? = null,
    val isVerified: Boolean = false,
    val postCount: Int = 0,
    val followerCount: Int = 0,
    val followingCount: Int = 0
)

sealed class ProfileUiState {
    object Loading : ProfileUiState()
    data class Success(
        val user: User,
        val stats: List<UserState>,
        val posts: List<PostResponse>
    ) : ProfileUiState()

    data class Error(val message: String) : ProfileUiState()
}

data class UserState(
    val count: String,
    val label: String
)

data class User(
    val userId: Int,
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