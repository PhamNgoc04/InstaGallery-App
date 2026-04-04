package com.codewithngoc.instagallery.ui.features.profile.settings.changepassword

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codewithngoc.instagallery.data.model.ChangePasswordRequest
import com.codewithngoc.instagallery.data.remote.ApiResponse
import com.codewithngoc.instagallery.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class ChangePasswordState {
    object Idle : ChangePasswordState()
    object Loading : ChangePasswordState()
    data class Success(val message: String) : ChangePasswordState()
    data class Error(val message: String) : ChangePasswordState()
}

@HiltViewModel
class ChangePasswordViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ChangePasswordState>(ChangePasswordState.Idle)
    val uiState: StateFlow<ChangePasswordState> = _uiState.asStateFlow()

    fun changePassword(current: String, new: String) {
        viewModelScope.launch {
            _uiState.value = ChangePasswordState.Loading
            
            val request = ChangePasswordRequest(
                currentPassword = current,
                newPassword = new
            )

            when (val response = authRepository.changePassword(request)) {
                is ApiResponse.Success -> {
                    _uiState.value = ChangePasswordState.Success("Đổi mật khẩu thành công!")
                }
                is ApiResponse.Error -> {
                    _uiState.value = ChangePasswordState.Error(response.message)
                }
                else -> {
                    _uiState.value = ChangePasswordState.Error("Lỗi hệ thống")
                }
            }
        }
    }
    
    fun resetState() {
        _uiState.value = ChangePasswordState.Idle
    }
}
