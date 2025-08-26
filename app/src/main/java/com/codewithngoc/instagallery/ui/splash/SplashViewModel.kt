package com.codewithngoc.instagallery.ui.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codewithngoc.instagallery.data.InstaGallerySession
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val session: InstaGallerySession
) : ViewModel() {

    private val _navigationEvent = MutableSharedFlow<SplashNavigationEvent>()
    val navigationEvent = _navigationEvent.asSharedFlow()

    init {
        checkLoginStatus()
    }

    private fun checkLoginStatus() {
        val token = session.getToken()
        viewModelScope.launch {
            if (token != null) {
                _navigationEvent.emit(SplashNavigationEvent.NavigateToHomeFeed)
            } else {
                _navigationEvent.emit(SplashNavigationEvent.NavigateToSignIn)
            }
        }
    }

    sealed class SplashNavigationEvent {
        object NavigateToHomeFeed : SplashNavigationEvent()
        object NavigateToSignIn : SplashNavigationEvent()
    }
}