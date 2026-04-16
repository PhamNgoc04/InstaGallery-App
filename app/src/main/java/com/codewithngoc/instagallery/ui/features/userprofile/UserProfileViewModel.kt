package com.codewithngoc.instagallery.ui.features.userprofile

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codewithngoc.instagallery.data.model.FeedPostResponse
import com.codewithngoc.instagallery.data.model.UserProfileResponse
import com.codewithngoc.instagallery.data.remote.ApiResponse
import com.codewithngoc.instagallery.data.repository.PostRepository
import com.codewithngoc.instagallery.data.repository.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
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
        val isFollowing: Boolean
    ) : UserProfileUiState()
    data class Error(val message: String) : UserProfileUiState()
}

@HiltViewModel
class UserProfileViewModel @Inject constructor(
    private val profileRepository: ProfileRepository,
    private val postRepository: PostRepository,
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
            when (val res = profileRepository.getUserProfile(userId)) {
                is ApiResponse.Success -> {
                    val user = res.data
                    // Load bài viết của user này
                    val posts = when (val postsRes = postRepository.getUserPosts(userId)) {
                        is ApiResponse.Success -> postsRes.data
                        else -> emptyList()
                    }
                    _uiState.value = UserProfileUiState.Success(
                        user = user,
                        posts = posts,
                        isFollowing = false // TODO: check follow status từ API
                    )
                }
                is ApiResponse.Error -> {
                    _uiState.value = UserProfileUiState.Error(res.message ?: "Không thể tải hồ sơ")
                }
                is ApiResponse.Exception -> {
                    _uiState.value = UserProfileUiState.Error(res.exception.message ?: "Lỗi kết nối")
                }
            }
        }
    }

    fun toggleFollow() {
        viewModelScope.launch {
            profileRepository.toggleFollow(userId)
            // Reload để cập nhật trạng thái follow
            loadProfile()
        }
    }
}
