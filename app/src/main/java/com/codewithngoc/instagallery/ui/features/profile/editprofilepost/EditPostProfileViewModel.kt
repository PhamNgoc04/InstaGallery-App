package com.codewithngoc.instagallery.ui.features.profile.editprofilepost

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codewithngoc.instagallery.data.model.FeedPostResponse
import com.codewithngoc.instagallery.data.model.PostVisibility
import com.codewithngoc.instagallery.data.model.UpdatePostRequest
import com.codewithngoc.instagallery.data.remote.ApiResponse
import com.codewithngoc.instagallery.data.repository.PostRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class EditPostState {
    object Loading : EditPostState()
    data class Success(val post: FeedPostResponse) : EditPostState()
    data class Error(val message: String) : EditPostState()
}

sealed class EditPostEvent {
    object UpdateSuccess : EditPostEvent()
    data class UpdateError(val message: String) : EditPostEvent()
}

@HiltViewModel
class EditPostProfileViewModel @Inject constructor(
    private val postRepository: PostRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<EditPostState>(EditPostState.Loading)
    val uiState: StateFlow<EditPostState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<EditPostEvent>()
    val events: SharedFlow<EditPostEvent> = _events.asSharedFlow()

    fun loadPostData(postId: Long) {
        viewModelScope.launch {
            _uiState.value = EditPostState.Loading
            when (val response = postRepository.getPostDetail(postId)) {
                is ApiResponse.Success -> {
                    _uiState.value = EditPostState.Success(response.data)
                }
                is ApiResponse.Error -> {
                    _uiState.value = EditPostState.Error(response.message ?: "Lỗi tải thông tin bài viết")
                }
                else -> {
                    _uiState.value = EditPostState.Error("Lỗi kết nối")
                }
            }
        }
    }

    fun updatePost(postId: Long, caption: String, location: String, visibility: PostVisibility) {
        viewModelScope.launch {
            val request = UpdatePostRequest(
                caption = caption,
                location = location, // Note: Location handles mapped string for now
                visibility = visibility
            )
            when (val response = postRepository.updatePost(postId, request)) {
                is ApiResponse.Success -> {
                    _events.emit(EditPostEvent.UpdateSuccess)
                }
                is ApiResponse.Error -> {
                    _events.emit(EditPostEvent.UpdateError(response.message ?: "Cập nhật thất bại"))
                }
                else -> {
                    _events.emit(EditPostEvent.UpdateError("Lỗi mạng"))
                }
            }
        }
    }
}