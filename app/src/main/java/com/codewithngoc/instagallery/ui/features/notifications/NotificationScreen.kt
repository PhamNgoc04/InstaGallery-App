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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage

data class NotificationItem(
    val id: Int,
    val username: String,
    val type: String, // LIKE, COMMENT, FOLLOW, BOOKING
    val message: String,
    val time: String,
    val isRead: Boolean = false
)

@Composable
fun NotificationScreen(navController: NavController) {
    val notifications = remember {
        listOf(
            NotificationItem(1, "Apollo Phelps", "LIKE", "đã thích bài viết của bạn", "5:30 PM"),
            NotificationItem(2, "Bailey Stein", "COMMENT", "đã bình luận: Đẹp quá!", "4:42 PM"),
            NotificationItem(3, "Leo Reilly", "FOLLOW", "đã bắt đầu follow bạn", "Hôm qua"),
            NotificationItem(4, "Coco Gordon", "BOOKING", "đã đặt lịch chụp với bạn", "Hôm qua"),
            NotificationItem(5, "Hank Lozano", "BOOKING", "đã đặt lịch chụp với bạn", "14/12"),
            NotificationItem(6, "Rocky Ellison", "LIKE", "đã thích bài viết của bạn", "13/12")
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFF8F0))
    ) {
        // Header
        Text(
            "Thông báo",
            modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 8.dp),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(notifications) { notification ->
                NotificationRow(
                    notification = notification,
                    onClick = {
                        when (notification.type) {
                            "LIKE", "COMMENT" -> { /* Navigate to post */ }
                            "FOLLOW" -> { navController.navigate("user_profile/${notification.id}") }
                            "BOOKING" -> { navController.navigate("booking_list") }
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun NotificationRow(notification: NotificationItem, onClick: () -> Unit) {
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
            model = null,
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
                fontSize = 15.sp
            )
            Text(
                "$icon ${notification.message}",
                fontSize = 13.sp,
                color = Color.DarkGray
            )
        }

        // Time
        Text(
            notification.time,
            fontSize = 12.sp,
            color = Color.Gray
        )
    }
    HorizontalDivider(color = Color.LightGray.copy(alpha = 0.2f))
}
