package com.codewithngoc.instagallery.ui.features.booking

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
class PhotographerListViewModel @Inject constructor(
    private val portfolioRepository: PortfolioRepository
) : ViewModel() {

    private val _photographers = MutableStateFlow<List<PortfolioResponse>>(emptyList())
    val photographers: StateFlow<List<PortfolioResponse>> = _photographers.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadPhotographers()
    }

    fun loadPhotographers(location: String? = null, specialty: String? = null) {
        viewModelScope.launch {
            _isLoading.value = true
            when (val res = portfolioRepository.getPortfolios(location, specialty)) {
                is ApiResponse.Success -> _photographers.value = res.data.portfolios
                else -> { /* Handle Error */ }
            }
            _isLoading.value = false
        }
    }
}
