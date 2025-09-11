package com.codewithngoc.instagallery.ui.features.homefeed

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codewithngoc.instagallery.R
import com.codewithngoc.instagallery.data.InstaGalleryApi
import com.codewithngoc.instagallery.data.InstaGallerySession
import com.codewithngoc.instagallery.data.model.PostResponse
import com.codewithngoc.instagallery.data.remote.ApiResponse
import com.codewithngoc.instagallery.data.remote.safeApiCall
import com.codewithngoc.instagallery.data.repository.LikeRepository
import com.codewithngoc.instagallery.data.repository.PostRepository
import com.codewithngoc.instagallery.ui.features.homefeed.likeAction.LikeViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

// SharedFlow để thông báo khi một bài viết mới được tạo thành công
val newPostSharedFlow = MutableSharedFlow<Unit>(extraBufferCapacity = 1)

@HiltViewModel
class HomeFeedViewModel @Inject constructor(
    private val postRepository: PostRepository,
    val session: InstaGallerySession,
    @ApplicationContext val context: Context,
) : ViewModel() {

    private val _uiState = MutableStateFlow<PostEvent>(PostEvent.Nothing)
    val uiState = _uiState.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<HomeFeedNavigationEvent>()
    val navigationEvent = _navigationEvent.asSharedFlow()

    private val _posts = MutableStateFlow<List<PostResponse>>(emptyList())
    val posts = _posts.asStateFlow()

    sealed class HomeFeedNavigationEvent {
        data class NavigateToPostDetail(val postId: String) : HomeFeedNavigationEvent()
    }

    init {
        viewModelScope.launch {
            newPostSharedFlow.collectLatest {
                loadAllPosts()
            }
        }
        // ✅ Tải dữ liệu ban đầu
        loadAllPosts()
    }

    fun loadAllPosts() {
        viewModelScope.launch {
            _uiState.value = PostEvent.Loading

            val response = postRepository.getAllPosts()

            when (response) {
                is ApiResponse.Success -> {
                    _uiState.value = PostEvent.Success
                    _posts.value = response.data.map {post ->
                        post
                    }
                }
                is ApiResponse.Error -> {
//                    _uiState.value = PostEvent.Error(response.code.getFeedErrorMessage())
                    if (response.code == 401) {
                        // Token hết hạn -> xoá token + điều hướng về login
                        session.clearToken()
                        _uiState.value = PostEvent.Error(
                            context.getString(R.string.feed_failed_unauthorized)
                        )
                        _navigationEvent.emit(HomeFeedNavigationEvent.NavigateToPostDetail("login"))
                    } else {
                        _uiState.value = PostEvent.Error(response.code.getFeedErrorMessage())
                    }
                }
                else -> {
                    _uiState.value = PostEvent.Error(
                        context.getString(R.string.feed_failed_network_error)
                    )
                }
            }
        }
    }

    fun onPostClick(postId: Int) {
        viewModelScope.launch {
            _navigationEvent.emit(HomeFeedNavigationEvent.NavigateToPostDetail(postId.toString()))
        }
    }

    fun updatePosts(updatedPosts: List<PostResponse>) {
        _posts.value = updatedPosts
    }

    fun updateCommentCount(postId: Int) {
        val currentPosts = _posts.value
        val updatedPosts = currentPosts.map { post ->
            if (post.postId == postId) {
                post.copy(commentCount = post.commentCount + 1)
            } else {
                post
            }
        }
        _posts.value = updatedPosts
    }

    // ✅ Hàm lắng nghe LikeViewModel để cập nhật like realtime
    fun observeLikeEvents(likeViewModel: LikeViewModel) {
        viewModelScope.launch {
            likeViewModel.likeEvent.collect { (postId, newLikeCount) ->
                _posts.update { currentPosts ->
                    currentPosts.map { post ->
                        if (post.postId == postId) post.copy(likeCount = newLikeCount)
                        else post
                    }
                }
            }
        }
    }

    private fun Int.getFeedErrorMessage(): String {
        return when (this) {
            400 -> context.getString(R.string.feed_failed_invalid_request)
            404 -> context.getString(R.string.feed_failed_not_found)
            500 -> context.getString(R.string.feed_failed_general_error)
            else -> context.getString(R.string.feed_failed_general_error)
        }
    }

    sealed class PostEvent {
        object Nothing : PostEvent()
        object Success : PostEvent()
        data class Error(val message: String) : PostEvent()
        object Loading : PostEvent()
    }

}