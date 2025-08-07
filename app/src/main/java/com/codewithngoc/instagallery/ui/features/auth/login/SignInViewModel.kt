package com.codewithngoc.instagallery.ui.features.auth.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codewithngoc.instagallery.data.InstaGalleryApi
import com.codewithngoc.instagallery.data.InstaGallerySession
import com.codewithngoc.instagallery.data.model.SignInRequest
import com.codewithngoc.instagallery.data.remote.ApiResponse
import com.codewithngoc.instagallery.data.remote.safeApiCall
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
    val instaGalleryApi: InstaGalleryApi,
    val session: InstaGallerySession
) : ViewModel() {
    private val _uiState = MutableStateFlow<SignInEvent>(SignInEvent.Nothing)
    val uiState = _uiState.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<SignInNavigationEvent>()
    val navigationEvent = _navigationEvent.asSharedFlow()

    private val _email = MutableStateFlow("")
    val email = _email.asStateFlow()

    private val _password = MutableStateFlow("")
    val password = _password.asStateFlow()

    fun onEmailChange(email : String) {
        _email.value = email
    }

    fun onPasswordChange(password : String) {
        _password.value = password
    }

    fun onSignInClick() {
        viewModelScope.launch {
            _uiState.value = SignInEvent.Loading
            val response = safeApiCall {
                instaGalleryApi.signIn(
                    SignInRequest(
                        email = _email.value,
                        password = _password.value
                    )
                )
            }

            when (response) {
                is ApiResponse.Success -> {
                    _uiState.value = SignInEvent.Success
                    session.storeToken(response.data.token)
                    _navigationEvent.emit(SignInNavigationEvent.NavigateToHome)
                }

                else -> {
                    val errorCode = (response as? ApiResponse.Error)?.code ?: 0
                    var errorMessage = "Sign In Failed"
                    var errorDescription = "Failed to sign up"

                    when (errorCode) {
                        400 -> {
                            errorMessage = "Invalid credentials"
                            errorDescription = "Please enter corect details."
                        }
                    }
                    _uiState.value = SignInEvent.Error
                }
            }
        }
    }

    sealed class SignInNavigationEvent {
        object NavigateToSignUp : SignInNavigationEvent()
        object NavigateToHome : SignInNavigationEvent()
    }

    sealed class SignInEvent {
        object Nothing : SignInEvent()
        object Success : SignInEvent()
        object Error : SignInEvent()
        object Loading : SignInEvent()
    }
}

