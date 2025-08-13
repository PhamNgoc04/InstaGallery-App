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
import com.codewithngoc.instagallery.data.repository.PostRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

// SharedFlow để thông báo khi một bài viết mới được tạo thành công
val newPostSharedFlow = MutableSharedFlow<Unit>(extraBufferCapacity = 1)

@HiltViewModel
class HomeFeedViewModel @Inject constructor(
    private val repository: PostRepository,
    val session: InstaGallerySession,
    @ApplicationContext val context: Context
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

            val response = repository.getAllPosts()

            when (response) {
                is ApiResponse.Success -> {
                    _uiState.value = PostEvent.Success
                    _posts.value = response.data
                }
                is ApiResponse.Error -> {
                    _uiState.value = PostEvent.Error(response.code.getFeedErrorMessage())
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