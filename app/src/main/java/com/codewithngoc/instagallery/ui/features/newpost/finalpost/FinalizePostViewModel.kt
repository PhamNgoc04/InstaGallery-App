package com.codewithngoc.instagallery.ui.features.newpost.finalpost

import android.content.Context
import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codewithngoc.instagallery.R
import com.codewithngoc.instagallery.data.InstaGalleryApi
import com.codewithngoc.instagallery.data.InstaGallerySession
import com.codewithngoc.instagallery.data.model.*
import com.codewithngoc.instagallery.data.remote.ApiResponse
import com.codewithngoc.instagallery.data.remote.safeApiCall
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FinalizePostViewModel @Inject constructor(
    private val instaGalleryApi: InstaGalleryApi,
    private val session: InstaGallerySession,
    @ApplicationContext private val context: Context,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    // Lấy URI ảnh từ SavedStateHandle
    private val _selectedMediaUri = MutableStateFlow<Uri?>(null)
    val selectedMediaUri = _selectedMediaUri.asStateFlow()

    private val _caption = MutableStateFlow("")
    val caption = _caption.asStateFlow()

    private val _uiState = MutableStateFlow<FinalizePostEvent>(FinalizePostEvent.Idle)
    val uiState = _uiState.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<FinalizePostNavigationEvent>()
    val navigationEvent = _navigationEvent.asSharedFlow()

    init {
        savedStateHandle.get<String>("uri")?.let { uriString ->
            _selectedMediaUri.value = Uri.parse(Uri.decode(uriString))
        }
    }

    sealed class FinalizePostNavigationEvent {
        object NavigateToHomeFeed : FinalizePostNavigationEvent()
    }

    sealed class FinalizePostEvent {
        object Idle : FinalizePostEvent()
        object Loading : FinalizePostEvent()
        object Success : FinalizePostEvent()
        data class Error(val message: String) : FinalizePostEvent()
    }

    fun onCaptionChanged(newCaption: String) {
        _caption.value = newCaption
    }

    fun uploadPost() {
        viewModelScope.launch {
            _uiState.value = FinalizePostEvent.Loading

            val userId = session.getUserId()
            val mediaUri = _selectedMediaUri.value

            if (userId == null || mediaUri == null) {
                _uiState.value = FinalizePostEvent.Error("Dữ liệu không hợp lệ. Vui lòng thử lại.")
                return@launch
            }

            // TODO: Gửi file lên server và nhận URL
            // Đây là một bước mô phỏng. Trong thực tế, bạn sẽ cần một service để upload file.
            val uploadedMediaUrl = "https://your-server.com/uploaded_media/${mediaUri.lastPathSegment}.jpg"

            val createRequest = CreatePostRequest(
                caption = _caption.value,
                visibility = PostVisibility.PUBLIC,
                location = null, // TODO: Lấy vị trí thực tế
                media = listOf(
                    MediaItem(
                        mediaFileUrl = uploadedMediaUrl,
                        mediaType = MediaType.IMAGE,
                        position = 0
                    )
                )
            )

            val response = safeApiCall {
                instaGalleryApi.createPost(createRequest)
            }

            when (response) {
                is ApiResponse.Success -> {
                    _uiState.value = FinalizePostEvent.Success
                    _navigationEvent.emit(FinalizePostNavigationEvent.NavigateToHomeFeed)
                }
                is ApiResponse.Error -> {
                    _uiState.value = FinalizePostEvent.Error("Lỗi ${response.code}: ${response.message}")
                }
                is ApiResponse.Exception -> {
                    _uiState.value = FinalizePostEvent.Error("Lỗi mạng: ${response.exception.localizedMessage}")
                }
            }
        }
    }
}