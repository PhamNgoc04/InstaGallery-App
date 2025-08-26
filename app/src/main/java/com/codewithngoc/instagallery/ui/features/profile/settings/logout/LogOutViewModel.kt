package com.codewithngoc.instagallery.ui.features.profile.settings.logout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codewithngoc.instagallery.data.InstaGallerySession
import com.codewithngoc.instagallery.data.remote.ApiResponse
import com.codewithngoc.instagallery.data.repository.LogOutRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LogOutViewModel @Inject constructor(
    private val repository: LogOutRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<LogOutEvent>(LogOutEvent.Idle)
    val uiState = _uiState.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<Unit>()
    val navigationEvent = _navigationEvent.asSharedFlow()

    fun logout() {
        viewModelScope.launch {
            _uiState.value = LogOutEvent.Loading
            val result = repository.logout()
            when(result) {
                is ApiResponse.Success -> {
                    _uiState.value = LogOutEvent.Success
                    _navigationEvent.emit(Unit) // để điều hướng về login
                }
                is ApiResponse.Error -> {
                    _uiState.value = LogOutEvent.Error(result.message ?: "Logout failed")
                }
                else -> { // thêm branch else để cover các trường hợp còn lại
                    _uiState.value = LogOutEvent.Error("Logout failed: unknown error")
                }
            }
        }
    }

    sealed class LogOutEvent {
        object Idle : LogOutEvent()
        object Loading : LogOutEvent()
        object Success : LogOutEvent()
        data class Error(val message: String) : LogOutEvent()
    }
}
