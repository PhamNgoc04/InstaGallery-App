package com.codewithngoc.instagallery.ui.features.news

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.codewithngoc.instagallery.data.model.FeedPostResponse
import com.codewithngoc.instagallery.ui.features.homefeed.HomeInsBottomBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsScreen(
    navController: NavController,
    viewModel: NewsViewModel = hiltViewModel()
) {
    // Lắng nghe cả 2 StateFlows
    val uiState by viewModel.uiState.collectAsState()
    val posts by viewModel.posts.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("News", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = { HomeInsBottomBar(navController = navController) },
        containerColor = Color(0xFFF0F0F0)
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            // Hiển thị danh sách posts
            NewsList(posts = posts)

            // Hiển thị Loading hoặc Error đè lên trên danh sách
            when (val state = uiState) {
                is NewsViewModel.NewsUiEvent.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is NewsViewModel.NewsUiEvent.Error -> {
                    // Có thể hiển thị một Snackbar hoặc Dialog thay vì Text
                    Box(modifier = Modifier.fillMaxSize().align(Alignment.Center)) {
                        Text(
                            text = "Error: ${state.message}",
                            modifier = Modifier.align(Alignment.Center),
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
                else -> {
                    // Không làm gì cho trạng thái Success hoặc Nothing
                }
            }
        }
    }
}

// Các Composable NewsList và NewsItem giữ nguyên như cũ, không cần thay đổi

@Composable
fun NewsList(posts: List<FeedPostResponse>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(posts) { post ->
            NewsItem(post = post, onClick = { /* TODO: Chuyển đến chi tiết bài viết */ })
        }
    }
}

@Composable
fun NewsItem(
    post: FeedPostResponse,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val imageUrl = post.media.firstOrNull()?.url ?: ""
            Image(
                painter = rememberAsyncImagePainter(
                    ImageRequest.Builder(LocalContext.current).data(data = imageUrl).crossfade(true).build()
                ),
                contentDescription = post.caption,
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(80.dp).clip(RoundedCornerShape(8.dp))
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = post.caption ?: "No caption",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "by ${post.username}",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NewsScreenPreview() {
    NewsScreen(navController = rememberNavController())
}