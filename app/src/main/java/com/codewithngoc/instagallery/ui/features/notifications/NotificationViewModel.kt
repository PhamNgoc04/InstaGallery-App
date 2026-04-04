package com.codewithngoc.instagallery.ui.features.notifications

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codewithngoc.instagallery.data.model.NotificationResponse
import com.codewithngoc.instagallery.data.repository.NotificationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale
import javax.inject.Inject

data class NotificationUiState(
    val isLoading: Boolean = false,
    val notifications: List<NotificationUI> = emptyList(),
    val error: String? = null,
    val isRefreshing: Boolean = false
)

data class NotificationUI(
    val id: Long,
    val userId: Long, // to navigate if type is FOLLOW
    val username: String,
    val avatarUrl: String?,
    val type: String, // mapped type: LIKE, COMMENT, FOLLOW, BOOKING
    val rawType: String,
    val message: String,
    val timeAgo: String,
    val isRead: Boolean,
    val targetId: Long? // to navigate to POST or BOOKING
)

@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val repository: NotificationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(NotificationUiState())
    val uiState: StateFlow<NotificationUiState> = _uiState.asStateFlow()

    init {
        fetchNotifications()
    }

    fun fetchNotifications() {
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)
        viewModelScope.launch {
            val result = repository.getNotifications(page = 1, limit = 50)
            result.onSuccess { response ->
                val mappedNotifications = response.notifications.map { mapToUI(it) }
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    notifications = mappedNotifications
                )
            }.onFailure { exception ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = exception.message ?: "Unknown error occurred"
                )
            }
        }
    }

    fun refreshNotifications() {
        _uiState.value = _uiState.value.copy(isRefreshing = true, error = null)
        viewModelScope.launch {
            val result = repository.getNotifications(page = 1, limit = 50)
            result.onSuccess { response ->
                val mappedNotifications = response.notifications.map { mapToUI(it) }
                _uiState.value = _uiState.value.copy(
                    isRefreshing = false,
                    notifications = mappedNotifications
                )
            }.onFailure { exception ->
                _uiState.value = _uiState.value.copy(
                    isRefreshing = false,
                    error = exception.message ?: "Unknown error occurred"
                )
            }
        }
    }

    fun markAsRead(notificationId: Long, isRead: Boolean) {
        if (isRead) return // Already read
        
        // Optimistic UI update
        val currentNotifications = _uiState.value.notifications.toMutableList()
        val index = currentNotifications.indexOfFirst { it.id == notificationId }
        if (index != -1) {
            currentNotifications[index] = currentNotifications[index].copy(isRead = true)
            _uiState.value = _uiState.value.copy(notifications = currentNotifications)
        }

        // Call API
        viewModelScope.launch {
            val result = repository.markNotificationRead(notificationId)
            if (result.isFailure) {
                // Revert if failed
                val revertedNotifications = _uiState.value.notifications.toMutableList()
                val revertIndex = revertedNotifications.indexOfFirst { it.id == notificationId }
                if (revertIndex != -1) {
                    revertedNotifications[revertIndex] = revertedNotifications[revertIndex].copy(isRead = false)
                    _uiState.value = _uiState.value.copy(notifications = revertedNotifications)
                }
            }
        }
    }

    fun markAllAsRead() {
        // Optimistic update
        val updatedNotifications = _uiState.value.notifications.map { it.copy(isRead = true) }
        _uiState.value = _uiState.value.copy(notifications = updatedNotifications)

        viewModelScope.launch {
            repository.markAllNotificationsRead()
        }
    }

    private fun mapToUI(dto: NotificationResponse): NotificationUI {
        // Handle message and type
        val uiType = when (dto.type) {
            "NEW_LIKE", "LIKE" -> "LIKE"
            "NEW_COMMENT", "COMMENT" -> "COMMENT"
            "NEW_FOLLOWER", "FOLLOW" -> "FOLLOW"
            "BOOKING_REQUEST", "BOOKING_CONFIRMED", "BOOKING_CANCELLED", "BOOKING" -> "BOOKING"
            else -> "SYSTEM"
        }

        val message = dto.body ?: when (uiType) {
            "LIKE" -> "đã thích bài viết của bạn"
            "COMMENT" -> "đã bình luận vào bài viết của bạn"
            "FOLLOW" -> "đã bắt đầu follow bạn"
            "BOOKING" -> "đã đặt lịch chụp với bạn"
            else -> "có một thông báo mới cho bạn"
        }

        return NotificationUI(
            id = dto.id,
            userId = dto.senderId ?: 0,
            username = dto.senderUsername ?: "Người dùng",
            avatarUrl = dto.senderAvatar,
            type = uiType,
            rawType = dto.type,
            message = message,
            timeAgo = formatTimeAgo(dto.createdAt),
            isRead = dto.isRead,
            targetId = dto.targetId
        )
    }

    private fun formatTimeAgo(isoString: String?): String {
        if (isoString.isNullOrEmpty()) return "Vừa xong"
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val zdt = ZonedDateTime.parse(isoString)
                val now = ZonedDateTime.now()
                
                val days = ChronoUnit.DAYS.between(zdt, now)
                
                when {
                    days == 0L -> {
                        // Today, return time like "5:30 PM"
                        val formatter = DateTimeFormatter.ofPattern("h:mm a", Locale.ENGLISH)
                        zdt.format(formatter)
                    }
                    days == 1L -> "Yesterday"
                    else -> {
                        // Older, return date like "14/12/18"
                        val formatter = DateTimeFormatter.ofPattern("dd/MM/yy", Locale.ENGLISH)
                        zdt.format(formatter)
                    }
                }
            } else {
                isoString.take(10) // fallback for older Android
            }
        } catch (e: Exception) {
            "Vừa xong"
        }
    }
}
