package com.codewithngoc.instagallery.ui.features.homefeed.likeAction

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codewithngoc.instagallery.R
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

    // Trạng thái like action
    private val _likeState = MutableStateFlow<LikeEvent>(LikeEvent.Nothing)
    val likeState = _likeState.asStateFlow()

    // Lưu trạng thái like cho từng postId
    private val _likedPosts = MutableStateFlow<Map<Long, Boolean>>(emptyMap())
    val likedPosts = _likedPosts.asStateFlow()

    // Flow để HomeFeedViewModel lắng nghe số like mới
    private val _likeEvent = MutableSharedFlow<Pair<Long, Int>>(extraBufferCapacity = 10)   // (postId, newLikeCount)
    val likeEvent: SharedFlow<Pair<Long, Int>> = _likeEvent.asSharedFlow()

    /**
     * Toggle like: gọi 1 API duy nhất, backend tự toggle.
     * Response trả về isLiked (true/false) và totalLikes.
     */
    fun toggleLike(postId: Long, currentLikeCount: Int) {
        viewModelScope.launch {
            val isCurrentlyLiked = _likedPosts.value[postId] ?: false

            // ✅ Cập nhật cục bộ UI trước (optimistic update)
            val tempChange = if (isCurrentlyLiked) -1 else 1
            _likeEvent.emit(postId to (currentLikeCount + tempChange))
            _likedPosts.value = _likedPosts.value.toMutableMap().apply { put(postId, !isCurrentlyLiked) }

            val response = likeRepository.toggleLike(postId)

            when (response) {
                is ApiResponse.Success -> {
                    // ✅ Cập nhật từ server response (source of truth)
                    _likedPosts.value = _likedPosts.value.toMutableMap().apply {
                        put(postId, response.data.isLiked)
                    }
                    _likeEvent.emit(postId to response.data.totalLikes)

                    _likeState.value = LikeEvent.ActionSuccess(
                        MessageResponse(
                            if (response.data.isLiked) "❤️ Like thành công" else "💔 Bỏ like thành công"
                        )
                    )
                }
                is ApiResponse.Error -> {
                    // Revert optimistic update
                    _likedPosts.value = _likedPosts.value.toMutableMap().apply { put(postId, isCurrentlyLiked) }
                    _likeEvent.emit(postId to currentLikeCount)
                    _likeState.value = LikeEvent.Error(response.message)
                }
                is ApiResponse.Exception -> {
                    // Revert optimistic update
                    _likedPosts.value = _likedPosts.value.toMutableMap().apply { put(postId, isCurrentlyLiked) }
                    _likeEvent.emit(postId to currentLikeCount)
                    _likeState.value = LikeEvent.Error(
                        context.getString(R.string.feed_failed_network_error)
                    )
                }
            }
        }
    }

    /**
     * Đặt trạng thái liked cho post (dùng khi load feed biết sẵn trạng thái)
     */
    fun setLikedState(postId: Long, isLiked: Boolean) {
        _likedPosts.value = _likedPosts.value.toMutableMap().apply { put(postId, isLiked) }
    }

    sealed class LikeEvent {
        object Nothing : LikeEvent()
        object Loading : LikeEvent()
        data class ActionSuccess(val message: MessageResponse) : LikeEvent()
        data class Error(val message: String) : LikeEvent()
    }
}