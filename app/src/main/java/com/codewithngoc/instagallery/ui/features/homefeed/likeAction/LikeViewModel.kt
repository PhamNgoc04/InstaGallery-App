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

    // Trạng thái like action
    private val _likeState = MutableStateFlow<LikeEvent>(LikeEvent.Nothing)
    val likeState = _likeState.asStateFlow()

    // Lưu trạng thái like cho từng postId
    private val _likedPosts = MutableStateFlow<Map<Int, Boolean>>(emptyMap())
    val likedPosts = _likedPosts.asStateFlow()

    // Flow để HomeFeedViewModel lắng nghe số like mới
    private val _likeEvent = MutableSharedFlow<Pair<Int, Int>>(extraBufferCapacity = 10)   // (postId, newLikeCount)
    val likeEvent: SharedFlow<Pair<Int, Int>> = _likeEvent.asSharedFlow()

    // Kiểm tra đã like chưa
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

    // Toggle like/unlike
    // Chức năng: Cập nhật tạm thời UI ngay, gọi API like/unlike và lấy số like thật từ server
    fun toggleLike(postId: Int, currentLikeCount: Int) {
        viewModelScope.launch {
            val isLiked = _likedPosts.value[postId] ?: false

            // ✅ Cập nhật cục bộ UI trước
            val tempChange = if (isLiked) -1 else 1
            _likeEvent.emit(postId to (currentLikeCount + tempChange))

            val response = if (isLiked) likeRepository.unlikePost(postId)
            else likeRepository.likePost(postId)

            when(response) {
                is ApiResponse.Success -> {
                    // ✅ Cập nhật trạng thái liked
                    _likedPosts.value = _likedPosts.value.toMutableMap().apply { put(postId, !isLiked) }

                    // ✅ Lấy số like thật từ server
                    val countResponse = likeRepository.getLikeCount(postId)
                    val newCount = if (countResponse is ApiResponse.Success) countResponse.data else currentLikeCount + tempChange

                    _likeEvent.emit(postId to newCount)

                    _likeState.value = LikeEvent.ActionSuccess(
                        MessageResponse(if (isLiked) "💔 Bỏ like thành công" else "❤️ Like thành công")
                    )
                }
                is ApiResponse.Error -> _likeState.value = LikeEvent.Error(response.message)
                is ApiResponse.Exception -> _likeState.value = LikeEvent.Error(
                    context.getString(R.string.feed_failed_network_error)
                )
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