package com.codewithngoc.instagallery.ui.features.notifications

import app.cash.turbine.test
import com.codewithngoc.instagallery.data.model.NotificationResponse
import com.codewithngoc.instagallery.data.model.PaginatedNotificationsResponse
import com.codewithngoc.instagallery.data.repository.NotificationRepository
import com.codewithngoc.instagallery.rules.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class NotificationViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var repository: NotificationRepository
    private lateinit var viewModel: NotificationViewModel

    @Before
    fun setUp() {
        repository = mockk()
    }

    @Test
    fun `init fetches notifications and updates uiState`() = runTest {
        // Arrange
        val mockNotification = NotificationResponse(
            id = 1L,
            userId = 2L,
            actorId = 3L,
            actorUsername = "test_user",
            actorAvatarUrl = null,
            type = "LIKE",
            targetId = 100L,
            isRead = false,
            createdAt = "2024-04-04T10:00:00Z"
        )
        val paginatedData = PaginatedNotificationsResponse(
            items = listOf(mockNotification),
            totalItems = 1,
            totalPages = 1,
            currentPage = 1
        )
        coEvery { repository.getNotifications(any(), any()) } returns Result.success(paginatedData)

        // Act
        viewModel = NotificationViewModel(repository)

        // Assert
        viewModel.uiState.test {
            // First emission might be initial state or already the newly fetched state due to runTest properties.
            // Let's await the item that has the notification.
            val state = awaitItem()
            assertFalse(state.isLoading)
            assertEquals(1, state.notifications.size)
            assertEquals("test_user", state.notifications.first().username)
            assertEquals("đã thích bài viết của bạn. ", state.notifications.first().message)
        }
    }
}
