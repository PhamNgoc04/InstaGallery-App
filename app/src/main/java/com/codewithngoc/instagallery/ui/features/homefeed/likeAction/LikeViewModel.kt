package com.codewithngoc.instagallery.ui.features.homefeed.likeAction

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codewithngoc.instagallery.R
import com.codewithngoc.instagallery.data.model.LikeResponse
import com.codewithngoc.instagallery.data.model.MessageResponse
import com.codewithngoc.instagallery.data.remote.ApiResponse
import com.codewithngoc.instagallery.data.repository.LikeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LikeViewModel @Inject constructor(
    private val likeRepository: LikeRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _likeState = MutableStateFlow<LikeEvent>(LikeEvent.Nothing)
    val likeState = _likeState.asStateFlow()

    // Lưu trạng thái like cho từng postId
    private val _likedPosts = MutableStateFlow<Map<Int, Boolean>>(emptyMap())
    val likedPosts = _likedPosts.asStateFlow()

    // Flow để HomeFeedViewModel lắng nghe số like mới
    private val _likeEvent = MutableSharedFlow<Pair<Int, Int>>()   // (postId, newLikeCount)
    val likeEvent: SharedFlow<Pair<Int, Int>> = _likeEvent.asSharedFlow()

    fun loadLikes(postId: Int) {
        viewModelScope.launch {
            _likeState.value = LikeEvent.Loading
            when (val response = likeRepository.getLikes(postId, 1, 20)) {
                is ApiResponse.Success -> {
                    _likeState.value = LikeEvent.LikesLoaded(response.data)
                }
                is ApiResponse.Error -> {
                    _likeState.value = LikeEvent.Error(response.message)
                }
                is ApiResponse.Exception -> {
                    _likeState.value = LikeEvent.Error(
                        context.getString(R.string.feed_failed_network_error)
                    )
                }
            }
        }
    }

    fun checkLiked(postId: Int) {
        viewModelScope.launch {
            when (val response = likeRepository.checkLiked(postId)) {
                is ApiResponse.Success -> {
                    _likedPosts.value = _likedPosts.value.toMutableMap()
                        .apply { put(postId, response.data.liked) }
                }
                else -> { /* ignore */ }
            }
        }
    }


    // Fetch số like hiện tại từ server
    fun fetchLikeCount(postId: Int) {
        viewModelScope.launch {
            when (val response = likeRepository.getLikeCount(postId)) {
                is ApiResponse.Success -> _likeEvent.emit(postId to response.data)
                is ApiResponse.Error -> { /* handle error */ }
                is ApiResponse.Exception -> { /* handle network error */ }
            }
        }
    }

    fun toggleLike(postId: Int) {
        viewModelScope.launch {
            val isLiked = _likedPosts.value[postId] ?: false
            if (isLiked) {
                when (val response = likeRepository.unlikePost(postId)) {
                    is ApiResponse.Success -> {
                        // ✅ Cập nhật trạng thái likedPosts
                        _likedPosts.value = _likedPosts.value.toMutableMap()
                            .apply { put(postId, false) }

                        // Lấy số like mới từ server
                        val newCount: Int = response.data.likeCount
                        _likeEvent.emit(postId to newCount)


                        _likeState.value = LikeEvent.ActionSuccess(MessageResponse("💔 Bỏ like thành công"))
                    }
                    is ApiResponse.Error -> {
                        _likeState.value = LikeEvent.Error(response.message)
                    }
                    is ApiResponse.Exception -> {
                        _likeState.value = LikeEvent.Error(
                            context.getString(R.string.feed_failed_network_error)
                        )
                    }
                }
            } else {
                when (val response = likeRepository.likePost(postId)) {
                    is ApiResponse.Success -> {
                        // ✅ Cập nhật trạng thái likedPosts
                        _likedPosts.value = _likedPosts.value.toMutableMap()
                            .apply { put(postId, true) }

                        val newCount: Int = response.data.likeCount
                        _likeEvent.emit(postId to newCount)

                        _likeState.value = LikeEvent.ActionSuccess(MessageResponse("❤️ Like thành công"))
                    }
                    is ApiResponse.Error -> {
                        _likeState.value = LikeEvent.Error(response.message)
                    }
                    is ApiResponse.Exception -> {
                        _likeState.value = LikeEvent.Error(
                            context.getString(R.string.feed_failed_network_error)
                        )
                    }
                }
            }
        }
    }

    sealed class LikeEvent {
        object Nothing : LikeEvent()
        object Loading : LikeEvent()
        data class LikesLoaded(val likes: List<LikeResponse>) : LikeEvent()
        data class ActionSuccess(val message: MessageResponse) : LikeEvent()
        data class Error(val message: String) : LikeEvent()
    }
}