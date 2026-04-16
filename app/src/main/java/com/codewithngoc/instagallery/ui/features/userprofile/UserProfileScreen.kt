package com.codewithngoc.instagallery.ui.features.userprofile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserProfileScreen(
    userId: Long,
    navController: NavController,
    viewModel: UserProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    val titleText = when (val s = uiState) {
                        is UserProfileUiState.Success -> "@${s.user.username}"
                        else -> "@user_$userId"
                    }
                    Text(titleText, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Quay lại")
                    }
                },
                actions = {
                    IconButton(onClick = { }) {
                        Icon(Icons.Default.MoreVert, "Thêm", tint = Color.DarkGray)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color.White
    ) { padding ->
        when (val state = uiState) {
            is UserProfileUiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color(0xFFFF6B35))
                }
            }

            is UserProfileUiState.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("❌ ${state.message}", style = MaterialTheme.typography.bodyLarge)
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = { viewModel.loadProfile() },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF6B35))
                        ) {
                            Text("Thử lại")
                        }
                    }
                }
            }

            is UserProfileUiState.Success -> {
                var isFollowing by remember { mutableStateOf(state.isFollowing) }
                val user = state.user
                val posts = state.posts

                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    modifier = Modifier
                        .padding(padding)
                        .fillMaxSize(),
                    horizontalArrangement = Arrangement.spacedBy(2.dp),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    // Profile header - spanning full width
                    item(span = { GridItemSpan(3) }) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Spacer(modifier = Modifier.height(16.dp))

                            // Avatar
                            AsyncImage(
                                model = user.profilePictureUrl,
                                contentDescription = "Avatar",
                                modifier = Modifier
                                    .size(100.dp)
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop,
                                placeholder = ColorPainter(Color.LightGray),
                                error = ColorPainter(Color.LightGray)
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            // Name + badge
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    user.fullName,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold
                                )
                                if (user.userType == "PHOTOGRAPHER") {
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = Color(0xFFFF6B35).copy(alpha = 0.15f)
                                    ) {
                                        Text(
                                            "📷 Photographer",
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                            style = MaterialTheme.typography.labelSmall,
                                            color = Color(0xFFFF6B35)
                                        )
                                    }
                                }
                            }

                            Text("@${user.username}", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)

                            Spacer(modifier = Modifier.height(8.dp))

                            // Stats
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                StatItem("${posts.size}", "Posts")
                                Text(" | ", color = Color.LightGray)
                                StatItem("0", "Followers", onClick = {
                                    navController.navigate("followers/$userId")
                                })
                                Text(" | ", color = Color.LightGray)
                                StatItem("0", "Following", onClick = {
                                    navController.navigate("followers/$userId")
                                })
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Follow + Message buttons
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Button(
                                    onClick = {
                                        isFollowing = !isFollowing
                                        viewModel.toggleFollow()
                                    },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(40.dp),
                                    shape = RoundedCornerShape(20.dp),
                                    colors = if (isFollowing) ButtonDefaults.buttonColors(
                                        containerColor = Color.White
                                    ) else ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFFFF6B35)
                                    )
                                ) {
                                    Text(
                                        if (isFollowing) "Đang follow" else "Follow",
                                        color = if (isFollowing) Color.Black else Color.White,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }

                                OutlinedButton(
                                    onClick = {
                                        navController.navigate("chat_detail/$userId")
                                    },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(40.dp),
                                    shape = RoundedCornerShape(20.dp)
                                ) {
                                    Text("✉ Nhắn tin", color = Color.Black)
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Book photographer button (chỉ hiện nếu là PHOTOGRAPHER)
                            if (user.userType == "PHOTOGRAPHER") {
                                OutlinedButton(
                                    onClick = {
                                        navController.navigate("create_booking/$userId")
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(40.dp),
                                    shape = RoundedCornerShape(20.dp)
                                ) {
                                    Text(
                                        "📷 Đặt lịch chụp",
                                        color = Color(0xFFFF6B35),
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("›", color = Color(0xFFFF6B35), style = MaterialTheme.typography.titleMedium)
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            HorizontalDivider(color = Color.LightGray.copy(alpha = 0.3f))
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }

                    // Photo grid từ API
                    if (posts.isEmpty()) {
                        item(span = { GridItemSpan(3) }) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 48.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("Chưa có bài đăng nào", color = Color.Gray)
                            }
                        }
                    } else {
                        items(posts) { post ->
                            val imageUrl = post.media.firstOrNull()?.url
                            AsyncImage(
                                model = imageUrl,
                                contentDescription = "Ảnh bài đăng",
                                modifier = Modifier
                                    .aspectRatio(1f)
                                    .clickable { },
                                contentScale = ContentScale.Crop,
                                placeholder = ColorPainter(Color.LightGray),
                                error = ColorPainter(Color.LightGray)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatItem(count: String, label: String, onClick: (() -> Unit)? = null) {
    Row(
        modifier = Modifier
            .let { if (onClick != null) it.clickable { onClick() } else it }
            .padding(horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(count, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.width(4.dp))
        Text(label, color = Color.Gray, style = MaterialTheme.typography.bodyMedium)
    }
}
