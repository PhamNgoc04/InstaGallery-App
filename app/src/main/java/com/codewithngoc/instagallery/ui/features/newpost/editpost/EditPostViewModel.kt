package com.codewithngoc.instagallery.ui.features.newpost.editpost

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
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
import com.codewithngoc.instagallery.data.utils.uriToMultipart
import com.codewithngoc.instagallery.ui.features.homefeed.HomeFeedViewModel
import com.codewithngoc.instagallery.ui.navigation.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditPostViewModel @Inject constructor(
    private val instaGalleryApi: InstaGalleryApi,
    private val session: InstaGallerySession,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow<EditPostEvent>(EditPostEvent.Nothing)
    val uiState = _uiState.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<EditPostNavigationEvent>()
    val navigationEvent = _navigationEvent.asSharedFlow()

    private val _selectedMediaUri = MutableStateFlow<Uri?>(null)
    val selectedMediaUri = _selectedMediaUri.asStateFlow()

    private val _caption = MutableStateFlow("")
    val caption = _caption.asStateFlow()

    private val _location = MutableStateFlow("")
    val location = _location.asStateFlow()

    sealed class EditPostNavigationEvent {
        object NavigateBackToHome : EditPostNavigationEvent()
    }

    sealed class EditPostEvent {
        object Nothing : EditPostEvent()
        object Loading : EditPostEvent()
        object Success : EditPostEvent()
        data class Error(val message: String) : EditPostEvent()
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

    fun uploadAndCreatePost(
        caption: String,
        imageUri: Uri,
        context: Context,
        navController: NavController,
        homeFeedViewModel: HomeFeedViewModel
    ) {
        viewModelScope.launch {
            _uiState.value = EditPostEvent.Loading
            try {
                val uri = selectedMediaUri.value ?: return@launch
                val multipart = context.uriToMultipart("file", uri)

                val uploadResult = instaGalleryApi.uploadFile(multipart)
                if (uploadResult.isSuccessful) {
                    val uploadedUrl = uploadResult.body()?.url
                    if (!uploadedUrl.isNullOrEmpty()) {
                        val createRequest = CreatePostRequest(
                            caption = caption,
                            visibility = PostVisibility.PUBLIC,
                            location = "",
                            media = listOf(
                                MediaItem(
                                    mediaFileUrl = uploadedUrl,
                                    mediaType = MediaType.IMAGE,
                                    position = 0
                                )
                            )
                        )

                        val postCreationResponse = instaGalleryApi.createPost(createRequest)
                        if (postCreationResponse.isSuccessful && postCreationResponse.body() != null) {
                            val newPost = postCreationResponse.body()!!

                            _uiState.value = EditPostEvent.Success

                            // Load lại toàn bộ để đồng bộ dữ liệu
                            homeFeedViewModel.loadAllPosts()

                            // Quay lại màn hình Home
                            navController.navigate(Screen.HomeFeed.route) {
                                popUpTo(Screen.HomeFeed.route) { inclusive = true }
                            }
                        } else {
                            _uiState.value = EditPostEvent.Error("Tạo bài viết thất bại")
                        }
                    } else {
                        _uiState.value = EditPostEvent.Error("Không nhận được URL ảnh")
                    }
                } else {
                    _uiState.value = EditPostEvent.Error("Upload thất bại")
                }
            } catch (e: Exception) {
                _uiState.value = EditPostEvent.Error("Lỗi: ${e.message}")
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
