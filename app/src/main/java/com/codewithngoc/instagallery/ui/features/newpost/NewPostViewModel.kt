package com.codewithngoc.instagallery.ui.features.newpost

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codewithngoc.instagallery.R
import com.codewithngoc.instagallery.data.InstaGalleryApi
import com.codewithngoc.instagallery.data.InstaGallerySession
import com.codewithngoc.instagallery.data.model.CreatePostRequest
import com.codewithngoc.instagallery.data.model.MediaItem
import com.codewithngoc.instagallery.data.model.MediaType
import com.codewithngoc.instagallery.data.model.PostResponse
import com.codewithngoc.instagallery.data.model.PostVisibility
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
class NewPostViewModel @Inject constructor(
    private val instaGalleryApi: InstaGalleryApi,
    private val session: InstaGallerySession,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow<NewPostEvent>(NewPostEvent.Nothing)
    val uiState = _uiState.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<NewPostNavigationEvent>()
    val navigationEvent = _navigationEvent.asSharedFlow()

    private val _selectedMediaUri = MutableStateFlow<Uri?>(null)
    val selectedMediaUri = _selectedMediaUri.asStateFlow()

    private val _caption = MutableStateFlow("")
    val caption = _caption.asStateFlow()

    private val _location = MutableStateFlow("")
    val location = _location.asStateFlow()

    sealed class NewPostNavigationEvent {
        object NavigateToHomeFeed : NewPostNavigationEvent()
    }

    sealed class NewPostEvent {
        object Nothing : NewPostEvent()
        object Loading : NewPostEvent()
        object Success : NewPostEvent()
        data class Error(val message: String) : NewPostEvent()
    }

    fun onMediaSelected(uri: Uri) {
        _selectedMediaUri.value = uri
    }

    fun onCaptionChanged(newCaption: String) {
        _caption.value = newCaption
    }

    fun onLocationChanged(newLocation: String) {
        _location.value = newLocation
    }

    fun uploadPost() {
        viewModelScope.launch {
            _uiState.value = NewPostEvent.Loading

            val userId = session.getUserId()
            val mediaUri = _selectedMediaUri.value

            if (userId == null || mediaUri == null) {
                _uiState.value = NewPostEvent.Error(
                    context.getString(R.string.post_failed_missing_data)
                )
                return@launch
            }

            val createRequest = CreatePostRequest(
                caption = _caption.value,
                visibility = PostVisibility.PUBLIC,
                location = _location.value,
                media = listOf(
                    MediaItem(
                        mediaFileUrl = mediaUri.toString(), // Tạm thời dùng URI local, cần upload file trước
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
                    _uiState.value = NewPostEvent.Success
                    _navigationEvent.emit(NewPostNavigationEvent.NavigateToHomeFeed)
                }
                is ApiResponse.Error -> {
                    _uiState.value = NewPostEvent.Error(response.code.getPostErrorMessage())
                }
                is ApiResponse.Exception -> {
                    _uiState.value = NewPostEvent.Error(
                        context.getString(R.string.post_failed_network_error)
                    )
                }
            }
        }
    }

    private fun Int.getPostErrorMessage(): String {
        return when (this) {
            400 -> context.getString(R.string.post_failed_invalid_request)
            401 -> context.getString(R.string.post_failed_unauthorized)
            500 -> context.getString(R.string.post_failed_server_error)
            else -> context.getString(R.string.post_failed_general_error)
        }
    }
}
