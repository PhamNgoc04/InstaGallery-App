package com.codewithngoc.instagallery.ui.features.homefeed.detailspost

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codewithngoc.instagallery.data.model.PostResponse
import com.codewithngoc.instagallery.data.remote.ApiResponse
import com.codewithngoc.instagallery.data.repository.PostRepository
import com.codewithngoc.instagallery.ui.features.homefeed.detailspost.PostDetailUiState.*
import com.codewithngoc.instagallery.ui.features.homefeed.likeAction.LikeViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface PostDetailUiState {
    object Loading : PostDetailUiState
    data class Success(val post: PostResponse) : PostDetailUiState
    data class Error(val message: String) : PostDetailUiState
}

@HiltViewModel
class PostDetailViewModel @Inject constructor(
    private val postRepository: PostRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow<PostDetailUiState>(PostDetailUiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        val postId: String? = savedStateHandle["postId"]
        if (postId != null) {
            fetchPostDetails(postId.toInt())
        } else {
            _uiState.value = PostDetailUiState.Error("Post ID not found.")
        }
    }

    private fun fetchPostDetails(postId: Int) {
        viewModelScope.launch {
            _uiState.value = PostDetailUiState.Loading
            when (val response = postRepository.getPostById(postId)) {
                is ApiResponse.Success -> {
                    _uiState.value = Success(response.data)
                }
                is ApiResponse.Error -> {
                    _uiState.value = Error(response.message ?: "Failed to load post.")
                }

                is ApiResponse.Exception -> TODO()
            }
        }
    }

    fun incrementCommentCount() {
        _uiState.update { currentState ->
            if (currentState is PostDetailUiState.Success) {
                val currentPost = currentState.post
                currentState.copy(post = currentPost.copy(commentCount = currentPost.commentCount + 1))
            } else {
                currentState
            }
        }
    }

    /**
     * Lắng nghe các sự kiện từ LikeViewModel và cập nhật trạng thái UI.
     * Hàm này sẽ được gọi từ Composable.
     */
    fun observeLikeEvents(likeViewModel: LikeViewModel) {
        viewModelScope.launch {
            likeViewModel.likeEvent.collect { (postId, newLikeCount) ->
                // Lấy ra trạng thái hiện tại
                val currentState = _uiState.value
                // Chỉ cập nhật nếu sự kiện like dành cho bài viết đang hiển thị
                if (currentState is PostDetailUiState.Success && currentState.post.postId == postId) {
                    _uiState.update {
                        // Cập nhật post bên trong trạng thái Success
                        (it as PostDetailUiState.Success).copy(
                            post = it.post.copy(likeCount = newLikeCount)
                        )
                    }
                }
            }
        }
    }
}