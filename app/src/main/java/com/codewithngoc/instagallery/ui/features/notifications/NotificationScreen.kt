package com.codewithngoc.instagallery.ui.features.notifications

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage

@Composable
fun NotificationScreen(
    navController: NavController,
    viewModel: NotificationViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        bottomBar = { com.codewithngoc.instagallery.ui.features.homefeed.HomeInsBottomBar(navController = navController) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFFFF8F0))
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "Thông báo",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                
                if (uiState.notifications.any { !it.isRead }) {
                    Text(
                        text = "Đánh dấu đã đọc",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.clickable { viewModel.markAllAsRead() }
                    )
                }
            }

            Box(modifier = Modifier.fillMaxSize()) {
                if (uiState.isLoading && uiState.notifications.isEmpty()) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                } else if (uiState.error != null && uiState.notifications.isEmpty()) {
                    Text(
                        text = uiState.error ?: "Đã xảy ra lỗi",
                        modifier = Modifier.align(Alignment.Center).padding(16.dp),
                        color = Color.Red,
                        textAlign = TextAlign.Center
                    )
                } else if (uiState.notifications.isEmpty()) {
                    Text(
                        text = "Bạn không có thông báo nào",
                        modifier = Modifier.align(Alignment.Center).padding(16.dp),
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )
                } else {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(uiState.notifications, key = { it.id }) { notification ->
                            NotificationRow(
                                notification = notification,
                                onClick = {
                                    viewModel.markAsRead(notification.id, notification.isRead)
                                    when (notification.type) {
                                        "LIKE", "COMMENT" -> { 
                                            notification.targetId?.let { 
                                                navController.navigate(com.codewithngoc.instagallery.ui.navigation.Screen.PostDetail.createRoute(it.toString())) 
                                            }
                                        }
                                        "FOLLOW" -> { navController.navigate(com.codewithngoc.instagallery.ui.navigation.Screen.UserProfile.createRoute(notification.userId)) }
                                        "BOOKING" -> { navController.navigate(com.codewithngoc.instagallery.ui.navigation.Screen.BookingList.route) }
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun NotificationRow(notification: NotificationUI, onClick: () -> Unit) {
    val bgColor = if (!notification.isRead) Color(0xFFFFF0E0) else Color.Transparent

    val icon = when (notification.type) {
        "LIKE" -> "❤️"
        "COMMENT" -> "💬"
        "FOLLOW" -> "👤"
        "BOOKING" -> "📅"
        else -> "🔔"
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(bgColor)
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Avatar
        AsyncImage(
            model = notification.avatarUrl,
            contentDescription = "Avatar",
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop,
            placeholder = ColorPainter(Color.LightGray),
            error = ColorPainter(Color.LightGray)
        )

        Spacer(modifier = Modifier.width(12.dp))

        // Content
        Column(modifier = Modifier.weight(1f)) {
            Text(
                notification.username,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = Color.Black
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = icon, fontSize = 14.sp)
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = notification.message,
                    fontSize = 13.sp,
                    color = Color.DarkGray,
                    maxLines = 2
                )
            }
        }

        // Time
        Text(
            text = notification.timeAgo,
            fontSize = 12.sp,
            color = Color.Gray
        )
    }
    HorizontalDivider(color = Color.LightGray.copy(alpha = 0.2f))
}
