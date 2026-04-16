package com.codewithngoc.instagallery.ui.features.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    navController: NavController,
    viewModel: SearchViewModel = hiltViewModel()
) {
    var query by remember { mutableStateOf("") }
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Users, 1: Posts, 2: Tags

    val searchResults by viewModel.searchResults.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    // Tự động gọi API khi query thay đổi
    LaunchedEffect(query, selectedTab) {
        val type = when(selectedTab) {
            0 -> "users"
            1 -> "posts"
            2 -> "tags"
            else -> null
        }
        viewModel.search(query, type)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("InstaGallery", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color.White
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            // Search input
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                placeholder = { Text("Tìm kiếm...", color = Color.Gray) },
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                leadingIcon = { Text("🔍") },
                trailingIcon = {
                    if (query.isNotEmpty()) {
                        IconButton(onClick = { query = "" }) {
                            Icon(Icons.Default.Close, "Xóa", tint = Color.Gray)
                        }
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFFF6B35)
                )
            )

            // Tabs
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.White,
                contentColor = Color.Black,
                indicator = { tabPositions ->
                    if (selectedTab < tabPositions.size) {
                        TabRowDefaults.SecondaryIndicator(
                            modifier = Modifier.fillMaxWidth(),
                            color = Color(0xFFFF6B35)
                        )
                    }
                }
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Người dùng", style = MaterialTheme.typography.titleMedium, fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal, color = if (selectedTab == 0) Color(0xFFFF6B35) else Color.Gray) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Bài đăng", style = MaterialTheme.typography.titleMedium, fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal, color = if (selectedTab == 1) Color(0xFFFF6B35) else Color.Gray) }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = { Text("Tags", style = MaterialTheme.typography.titleMedium, fontWeight = if (selectedTab == 2) FontWeight.Bold else FontWeight.Normal, color = if (selectedTab == 2) Color(0xFFFF6B35) else Color.Gray) }
                )
            }

            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFFFF6B35))
                }
            } else if (error != null) {
                Box(modifier = Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                    Text(error ?: "", color = Color.Red)
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    when (selectedTab) {
                        0 -> {
                            val users = searchResults?.users ?: emptyList()
                            if (users.isEmpty() && query.isNotEmpty()) {
                                item { Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) { Text("Không tìm thấy người dùng nào", color = Color.Gray) } }
                            }
                            items(users) { user ->
                                SearchUserItem(
                                    username = user.username,
                                    subtitle = user.fullName,
                                    avatarUrl = user.profilePictureUrl,
                                    onFollowClick = {  },
                                    onUserClick = { navController.navigate("user_profile/${user.id}") }
                                )
                            }
                        }
                        1 -> {
                            val posts = searchResults?.posts ?: emptyList()
                            if (posts.isEmpty() && query.isNotEmpty()) {
                                item { Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) { Text("Không tìm thấy bài viết nào", color = Color.Gray) } }
                            }
                            items(posts) { post ->
                                // Post UI Mock, we can expand later
                                Text("Khớp với bài đăng ID: ${post.postId}", modifier = Modifier.padding(16.dp))
                                HorizontalDivider()
                            }
                        }
                        2 -> {
                            val tags = searchResults?.tags ?: emptyList()
                            if (tags.isEmpty() && query.isNotEmpty()) {
                                item { Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) { Text("Không tìm thấy tag nào", color = Color.Gray) } }
                            }
                            items(tags) { tag ->
                                Text("#${tag.name} - ${tag.usageCount} bài viết", modifier = Modifier.padding(16.dp).clickable { 
                                    navController.navigate("tag_posts/${tag.name}")
                                })
                                HorizontalDivider()
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchUserItem(
    username: String,
    subtitle: String,
    avatarUrl: String?,
    onFollowClick: () -> Unit,
    onUserClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onUserClick() }
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = avatarUrl,
            contentDescription = "Avatar",
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop,
            placeholder = ColorPainter(Color.LightGray),
            error = ColorPainter(Color.LightGray)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text("@$username", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            Text(subtitle, color = Color.Gray, style = MaterialTheme.typography.bodyMedium)
        }
        Button(
            onClick = onFollowClick,
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.height(34.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF6B35))
        ) {
            Text("Follow", style = MaterialTheme.typography.labelMedium, color = Color.White)
        }
    }
    HorizontalDivider(color = Color.LightGray.copy(alpha = 0.3f))
}

