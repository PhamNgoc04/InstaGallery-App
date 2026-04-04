package com.codewithngoc.instagallery.ui.features.profile.editprofilepost

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditPostProfileScreen(
    onBack: () -> Unit = {},
    onCancel: () -> Unit = {},
    onUpdate: (String) -> Unit = {}
) {
    var postContent by remember {
        mutableStateOf("Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.")
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Chỉnh sửa",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
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
                        Text("Hủy", color = Color.Gray, fontSize = 16.sp)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        containerColor = Color.White
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
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
                Spacer(modifier = Modifier.width(12.dp))
                // Tên người dùng
                Text(
                    text = "Ngọc Jackson",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp
                )
            }

            // 2. Phần ảnh bài đăng
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(0.8f) // Tỉ lệ ảnh dọc
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .clip(RoundedCornerShape(8.dp))
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
                        .background(Color.Black.copy(alpha = 0.4f))
                        .size(30.dp)
                ) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "Remove Image",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // 3. Phần nội dung có thể chỉnh sửa
            OutlinedTextField(
                value = postContent,
                onValueChange = { postContent = it },
                textStyle = LocalTextStyle.current.copy(fontSize = 15.sp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color(0xFFE0E0E0),
                    focusedBorderColor = Color.Gray
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            // 4. Location Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { /* Mở location picker */ }
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.LocationOn,
                    contentDescription = "Location",
                    tint = Color.DarkGray
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "TP. Hồ Chí Minh",
                    fontSize = 16.sp,
                    color = Color.DarkGray,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.KeyboardArrowRight,
                    contentDescription = "Arrow right",
                    tint = Color.LightGray
                )
            }

            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), thickness = 0.5.dp, color = Color(0xFFEEEEEE))

            // 5. Visibility Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { /* Mở visibility picker */ }
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.Lock,
                    contentDescription = "Visibility",
                    tint = Color.DarkGray
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Công khai",
                    fontSize = 16.sp,
                    color = Color.DarkGray,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.KeyboardArrowRight,
                    contentDescription = "Arrow right",
                    tint = Color.LightGray
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Nút Update
            Button(
                onClick = { onUpdate(postContent) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 24.dp)
                    .height(52.dp)
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(Color(0xFFFE8C00), Color(0xFFF85D00)) // Gradient màu cam
                        ),
                        shape = RoundedCornerShape(26.dp)
                    ),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent, // Làm cho màu nền trong suốt để thấy gradient
                    contentColor = Color.White
                ),
                contentPadding = PaddingValues(0.dp)
            ) {
                Text("Cập nhật", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
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