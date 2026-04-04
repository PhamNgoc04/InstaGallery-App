package com.codewithngoc.instagallery.ui.features.profile.settings.editprofile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codewithngoc.instagallery.data.model.UpdateUserProfileRequest
import com.codewithngoc.instagallery.data.model.UserProfileResponse
import com.codewithngoc.instagallery.data.remote.ApiResponse
import com.codewithngoc.instagallery.data.repository.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class EditProfileState {
    object Idle : EditProfileState()
    object Loading : EditProfileState()
    data class Success(val profile: UserProfileResponse) : EditProfileState()
    data class UpdateSuccess(val message: String) : EditProfileState()
    data class Error(val message: String) : EditProfileState()
}

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val repository: ProfileRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<EditProfileState>(EditProfileState.Idle)
    val uiState: StateFlow<EditProfileState> = _uiState.asStateFlow()

    // Cần có các biến lưu giữ State cho các trường
    val username = MutableStateFlow("")
    val fullName = MutableStateFlow("")
    val bio = MutableStateFlow("")
    val email = MutableStateFlow("")
    val phone = MutableStateFlow("")
    val birthDate = MutableStateFlow("")
    val location = MutableStateFlow("")
    val avatarUrl = MutableStateFlow<String?>("")

    init {
        loadCurrentUser()
    }

    private fun loadCurrentUser() {
        viewModelScope.launch {
            _uiState.value = EditProfileState.Loading
            when (val response = repository.getCurrentUser()) {
                is ApiResponse.Success -> {
                    val user = response.data
                    username.value = user.username
                    fullName.value = user.fullName
                    email.value = user.email
                    avatarUrl.value = user.profilePictureUrl
                    // Các field khác hiện tại backend chưa ánh xạ đầy đủ ra UserProfileResponse, 
                    // nhưng nếu gửi lên updateProfile thì server vẫn nhận UpdateUserProfileRequest.
                    _uiState.value = EditProfileState.Success(user)
                }
                is ApiResponse.Error -> {
                    _uiState.value = EditProfileState.Error(response.message)
                }
                else -> {
                    _uiState.value = EditProfileState.Error("Lỗi mạng")
                }
            }
        }
    }

    fun updateProfile() {
        viewModelScope.launch {
            _uiState.value = EditProfileState.Loading
            
            // Xây dựng request
            val request = UpdateUserProfileRequest(
                username = username.value.takeIf { it.isNotBlank() },
                fullName = fullName.value.takeIf { it.isNotBlank() },
                bio = bio.value.takeIf { it.isNotBlank() },
                phoneNumber = phone.value.takeIf { it.isNotBlank() },
                dateOfBirth = birthDate.value.takeIf { it.isNotBlank() },
                location = location.value.takeIf { it.isNotBlank() }
            )

            when (val response = repository.updateProfile(request)) {
                is ApiResponse.Success -> {
                    _uiState.value = EditProfileState.UpdateSuccess("Cập nhật hồ sơ thành công")
                }
                is ApiResponse.Error -> {
                    _uiState.value = EditProfileState.Error(response.message)
                }
                else -> {
                    _uiState.value = EditProfileState.Error("Lỗi mạng")
                }
            }
        }
    }
}
