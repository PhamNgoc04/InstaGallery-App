package com.codewithngoc.instagallery.ui.features.profile.editprofilepost

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditPostProfileScreen(
    onBack: () -> Unit = {},
    onCancel: () -> Unit = {},
    onUpdate: (String) -> Unit = {}
) {
    var postContent by remember {
        // Đây là nội dung Lorem Ipsum trong ảnh
        mutableStateOf("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed eiusmod tempor amet en sed do eiusmod tempor quiae incididunt utell labore etoneme dolore magna.")
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Edit Post",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    TextButton(onClick = onCancel) {
                        Text("Cancel")
                    }
                }
            )
        },
        bottomBar = {
            // Nút Update
            Button(
                onClick = { onUpdate(postContent) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .padding(bottom = 10.dp)
                    .height(50.dp)
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(Color(0xFFFE8C00), Color(0xFFF83600)) // Gradient màu cam/hồng
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent, // Làm cho màu nền trong suốt để thấy gradient
                    contentColor = Color.White
                ),
                contentPadding = PaddingValues(0.dp)
            ) {
                Text("Update", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // 1. Phần thông tin người dùng
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Ảnh đại diện
                Image(
                    painter = rememberAsyncImagePainter("https://i.imgur.com/KzX3s9n.png"), // Thay thế bằng URL ảnh
                    contentDescription = "Avatar",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                )
                Spacer(modifier = Modifier.width(8.dp))
                // Tên người dùng
                Text(
                    text = "Tommy Jackson",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp
                )
            }

            // 2. Phần ảnh bài đăng
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.5f) // Tỉ lệ ảnh (ví dụ 3:2)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .clip(RoundedCornerShape(8.dp)) // Bo góc cho ảnh
            ) {
                // Ảnh chính
                Image(
                    painter = rememberAsyncImagePainter("https://images.unsplash.com/photo-1507525428034-b723cf961d3e"), // Thay thế bằng URL ảnh
                    contentDescription = "Post Image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Nút "X" (Close) ở góc trên bên phải
                IconButton(
                    onClick = { /* Xử lý sự kiện xóa ảnh */ },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.5f))
                        .size(24.dp)
                ) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "Remove Image",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            // 3. Phần nội dung có thể chỉnh sửa
            BasicTextField(
                value = postContent,
                onValueChange = { postContent = it },
                textStyle = LocalTextStyle.current.copy(fontSize = 16.sp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp) // Cố định chiều cao cho vùng nhập liệu
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )
            Divider(modifier = Modifier.padding(horizontal = 16.dp)) // Đường phân cách
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewEditPostScreen() {
    MaterialTheme {
        EditPostProfileScreen()
    }
}