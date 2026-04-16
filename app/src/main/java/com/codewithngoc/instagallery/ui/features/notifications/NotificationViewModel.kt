package com.codewithngoc.instagallery.ui.features.notifications

import android.os.Build
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codewithngoc.instagallery.data.model.NotificationResponse
import com.codewithngoc.instagallery.data.repository.NotificationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
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
    val userId: Long,
    val username: String,
    val avatarUrl: String?,
    val type: String,
    val rawType: String,
    val message: String,
    val timeAgo: String,
    val isRead: Boolean,
    val targetId: Long?
)

@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val repository: NotificationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(NotificationUiState())
    val uiState: StateFlow<NotificationUiState> = _uiState.asStateFlow()

    // Badge count cho bottom bar
    private val _unreadCount = MutableStateFlow(0)
    val unreadCount: StateFlow<Int> = _unreadCount.asStateFlow()

    init {
        fetchNotifications()
        startUnreadCountPolling()
    }

    /** Poll unread count mỗi 60 giây trong background */
    private fun startUnreadCountPolling() {
        viewModelScope.launch {
            while (isActive) {
                fetchUnreadCount()
                delay(60_000L)
            }
        }
    }

    private suspend fun fetchUnreadCount() {
        repository.getUnreadNotificationCount().onSuccess { count ->
            _unreadCount.value = count
        }
    }

    fun fetchNotifications() {
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)
        viewModelScope.launch {
            repository.getNotifications(page = 1, limit = 50)
                .onSuccess { response ->
                    val mapped = response.notifications.map { mapToUI(it) }
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        notifications = mapped
                    )
                    _unreadCount.value = mapped.count { !it.isRead }
                }
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = e.message ?: "Đã xảy ra lỗi"
                    )
                }
        }
    }

    fun refreshNotifications() {
        _uiState.value = _uiState.value.copy(isRefreshing = true, error = null)
        viewModelScope.launch {
            repository.getNotifications(page = 1, limit = 50)
                .onSuccess { response ->
                    val mapped = response.notifications.map { mapToUI(it) }
                    _uiState.value = _uiState.value.copy(
                        isRefreshing = false,
                        notifications = mapped
                    )
                    _unreadCount.value = mapped.count { !it.isRead }
                }
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(
                        isRefreshing = false,
                        error = e.message ?: "Đã xảy ra lỗi"
                    )
                }
        }
    }

    fun markAsRead(notificationId: Long, isRead: Boolean) {
        if (isRead) return
        val list = _uiState.value.notifications.toMutableList()
        val idx = list.indexOfFirst { it.id == notificationId }
        if (idx != -1) {
            list[idx] = list[idx].copy(isRead = true)
            _uiState.value = _uiState.value.copy(notifications = list)
            _unreadCount.value = (_unreadCount.value - 1).coerceAtLeast(0)
        }
        viewModelScope.launch {
            val result = repository.markNotificationRead(notificationId)
            if (result.isFailure) {
                // Revert
                val r = _uiState.value.notifications.toMutableList()
                val i = r.indexOfFirst { it.id == notificationId }
                if (i != -1) {
                    r[i] = r[i].copy(isRead = false)
                    _uiState.value = _uiState.value.copy(notifications = r)
                    _unreadCount.value = _unreadCount.value + 1
                }
            }
        }
    }

    fun markAllAsRead() {
        val updated = _uiState.value.notifications.map { it.copy(isRead = true) }
        _uiState.value = _uiState.value.copy(notifications = updated)
        _unreadCount.value = 0
        viewModelScope.launch { repository.markAllNotificationsRead() }
    }

    private fun mapToUI(dto: NotificationResponse): NotificationUI {
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
                val minutes = ChronoUnit.MINUTES.between(zdt, now)
                val hours = ChronoUnit.HOURS.between(zdt, now)
                val days = ChronoUnit.DAYS.between(zdt, now)
                when {
                    minutes < 1 -> "Vừa xong"
                    minutes < 60 -> "${minutes}p"
                    hours < 24 -> "${hours}h"
                    days == 1L -> "Hôm qua"
                    days < 7 -> "${days} ngày trước"
                    else -> zdt.format(DateTimeFormatter.ofPattern("dd/MM/yy", Locale.ENGLISH))
                }
            } else {
                isoString.take(10)
            }
        } catch (e: Exception) {
            "Vừa xong"
        }
    }
}
