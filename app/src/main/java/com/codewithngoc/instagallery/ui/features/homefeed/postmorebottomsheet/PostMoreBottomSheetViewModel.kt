package com.codewithngoc.instagallery.ui.features.homefeed.postmorebottomsheet

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codewithngoc.instagallery.R
import com.codewithngoc.instagallery.data.InstaGalleryApi
import com.codewithngoc.instagallery.data.InstaGallerySession
import com.codewithngoc.instagallery.data.remote.ApiResponse
import com.codewithngoc.instagallery.data.remote.safeApiCall
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PostMoreBottomSheetViewModel @Inject constructor(
    private val instaGalleryApi: InstaGalleryApi,
    private val session: InstaGallerySession,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiEvent = MutableStateFlow<MoreEvent>(MoreEvent.Nothing)
    val uiEvent = _uiEvent.asStateFlow()

    // You can add more state for specific actions, e.g.,
    private val _isPostSaved = MutableStateFlow(false)
    val isPostSaved = _isPostSaved.asStateFlow()

    sealed class MoreEvent {
        object Nothing : MoreEvent()
        object Loading : MoreEvent()
        object Success : MoreEvent()
        data class Error(val message: String) : MoreEvent()
    }

    // Function to handle the "Save" action
    fun savePost(postId: Long) {
        viewModelScope.launch {
            _uiEvent.value = MoreEvent.Loading
            // TODO: Implement API call to save the post
            _uiEvent.value = MoreEvent.Success // Simulate success
            _isPostSaved.value = true
        }
    }

    // Function to handle the "Unfollow" action
    fun unfollowUser(userId: Long) {
        viewModelScope.launch {
            _uiEvent.value = MoreEvent.Loading
            // TODO: Implement API call to unfollow the user
            _uiEvent.value = MoreEvent.Success // Simulate success
        }
    }

    // Function to handle the "Report" action
    fun reportPost(postId: Long) {
        viewModelScope.launch {
            _uiEvent.value = MoreEvent.Loading
            // TODO: Implement API call to report the post
            _uiEvent.value = MoreEvent.Success // Simulate success
        }
    }
}