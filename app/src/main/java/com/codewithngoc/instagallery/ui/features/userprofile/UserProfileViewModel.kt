package com.codewithngoc.instagallery.ui.features.userprofile

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codewithngoc.instagallery.data.model.FeedPostResponse
import com.codewithngoc.instagallery.data.model.UserProfileResponse
import com.codewithngoc.instagallery.data.remote.ApiResponse
import com.codewithngoc.instagallery.data.repository.ChatRepository
import com.codewithngoc.instagallery.data.repository.PostRepository
import com.codewithngoc.instagallery.data.repository.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class UserProfileUiState {
    object Loading : UserProfileUiState()
    data class Success(
        val user: UserProfileResponse,
        val posts: List<FeedPostResponse>,
        val isFollowing: Boolean,
        val followerCount: Int = 0,
        val followingCount: Int = 0
    ) : UserProfileUiState()
    data class Error(val message: String) : UserProfileUiState()
}

@HiltViewModel
class UserProfileViewModel @Inject constructor(
    private val profileRepository: ProfileRepository,
    private val postRepository: PostRepository,
    private val chatRepository: ChatRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val userId: Long = savedStateHandle.get<Long>("userId") ?: 0L

    private val _uiState = MutableStateFlow<UserProfileUiState>(UserProfileUiState.Loading)
    val uiState: StateFlow<UserProfileUiState> = _uiState.asStateFlow()

    init {
        loadProfile()
    }

    fun loadProfile() {
        viewModelScope.launch {
            _uiState.value = UserProfileUiState.Loading

            // Gọi song song 4 API để tăng tốc
            val userDeferred = async { profileRepository.getUserProfile(userId) }
            val postsDeferred = async { postRepository.getUserPosts(userId) }
            val followersDeferred = async { profileRepository.getFollowers(userId) }
            val followingDeferred = async { profileRepository.getFollowing(userId) }

            val userRes = userDeferred.await()
            val postsRes = postsDeferred.await()
            val followersRes = followersDeferred.await()
            val followingRes = followingDeferred.await()

            when (userRes) {
                is ApiResponse.Success -> {
                    val posts = when (postsRes) {
                        is ApiResponse.Success -> postsRes.data
                        else -> emptyList()
                    }
                    val followerCount = when (followersRes) {
                        is ApiResponse.Success -> followersRes.data.meta.totalRecords
                        else -> 0
                    }
                    val followingCount = when (followingRes) {
                        is ApiResponse.Success -> followingRes.data.meta.totalRecords
                        else -> 0
                    }
                    _uiState.value = UserProfileUiState.Success(
                        user = userRes.data,
                        posts = posts,
                        isFollowing = false,
                        followerCount = followerCount,
                        followingCount = followingCount
                    )
                }
                is ApiResponse.Error -> _uiState.value = UserProfileUiState.Error(userRes.message ?: "Không thể tải hồ sơ")
                is ApiResponse.Exception -> _uiState.value = UserProfileUiState.Error(userRes.exception.message ?: "Lỗi kết nối")
            }
        }
    }

    fun toggleFollow() {
        viewModelScope.launch {
            profileRepository.toggleFollow(userId)
            loadProfile()
        }
    }

    /**
     * Lấy conversation DIRECT đang có với user này,
     * nếu chưa có thì tạo mới qua API.
     * Sau khi có conversationId, gọi onSuccess(conversationId) để navigate.
     */
    fun getOrCreateConversation(onSuccess: (Long) -> Unit) {
        viewModelScope.launch {
            val result = chatRepository.getOrCreateDirectConversation(userId)
            when (result) {
                is com.codewithngoc.instagallery.data.remote.ApiResponse.Success -> onSuccess(result.data)
                else -> { /* TODO: hiện snackbar lỗi */ }
            }
        }
    }
}
