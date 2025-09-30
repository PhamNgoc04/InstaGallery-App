package com.codewithngoc.instagallery.ui.features.news

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.codewithngoc.instagallery.ui.features.homefeed.HomeInsBottomBar

// Data class để đại diện cho một bài báo
data class NewsArticle(
    val id: Int,
    val title: String,
    val subtitle: String,
    val imageUrl: String
)

// Dữ liệu mẫu (bạn sẽ thay thế bằng dữ liệu từ API sau này)
val sampleNews = listOf(
    NewsArticle(1, "Dogs Are Even More Like Us...", "Lorem ipsum dolor sit amet, consectetur ad...", "https://images.unsplash.com/photo-1548199973-03cce0bbc87b"),
    NewsArticle(2, "Is Your Dog Super Smart?", "Lorem ipsum dolor sit amet, consectetur ad...", "https://images.unsplash.com/photo-1552053831-71594a27632d"),
    NewsArticle(3, "Do you think dog's are super...", "Lorem ipsum dolor sit amet, consectetur ad...", "https://images.unsplash.com/photo-1537151625747-768eb6cf92b2"),
    NewsArticle(4, "Your Dog Knows When You're...", "Lorem ipsum dolor sit amet, consectetur ad...", "https://images.unsplash.com/photo-1505628346881-b72b27e84530"),
    NewsArticle(5, "Dogs Might Be More Rational...", "Lorem ipsum dolor sit amet, consectetur ad...", "https://images.unsplash.com/photo-1529429617124-95b109e86bb8"),
    NewsArticle(6, "Are Dogs More Likely To Bite...", "Lorem ipsum dolor sit amet, consectetur ad...", "https://images.unsplash.com/photo-1560807707-8cc77767d783")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsScreen(navController: NavController) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("News", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.White
                )
            )
        },

        bottomBar = {
            HomeInsBottomBar(navController = navController)
        },

        containerColor = Color(0xFFF0F0F0) // Màu nền xám nhạt
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(sampleNews) { article ->
                NewsItem(article = article, onClick = { /* TODO: Handle click news item */ })
            }
        }
    }
}

@Composable
fun NewsItem(
    article: NewsArticle,
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
            Image(
                painter = rememberAsyncImagePainter(
                    ImageRequest.Builder(LocalContext.current).data(data = article.imageUrl)
                        .apply(block = fun ImageRequest.Builder.() {
                            crossfade(true)
                        }).build()
                ),
                contentDescription = article.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp))
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = article.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = article.subtitle,
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