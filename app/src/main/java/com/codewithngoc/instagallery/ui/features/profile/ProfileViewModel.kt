package com.codewithngoc.instagallery.ui.features.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codewithngoc.instagallery.data.InstaGallerySession
import com.codewithngoc.instagallery.data.model.PostResponse
import com.codewithngoc.instagallery.data.model.ProfileUiState
import com.codewithngoc.instagallery.data.model.User
import com.codewithngoc.instagallery.data.model.UserProfileResponse
import com.codewithngoc.instagallery.data.model.UserState
import com.codewithngoc.instagallery.data.repository.ProfileRepository
import com.codewithngoc.instagallery.data.remote.ApiResponse
import com.codewithngoc.instagallery.ui.features.homefeed.likeAction.LikeViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repository: ProfileRepository,
    private val session: InstaGallerySession
) : ViewModel() {

    // --- Profile State
    private val _uiState = MutableStateFlow<ProfileUiState>(ProfileUiState.Loading)
    val uiState = _uiState.asStateFlow()

    fun loadProfile(userId: Int) {
        viewModelScope.launch {
            _uiState.value = ProfileUiState.Loading

            val userResult = repository.getUserProfile(userId)
            val postsResult = repository.getUserPosts(userId)

            if (userResult is ApiResponse.Success && postsResult is ApiResponse.Success) {
                val userProfile = userResult.data
                val posts = postsResult.data

                _uiState.value = ProfileUiState.Success(
                    user = userProfile.toUiUser(),
                    states = listOf(
                        UserState(userProfile.postCount.toString(), "Posts"),
                        UserState(userProfile.followerCount.toString(), "Followers"),
                        UserState(userProfile.followingCount.toString(), "Following")
                    ),
                    posts = posts
                )
            } else {
                val message = (userResult as? ApiResponse.Error)?.message
                    ?: (postsResult as? ApiResponse.Error)?.message
                    ?: "Unknown error"
                _uiState.value = ProfileUiState.Error(message)
            }
        }
    }

    fun updateCommentCount(postId: Int) {
        val currentState = _uiState.value
        if (currentState is ProfileUiState.Success) {
            val updatedPosts = currentState.posts.map { post ->
                if (post.postId == postId) {
                    post.copy(commentCount = post.commentCount + 1)
                } else {
                    post
                }
            }
            _uiState.value = currentState.copy(posts = updatedPosts)
        }
    }

    // ✅ Lắng nghe LikeViewModel để cập nhật like realtime
    fun observeLikeEvents(likeViewModel: LikeViewModel) {
        viewModelScope.launch {
            likeViewModel.likeEvent.collect { (postId, newCount) ->
                val currentState = _uiState.value
                if (currentState is ProfileUiState.Success) {
                    val updatedPosts = currentState.posts.map { post ->
                        if (post.postId == postId) post.copy(likeCount = newCount)
                        else post
                    }
                    _uiState.value = currentState.copy(posts = updatedPosts)
                }
            }
        }
    }

}

fun UserProfileResponse.toUiUser(): User {
    return User(
        userId = this.userId,
        username = this.username,
        fullName = this.fullName,
        profilePictureUrl = this.profilePictureUrl,
        bio = this.bio,
        website = this.website,
        gender = this.gender,
        dateOfBirth = this.dateOfBirth,
        location = this.location,
        isVerified = this.isVerified,
        postCount = this.postCount,
        followerCount = this.followerCount,
        followingCount = this.followingCount
    )
}
