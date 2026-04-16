package com.codewithngoc.instagallery.ui.features.profile.portfolio

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codewithngoc.instagallery.data.model.PortfolioResponse
import com.codewithngoc.instagallery.data.remote.ApiResponse
import com.codewithngoc.instagallery.data.repository.PortfolioRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PortfolioViewModel @Inject constructor(
    private val portfolioRepository: PortfolioRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    // Lấy photographerId từ điều hướng (nếu có null thì tải profile của chính mình)
    private val photographerId: Long? = savedStateHandle.get<Long>("photographerId")

    private val _portfolio = MutableStateFlow<PortfolioResponse?>(null)
    val portfolio: StateFlow<PortfolioResponse?> = _portfolio.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        loadPortfolio()
    }

    private fun loadPortfolio() {
        viewModelScope.launch {
            _isLoading.value = true
            val response = if (photographerId != null) {
                portfolioRepository.getUserPortfolio(photographerId)
            } else {
                portfolioRepository.getMyPortfolio()
            }

            when (response) {
                is ApiResponse.Success -> _portfolio.value = response.data
                is ApiResponse.Error -> _error.value = response.formatMsg()
                is ApiResponse.Exception -> _error.value = response.exception.message
            }
            _isLoading.value = false
        }
    }
}
