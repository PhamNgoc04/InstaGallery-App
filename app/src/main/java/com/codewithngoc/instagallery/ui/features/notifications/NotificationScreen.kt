package com.codewithngoc.instagallery.ui.features.notifications

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.NavController
import coil.compose.AsyncImage
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationScreen(
    navController: NavController,
    viewModel: NotificationViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val lifecycleOwner = LocalLifecycleOwner.current

    val pullRefreshState = rememberPullToRefreshState()
    if (pullRefreshState.isRefreshing) {
        LaunchedEffect(true) {
            viewModel.refreshNotifications()
        }
    }
    LaunchedEffect(uiState.isRefreshing) {
        if (uiState.isRefreshing) {
            pullRefreshState.startRefresh()
        } else {
            pullRefreshState.endRefresh()
        }
    }

    // Auto-refresh khi quay lại màn hình (ON_RESUME)
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.fetchNotifications()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    // Auto-poll mỗi 30 giây khi user đang ở màn hình này
    LaunchedEffect(Unit) {
        while (true) {
            delay(30_000L)
            viewModel.fetchNotifications()
        }
    }

    Scaffold(
        bottomBar = {
            com.codewithngoc.instagallery.ui.features.homefeed.HomeInsBottomBar(navController = navController)
        }
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
                    text = "Thông báo",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                if (uiState.notifications.any { !it.isRead }) {
                    Text(
                        text = "Đánh dấu tất cả đã đọc",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.clickable { viewModel.markAllAsRead() }
                    )
                }
            }

            // Pull-to-Refresh bọc content
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .nestedScroll(pullRefreshState.nestedScrollConnection)
            ) {
                when {
                    uiState.isLoading && uiState.notifications.isEmpty() -> {
                        Box(Modifier.fillMaxSize()) {
                            CircularProgressIndicator(Modifier.align(Alignment.Center))
                        }
                    }
                    uiState.error != null && uiState.notifications.isEmpty() -> {
                        Box(Modifier.fillMaxSize()) {
                            Column(
                                modifier = Modifier.align(Alignment.Center),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "⚠️ ${uiState.error}",
                                    color = Color.Red,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(16.dp)
                                )
                                TextButton(onClick = { viewModel.fetchNotifications() }) {
                                    Text("Thử lại")
                                }
                            }
                        }
                    }
                    uiState.notifications.isEmpty() -> {
                        Box(Modifier.fillMaxSize()) {
                            Text(
                                text = "🔔 Chưa có thông báo nào\nKéo xuống để làm mới",
                                modifier = Modifier.align(Alignment.Center).padding(16.dp),
                                color = Color.Gray,
                                textAlign = TextAlign.Center,
                                fontSize = 15.sp
                            )
                        }
                    }
                    else -> {
                        LazyColumn(modifier = Modifier.fillMaxSize()) {
                            items(uiState.notifications, key = { it.id }) { notification ->
                                NotificationRow(
                                    notification = notification,
                                    onClick = {
                                        viewModel.markAsRead(notification.id, notification.isRead)
                                        when (notification.type) {
                                            "LIKE", "COMMENT" -> notification.targetId?.let {
                                                navController.navigate(
                                                    com.codewithngoc.instagallery.ui.navigation.Screen.PostDetail.createRoute(it.toString())
                                                )
                                            }
                                            "FOLLOW" -> navController.navigate(
                                                com.codewithngoc.instagallery.ui.navigation.Screen.UserProfile.createRoute(notification.userId)
                                            )
                                            "BOOKING" -> navController.navigate(
                                                com.codewithngoc.instagallery.ui.navigation.Screen.BookingList.route
                                            )
                                        }
                                    }
                                )
                            }
                        }
                    }
                }

                PullToRefreshContainer(
                    state = pullRefreshState,
                    modifier = Modifier.align(Alignment.TopCenter)
                )
            }
        }
    }
}

@Composable
private fun NotificationRow(notification: NotificationUI, onClick: () -> Unit) {
    val bgColor = if (!notification.isRead) Color(0xFFFFF0E0) else Color.White

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
        Box {
            AsyncImage(
                model = notification.avatarUrl,
                contentDescription = "Avatar",
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop,
                placeholder = ColorPainter(Color.LightGray),
                error = ColorPainter(Color.LightGray)
            )
            // Emoji icon nhỏ ở góc dưới phải avatar
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .align(Alignment.BottomEnd)
                    .background(Color.White, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(text = icon, fontSize = 11.sp)
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Content
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = notification.username,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = Color.Black
            )
            Text(
                text = notification.message,
                fontSize = 13.sp,
                color = Color.DarkGray,
                maxLines = 2
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Time + unread dot
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = notification.timeAgo,
                fontSize = 11.sp,
                color = Color.Gray
            )
            if (!notification.isRead) {
                Spacer(modifier = Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(Color(0xFFFF5722), CircleShape)
                )
            }
        }
    }
    HorizontalDivider(color = Color.LightGray.copy(alpha = 0.15f))
}
