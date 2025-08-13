package com.codewithngoc.instagallery.ui.features.auth.signup

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.codewithngoc.instagallery.R
import com.codewithngoc.instagallery.data.InstaGalleryApi
import com.codewithngoc.instagallery.data.InstaGallerySession
import com.codewithngoc.instagallery.data.model.SignUpRequest
import com.codewithngoc.instagallery.data.remote.ApiResponse
import com.codewithngoc.instagallery.data.remote.safeApiCall
import com.codewithngoc.instagallery.data.repository.AuthRepository
import com.codewithngoc.instagallery.ui.features.auth.login.SignInViewModel
import com.codewithngoc.instagallery.ui.features.auth.login.SignInViewModel.Companion
import com.codewithngoc.instagallery.ui.features.auth.login.SignInViewModel.SignInEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val repository: AuthRepository,
    val session: InstaGallerySession,
    @ApplicationContext val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow<SignUpEvent>(SignUpEvent.Nothing)
    val uiState = _uiState.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<SignUpNavigationEvent>()
    val navigationEvent = _navigationEvent.asSharedFlow()

    private val _username = MutableStateFlow("")
    val username = _username.asStateFlow()

    private val _email = MutableStateFlow("")
    val email = _email.asStateFlow()

    private val _password = MutableStateFlow("")
    val password = _password.asStateFlow()

    private val _fullName = MutableStateFlow("")
    val fullName = _fullName.asStateFlow()

    private val _confirmPassword = MutableStateFlow("")
    val confirmPassword = _confirmPassword.asStateFlow()

    private val _usernameError = MutableStateFlow<String?>(null)
    val usernameError = _usernameError.asStateFlow()

    private val _emailError = MutableStateFlow<String?>(null)
    val emailError = _emailError.asStateFlow()

    private val _passwordError = MutableStateFlow<String?>(null)
    val passwordError = _passwordError.asStateFlow()

    private val _fullNameError = MutableStateFlow<String?>(null)
    val fullNameError = _fullNameError.asStateFlow()

    private val _confirmPasswordError = MutableStateFlow<String?>(null)
    val confirmPasswordError = _confirmPasswordError.asStateFlow()

    companion object {
        private const val MIN_PASSWORD_LENGTH = 6
        private val GMAIL_REGEX = "^[A-Za-z0-9+_.-]+@gmail\\.com$".toRegex()
    }

    // Validate Functions
    private fun validateUsername(input: String) : String? {
        return if (input.isNotEmpty()) {
            if (input.length < 3) "Tên đăng nhập phải có ít nhất 3 ký tự" else null
        } else null
    }

    private fun validateFullName(input: String) : String? {
        return if (input.isNotEmpty()) {
            if (input.length < 5) "Họ và tên phải có ít nhất 5 ký tự" else null
        } else null
    }

    private fun validateEmail(input: String) : String? {
        return if (input.isNotEmpty()) {
            if (!GMAIL_REGEX.matches(input)) "Email không hợp lệ" else null
        } else null
    }

    private fun validatePassword(input: String)  : String? {
        return if (input.isNotEmpty()) {
            if (input.length < MIN_PASSWORD_LENGTH) "Mật khẩu phải có ít nhất ${MIN_PASSWORD_LENGTH} ký tự" else null
        } else null
    }

    private fun validateConfirmPassword(password: String, confirm: String) : String? {
        return if (confirm.isNotEmpty()) {
            if (password != confirm) "Mật khẩu không khớp" else null
        } else null
    }

    fun onNameChange(username: String) {
        _username.value = username
        _usernameError.value = validateUsername(username)
    }

    fun onEmailChange(email: String) {
        _email.value = email
        _emailError.value = validateEmail(email)
    }

    fun onPasswordChange(password: String) {
        _password.value = password
        _passwordError.value = validatePassword(password)
        _confirmPasswordError.value = validateConfirmPassword(password, _confirmPassword.value)
    }

    fun onFullNameChange(fullName: String) {
        _fullName.value = fullName
        _fullNameError.value = validateFullName(fullName)
    }

    fun onConfirmPasswordChange(value: String) {
        _confirmPassword.value = value
        _confirmPasswordError.value = validateConfirmPassword(_password.value, value)
    }

    fun onSignUpClick() {
        val usernameVal = _username.value.trim()
        val fullNameVal = _fullName.value.trim()
        val emailVal = _email.value.trim()
        val passwordVal = _password.value
        val confirmPasswordVal = _confirmPassword.value

        val usernameErr = if (usernameVal.isBlank()) "Tên đăng nhập không được để trống"
                           else validateUsername(usernameVal)
        val fullNameErr = if (fullNameVal.isBlank()) "Họ và tên không được để trống"
                           else validateFullName(fullNameVal)
        val emailErr = if (emailVal.isBlank()) "Email không được để trống"
                       else validateEmail(emailVal)
        val passwordErr = if (passwordVal.isBlank()) "Mật khẩu không được để trống"
                          else validatePassword(passwordVal)
        val confirmPasswordErr = if (confirmPasswordVal.isBlank()) "Xác nhận mật khẩu không được để trống"
                                  else validateConfirmPassword(passwordVal, confirmPasswordVal)

        // Validate tất cả
        _usernameError.value = usernameErr
        _fullNameError.value = fullNameErr
        _emailError.value = emailErr
        _passwordError.value = passwordErr
        _confirmPasswordError.value = confirmPasswordErr

        if (listOf(
                _usernameError.value,
                _fullNameError.value,
                _emailError.value,
                _passwordError.value,
                _confirmPasswordError.value
            ).any { it != null }
        ) return


        viewModelScope.launch {
            _uiState.value = SignUpEvent.Loading

            val response = repository.signup(
                SignUpRequest(
                    username = _username.value,
                    fullName = _fullName.value,
                    email = _email.value,
                    password = _password.value,
                )
            )

            when (response) {
                is ApiResponse.Success -> {
                    _uiState.value = SignUpEvent.Success
                    _navigationEvent.emit(SignUpNavigationEvent.NavigateToLogin)
                }

                is ApiResponse.Error -> {
                    _uiState.value = SignUpEvent.Error(response.code.getInSignUpErrorMessage())
                }

                else -> {
                    _uiState.value = SignUpEvent.Error(
                        context.getString(R.string.sign_up_failed_network_error)
                    )
                }
            }
        }
    }

    private fun Int.getInSignUpErrorMessage() : String {
        return when (this) {
            400 -> context.getString(R.string.sign_up_failed_invalid_data)
            409 -> context.getString(R.string.sign_up_failed_email_exists)
            428 -> context.getString(R.string.sign_up_failed_terms_not_accepted)
            500 -> context.getString(R.string.sign_up_failed_general_error)
            else -> context.getString(R.string.sign_up_failed_unknown_error)
        }
    }

    sealed class SignUpNavigationEvent {
        object NavigateToLogin : SignUpNavigationEvent()
        object NavigateToHome : SignUpNavigationEvent()
    }

    sealed class SignUpEvent {
        object Nothing : SignUpEvent()
        object Success : SignUpEvent()
        data class Error(val message: String) : SignUpEvent()
        object Loading : SignUpEvent()
    }

}