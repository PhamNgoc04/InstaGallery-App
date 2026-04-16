package com.codewithngoc.instagallery.ui.features.profile.album

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codewithngoc.instagallery.data.model.AlbumResponse
import com.codewithngoc.instagallery.data.model.CreateAlbumRequest
import com.codewithngoc.instagallery.data.remote.ApiResponse
import com.codewithngoc.instagallery.data.repository.AlbumRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AlbumViewModel @Inject constructor(
    private val albumRepository: AlbumRepository
) : ViewModel() {

    private val _albums = MutableStateFlow<List<AlbumResponse>>(emptyList())
    val albums: StateFlow<List<AlbumResponse>> = _albums.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        loadAlbums()
    }

    fun loadAlbums() {
        viewModelScope.launch {
            _isLoading.value = true
            when (val res = albumRepository.getAlbums()) {
                is ApiResponse.Success -> _albums.value = res.data
                is ApiResponse.Error -> _error.value = res.formatMsg()
                is ApiResponse.Exception -> _error.value = res.exception.message
            }
            _isLoading.value = false
        }
    }

    fun createAlbum(name: String, description: String?) {
        viewModelScope.launch {
            _isLoading.value = true
            when (val res = albumRepository.createAlbum(CreateAlbumRequest(name, description))) {
                is ApiResponse.Success -> loadAlbums() // Reload sau khi tạo
                else -> { /* Handle Error */ }
            }
            _isLoading.value = false
        }
    }
}
