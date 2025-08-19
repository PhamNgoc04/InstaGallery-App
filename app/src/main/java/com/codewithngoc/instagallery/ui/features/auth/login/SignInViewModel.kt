package com.codewithngoc.instagallery.ui.features.auth.login

import android.content.Context
import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codewithngoc.instagallery.R
import com.codewithngoc.instagallery.data.InstaGalleryApi
import com.codewithngoc.instagallery.data.InstaGallerySession
import com.codewithngoc.instagallery.data.model.SignInRequest
import com.codewithngoc.instagallery.data.remote.ApiResponse
import com.codewithngoc.instagallery.data.remote.safeApiCall
import com.codewithngoc.instagallery.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val repository: AuthRepository,
    val session: InstaGallerySession,
    @ApplicationContext val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow<SignInEvent>(SignInEvent.Nothing)
    val uiState = _uiState.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<SignInNavigationEvent>()
    val navigationEvent = _navigationEvent.asSharedFlow()

    private val _email = MutableStateFlow("")
    val email = _email.asStateFlow()

    private val _password = MutableStateFlow("")
    val password = _password.asStateFlow()

    private val _emailError = MutableStateFlow<String?>(null)
    val emailError = _emailError.asStateFlow()

    private val _passwordError = MutableStateFlow<String?>(null)
    val passwordError = _passwordError.asStateFlow()

    companion object {
        private const val MIN_PASSWORD_LENGTH = 6
        private val GMAIL_REGEX = "^[A-Za-z0-9+_.-]+@gmail\\.com$".toRegex()
    }

    private fun validateEmailRealtime(input: String) : String? {
        return if (input.isNotEmpty()) {
            if (!GMAIL_REGEX.matches(input)) "Email không hợp lệ" else null
        } else null
    }

    private fun validatePasswordRealtime(input: String) : String? {
        return if (input.isNotEmpty()) {
            if (input.length < MIN_PASSWORD_LENGTH) "Mật khẩu phải có ít nhất $MIN_PASSWORD_LENGTH ký tự" else null
        } else null
    }

    fun onEmailChange(email : String) {
        _email.value = email
        _emailError.value = validateEmailRealtime(email)
    }

    fun onPasswordChange(password : String) {
        _password.value = password
        _passwordError.value = validatePasswordRealtime(password)
    }

    fun onSignInClick() {
        val emailValue = _email.value.trim()
        val passwordValue = _password.value

        val emailErr = if (emailValue.isBlank()) "Email không được để trống"
                        else validateEmailRealtime(emailValue)
        val passwordErr = if (passwordValue.isBlank()) "Mật khẩu không được để trống"
                           else validatePasswordRealtime(passwordValue)

        _emailError.value = emailErr
        _passwordError.value = passwordErr

       if (emailErr != null || passwordErr != null) return

        viewModelScope.launch {
            _uiState.value = SignInEvent.Loading
            val response = repository.signIn(
                SignInRequest(
                    email = _email.value,
                    password = _password.value
                )
            )

            // Xử lý kết quả trả về
            when (response) {
                is ApiResponse.Success -> {
                    session.storeToken(response.data.token)
                    session.storeUserId(response.data.userId)

                    _uiState.value = SignInEvent.Success
                    _navigationEvent.emit(SignInNavigationEvent.NavigateToHomeFeed)
                }

                is ApiResponse.Error -> {
                    _uiState.value = SignInEvent.Error(response.code.getLoginErrorMessage())
                }

                else -> {
                    _uiState.value = SignInEvent.Error(
                        context.getString(R.string.login_failed_network_error)
                    )
                }
            }
        }
    }

    // Hàm mở rộng để lấy thông báo lỗi dựa trên mã lỗi
    private fun Int.getLoginErrorMessage() : String {
        return when (this) {
            400 -> context.getString(R.string.login_failed_invalid_request)
            401 -> context.getString(R.string.login_failed_invalid_credentials)
            403 -> context.getString(R.string.login_failed_account_locked)
            500 -> context.getString(R.string.login_failed_general_error)
            else -> context.getString(R.string.login_failed_general_error)
        }
    }

    // Lớp đại diện cho các sự kiện điều hướng
    sealed class SignInNavigationEvent {
        object NavigateToSignUp : SignInNavigationEvent()
        object NavigateToHomeFeed : SignInNavigationEvent()
    }

    // Lớp đại diện cho các trạng thái UI
    sealed class SignInEvent {
        object Nothing : SignInEvent()
        object Success : SignInEvent()
        data class Error(val message: String) : SignInEvent()
        object Loading : SignInEvent()
    }

    // Lớp đại diện cho các lỗi xác thực
    sealed class AuthError {
        object InvalidCredentials : AuthError()
        object NetworkError : AuthError()
        object AccountLocked : AuthError()
        object GeneralError : AuthError()
    }

}

