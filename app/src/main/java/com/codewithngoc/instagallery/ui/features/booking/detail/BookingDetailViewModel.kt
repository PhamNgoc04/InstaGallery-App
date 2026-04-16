package com.codewithngoc.instagallery.ui.features.booking.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codewithngoc.instagallery.data.model.BookingResponse
import com.codewithngoc.instagallery.data.remote.ApiResponse
import com.codewithngoc.instagallery.data.repository.BookingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookingDetailViewModel @Inject constructor(
    private val bookingRepository: BookingRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val bookingId: Long = savedStateHandle.get<Long>("bookingId") ?: 0L

    private val _booking = MutableStateFlow<BookingResponse?>(null)
    val booking: StateFlow<BookingResponse?> = _booking.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        loadBookingDetail()
    }

    private fun loadBookingDetail() {
        if (bookingId == 0L) return
        viewModelScope.launch {
            _isLoading.value = true
            when (val res = bookingRepository.getBookingDetail(bookingId)) {
                is ApiResponse.Success -> _booking.value = res.data
                is ApiResponse.Error -> _error.value = res.formatMsg()
                is ApiResponse.Exception -> _error.value = res.exception.message
            }
            _isLoading.value = false
        }
    }

    fun updateStatus(status: String) {
        viewModelScope.launch {
            _isLoading.value = true
            when (val res = bookingRepository.updateBookingStatus(bookingId, status)) {
                is ApiResponse.Success -> {
                    _booking.value = res.data
                }
                else -> { /* Handle Error */ }
            }
            _isLoading.value = false
        }
    }
}
