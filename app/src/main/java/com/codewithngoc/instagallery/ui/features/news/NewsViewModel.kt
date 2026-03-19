package com.codewithngoc.instagallery.ui.features.news

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codewithngoc.instagallery.data.model.FeedPostResponse
import com.codewithngoc.instagallery.data.remote.ApiResponse
import com.codewithngoc.instagallery.data.repository.PostRepository
import com.codewithngoc.instagallery.ui.features.news.NewsViewModel.NewsUiEvent.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewsViewModel @Inject constructor(
    private val postRepository: PostRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<NewsUiEvent>(NewsUiEvent.Nothing)
    val uiState = _uiState.asStateFlow()

    private val _posts = MutableStateFlow<List<FeedPostResponse>>(emptyList())
    val posts = _posts.asStateFlow()

    sealed class NewsUiEvent {
        object Nothing : NewsUiEvent()
        object Success : NewsUiEvent()
        data class Error(val message: String) : NewsUiEvent()
        object Loading : NewsUiEvent()
    }

    init {
        fetchNews()
    }

    fun fetchNews() {
        viewModelScope.launch {
            _uiState.value = NewsUiEvent.Loading
            when (val response = postRepository.getFeed(page = 1, limit = 50)) {
                is ApiResponse.Success -> {
                    _uiState.value = NewsUiEvent.Success
                    _posts.value = response.data.posts
                }
                is ApiResponse.Error -> {
                    _uiState.value = Error(response.message)
                }
                is ApiResponse.Exception -> {
                    _uiState.value = Error("Lỗi mạng: ${response.exception.message}")
                }
            }
        }
    }
}