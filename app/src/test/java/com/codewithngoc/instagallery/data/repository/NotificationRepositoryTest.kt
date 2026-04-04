package com.codewithngoc.instagallery.data.repository

import com.codewithngoc.instagallery.data.InstaGalleryApi
import com.codewithngoc.instagallery.data.model.ApiResponseWrapper
import com.codewithngoc.instagallery.data.model.NotificationResponse
import com.codewithngoc.instagallery.data.model.PaginatedNotificationsResponse
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Response

@ExperimentalCoroutinesApi
class NotificationRepositoryTest {

    private lateinit var api: InstaGalleryApi
    private lateinit var repository: NotificationRepository

    @Before
    fun setUp() {
        api = mockk()
        repository = NotificationRepository(api)
    }

    @Test
    fun `getNotifications returns success when API is successful`() = runTest {
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
        val mockResponse = ApiResponseWrapper(
            status = "SUCCESS",
            message = "Success",
            data = paginatedData
        )
        coEvery { api.getNotifications(any(), any()) } returns Response.success(mockResponse)

        // Act
        val result = repository.getNotifications(1, 20)

        // Assert
        assertTrue(result.isSuccess)
        assertEquals(1, result.getOrNull()?.items?.size)
        assertEquals("LIKE", result.getOrNull()?.items?.first()?.type)
    }

    @Test
    fun `getNotifications returns failure when API fails`() = runTest {
        // Arrange
        coEvery { api.getNotifications(any(), any()) } returns Response.error(
            500,
            okhttp3.ResponseBody.create(null, "")
        )

        // Act
        val result = repository.getNotifications(1, 20)

        // Assert
        assertTrue(result.isFailure)
    }
}
