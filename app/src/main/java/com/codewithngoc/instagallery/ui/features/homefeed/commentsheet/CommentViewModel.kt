package com.codewithngoc.instagallery.ui.features.homefeed.commentsheet

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codewithngoc.instagallery.R
import com.codewithngoc.instagallery.data.InstaGalleryApi
import com.codewithngoc.instagallery.data.InstaGallerySession
import com.codewithngoc.instagallery.data.model.AddCommentRequest
import com.codewithngoc.instagallery.data.model.CommentResponse
import com.codewithngoc.instagallery.data.model.PostResponse
import com.codewithngoc.instagallery.data.remote.ApiResponse
import com.codewithngoc.instagallery.data.remote.safeApiCall
import com.codewithngoc.instagallery.data.repository.CommentRepository
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

@HiltViewModel
class CommentViewModel @Inject constructor(
    private val repository: CommentRepository,
    val session: InstaGallerySession,
    @ApplicationContext val context: Context
) : ViewModel() {

    // Lưu bình luận theo postId
    private val _commentsMap = MutableStateFlow<Map<Int, List<CommentResponse>>>(emptyMap())
    val commentsMap = _commentsMap.asStateFlow()

    private val _commentEvent = MutableStateFlow<CommentEvent>(CommentEvent.Nothing)
    val commentEvent = _commentEvent.asStateFlow()

    private val _newCommentEvent = MutableSharedFlow<Pair<Int, CommentResponse>>() // postId -> comment
    val newCommentEvent = _newCommentEvent.asSharedFlow()

    // Hàm lấy bình luận
    fun loadComments(postId: Int) {
        viewModelScope.launch {
            _commentEvent.value = CommentEvent.Loading
            val response = repository.getCommentsForPost(postId)
            when (response) {
                is ApiResponse.Success -> {
                    val comments = response.data

                    // Sắp xếp lại bình luận theo cấu trúc cha-con
                    val rootComments = comments.filter { it.parentCommentId == null }.sortedByDescending { it.createdAt }
                    val repliesMap = comments.filter { it.parentCommentId != null }.groupBy { it.parentCommentId }

                    val sortedComments = mutableListOf<CommentResponse>()
                    rootComments.forEach { root ->
                        sortedComments.add(root)
                        repliesMap[root.commentId]?.sortedBy { it.createdAt }?.let { replies ->
                            sortedComments.addAll(replies)
                        }
                    }

                    _commentsMap.value = _commentsMap.value + (postId to sortedComments)
                    _commentEvent.value = CommentEvent.Success
                }

                is ApiResponse.Error -> {
                    _commentEvent.value = CommentEvent.Error("Lỗi khi tải bình luận: ${response.message}")
                }

                is ApiResponse.Exception -> {
                    _commentEvent.value = CommentEvent.Error("Lỗi mạng: ${response.exception.message}")
                }
            }
        }
    }

    // Thêm bình luận cho 1 post
    fun addComment(postId: Int, content: String, parentCommentId: Int? = null) {
        viewModelScope.launch {
            _commentEvent.value = CommentEvent.Loading
            val request = AddCommentRequest(content = content, parentCommentId = parentCommentId)
            val response = repository.addComment(postId, request)

            when (response) {
                is ApiResponse.Success -> {

                    val newComment = response.data
                    val currentComments = _commentsMap.value[postId] ?: emptyList()
                    // Logic để chèn bình luận con vào đúng vị trí
                    val updatedComments = if (parentCommentId != null) {
                        val parentIndex = currentComments.indexOfFirst { it.commentId == parentCommentId }
                        if (parentIndex != -1) {
                            // Chèn bình luận con ngay sau bình luận cha
                            currentComments.toMutableList().apply {
                                add(parentIndex + 1, newComment)
                            }
                        } else {
                            // Nếu không tìm thấy bình luận cha, thêm vào đầu
                            listOf(newComment) + currentComments
                        }
                    } else {
                        // Nếu là bình luận cha, thêm vào đầu danh sách
                        listOf(newComment) + currentComments
                    }

                    _commentsMap.value = _commentsMap.value + (postId to updatedComments)
                    _commentEvent.value = CommentEvent.Success
                    _newCommentEvent.emit(postId to newComment)
                }

                is ApiResponse.Error -> {
                    _commentEvent.value = CommentEvent.Error("Lỗi khi gửi bình luận: ${response.message}")
                }

                is ApiResponse.Exception -> {
                    _commentEvent.value = CommentEvent.Error("Lỗi mạng: ${response.exception.message}")
                }
            }
        }
    }

    sealed class CommentEvent {
        object Nothing : CommentEvent()
        object Loading : CommentEvent()
        object Success : CommentEvent()
        data class Error(val message: String) : CommentEvent()
    }
}