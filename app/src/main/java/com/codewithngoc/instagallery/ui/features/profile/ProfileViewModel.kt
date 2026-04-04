package com.codewithngoc.instagallery.ui.features.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codewithngoc.instagallery.data.InstaGallerySession
import com.codewithngoc.instagallery.data.model.FeedPostResponse
import com.codewithngoc.instagallery.data.model.ProfileUiState
import com.codewithngoc.instagallery.data.model.User
import com.codewithngoc.instagallery.data.model.UserProfileResponse
import com.codewithngoc.instagallery.data.model.UserState
import com.codewithngoc.instagallery.data.repository.PostRepository
import com.codewithngoc.instagallery.data.repository.ProfileRepository
import com.codewithngoc.instagallery.data.remote.ApiResponse
import com.codewithngoc.instagallery.ui.features.homefeed.likeAction.LikeViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repository: ProfileRepository,
    private val postRepository: PostRepository,
    private val session: InstaGallerySession
) : ViewModel() {

    // --- Profile State
    private val _uiState = MutableStateFlow<ProfileUiState>(ProfileUiState.Loading)
    val uiState = _uiState.asStateFlow()

    fun loadProfile(userId: Long) {
        viewModelScope.launch {
            _uiState.value = ProfileUiState.Loading

            // Gọi song song 3 API: user profile, followers count, following count
            val userDeferred = async { repository.getUserProfile(userId) }
            val followersDeferred = async { repository.getFollowers(userId) }
            val followingDeferred = async { repository.getFollowing(userId) }
            // Lấy posts từ feed (filter theo userId)
            val postsDeferred = async { postRepository.getFeed(page = 1, limit = 50) }

            val userResult = userDeferred.await()
            val followersResult = followersDeferred.await()
            val followingResult = followingDeferred.await()
            val postsResult = postsDeferred.await()

            if (userResult is ApiResponse.Success) {
                val userProfile = userResult.data

                // Lấy follower/following counts từ pagination meta
                val followerCount = if (followersResult is ApiResponse.Success) {
                    followersResult.data.meta.totalRecords
                } else 0

                val followingCount = if (followingResult is ApiResponse.Success) {
                    followingResult.data.meta.totalRecords
                } else 0

                // Lấy posts của user hiện tại từ feed
                val myPosts = if (postsResult is ApiResponse.Success) {
                    postsResult.data.posts.filter { it.userId == userId }
                } else emptyList()

                _uiState.value = ProfileUiState.Success(
                    user = userProfile.toUiUser(
                        postCount = myPosts.size,
                        followerCount = followerCount,
                        followingCount = followingCount
                    ),
                    states = listOf(
                        UserState(myPosts.size.toString(), "Posts"),
                        UserState(followerCount.toString(), "Followers"),
                        UserState(followingCount.toString(), "Following")
                    ),
                    posts = myPosts
                )
            } else {
                val message = (userResult as? ApiResponse.Error)?.message ?: "Unknown error"
                _uiState.value = ProfileUiState.Error(message)
            }
        }
    }

    /**
     * Load profile của user đang đăng nhập
     */
    fun loadCurrentProfile() {
        val userId = session.getUserId()
        if (userId != -1L) {
            loadProfile(userId)
        }
    }

    fun updateCommentCount(postId: Long) {
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

    // Xóa bài viết
    fun deletePost(postId: Long) {
        viewModelScope.launch {
            val result = postRepository.deletePost(postId)
            if (result is ApiResponse.Success) {
                // Xoá thành công, update lại UI state
                val currentState = _uiState.value
                if (currentState is ProfileUiState.Success) {
                    val updatedPosts = currentState.posts.filter { it.postId != postId }
                    
                    // Cập nhật postCount cho user object nếu cần
                    val updatedUser = currentState.user.copy(postCount = updatedPosts.size)
                    val updatedStates = currentState.states.map {
                        if (it.label == "Posts") it.copy(count = updatedPosts.size.toString())
                        else it
                    }

                    _uiState.value = currentState.copy(
                        user = updatedUser,
                        states = updatedStates,
                        posts = updatedPosts
                    )
                }
            } else {
                // Ignore error handling for now or implement SnackBar logic later if needed
            }
        }
    }

}

fun UserProfileResponse.toUiUser(
    postCount: Int = 0,
    followerCount: Int = 0,
    followingCount: Int = 0
): User {
    return User(
        userId = this.id,
        username = this.username,
        fullName = this.fullName,
        profilePictureUrl = this.profilePictureUrl,
        bio = null,
        website = null,
        gender = null,
        dateOfBirth = null,
        location = null,
        isVerified = this.isVerified,
        postCount = postCount,
        followerCount = followerCount,
        followingCount = followingCount
    )
}
