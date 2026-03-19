package com.codewithngoc.instagallery.ui.features.homefeed.commentsheet

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codewithngoc.instagallery.R
import com.codewithngoc.instagallery.data.InstaGallerySession
import com.codewithngoc.instagallery.data.model.AddCommentRequest
import com.codewithngoc.instagallery.data.model.CommentResponse
import com.codewithngoc.instagallery.data.remote.ApiResponse
import com.codewithngoc.instagallery.data.repository.CommentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CommentViewModel @Inject constructor(
    private val repository: CommentRepository,
    val session: InstaGallerySession,
    @ApplicationContext val context: Context
) : ViewModel() {

    // Lưu bình luận theo postId
    private val _commentsMap = MutableStateFlow<Map<Long, List<CommentResponse>>>(emptyMap())
    val commentsMap = _commentsMap.asStateFlow()

    private val _commentEvent = MutableStateFlow<CommentEvent>(CommentEvent.Nothing)
    val commentEvent = _commentEvent.asStateFlow()

    private val _newCommentEvent = MutableSharedFlow<Pair<Long, CommentResponse>>() // postId -> comment
    val newCommentEvent = _newCommentEvent.asSharedFlow()

    // Sắp xếp comment theo cấu trúc cha - con (đệ quy)
    private fun sortComments(comments: List<CommentResponse>): List<CommentResponse> {
        val repliesMap = comments.filter { it.parentId != null }.groupBy { it.parentId }
        val rootComments = comments.filter { it.parentId == null }.sortedByDescending { it.createdAt }

        val sortedList = mutableListOf<CommentResponse>()

        fun addReplies(comment: CommentResponse) {
            sortedList.add(comment)
            repliesMap[comment.commentId]?.sortedBy { it.createdAt }?.forEach { reply ->
                addReplies(reply) // đệ quy với reply
            }
        }

        rootComments.forEach { root ->
            addReplies(root)
        }

        return sortedList
    }

    // Hàm lấy bình luận
    fun loadComments(postId: Long) {
        viewModelScope.launch {
            _commentEvent.value = CommentEvent.Loading
            val response = repository.getCommentsForPost(postId)
            when (response) {
                is ApiResponse.Success -> {
                    val comments = response.data.comments

                    val sortedComments = sortComments(comments)

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
    fun addComment(postId: Long, content: String, parentId: Long? = null) {
        viewModelScope.launch {
            _commentEvent.value = CommentEvent.Loading
            val request = AddCommentRequest(content = content, parentId = parentId)
            val response = repository.addComment(postId, request)

            when (response) {
                is ApiResponse.Success -> {

                    val newComment = response.data
                    val currentComments = _commentsMap.value[postId] ?: emptyList()
                    // Logic để chèn bình luận con vào đúng vị trí
                    val updatedComments = if (parentId != null) {
                        val parentIndex = currentComments.indexOfFirst { it.commentId == parentId }
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