package com.codewithngoc.instagallery.ui.features.auth.resetpassword

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codewithngoc.instagallery.data.remote.ApiResponse
import com.codewithngoc.instagallery.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ResetPasswordViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _isSuccess = MutableStateFlow(false)
    val isSuccess: StateFlow<Boolean> = _isSuccess.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun resetPassword(otp: String, newPassword: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            when (val res = authRepository.resetPassword(otp, newPassword)) {
                is ApiResponse.Success -> {
                    _isSuccess.value = true
                }
                is ApiResponse.Error -> {
                    _error.value = res.formatMsg()
                }
                is ApiResponse.Exception -> {
                    _error.value = res.exception.message
                }
            }
            _isLoading.value = false
        }
    }
}
