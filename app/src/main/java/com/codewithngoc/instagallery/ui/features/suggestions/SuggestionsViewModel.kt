package com.codewithngoc.instagallery.ui.features.suggestions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codewithngoc.instagallery.data.model.SearchUserResult
import com.codewithngoc.instagallery.data.remote.ApiResponse
import com.codewithngoc.instagallery.data.repository.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SuggestionsViewModel @Inject constructor(
    private val profileRepository: ProfileRepository
) : ViewModel() {

    private val _suggestions = MutableStateFlow<List<SearchUserResult>>(emptyList())
    val suggestions: StateFlow<List<SearchUserResult>> = _suggestions.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        loadSuggestions()
    }

    fun loadSuggestions() {
        viewModelScope.launch {
            _isLoading.value = true
            when (val res = profileRepository.getSuggestions(20)) {
                is ApiResponse.Success -> _suggestions.value = res.data
                is ApiResponse.Error -> _error.value = res.formatMsg()
                is ApiResponse.Exception -> _error.value = res.exception.message
            }
            _isLoading.value = false
        }
    }
}
