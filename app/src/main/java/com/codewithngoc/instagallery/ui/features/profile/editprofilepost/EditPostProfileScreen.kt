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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.codewithngoc.instagallery.data.model.PostVisibility

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditPostProfileScreen(
    postId: String = "",
    onBack: () -> Unit = {},
    onCancel: () -> Unit = {},
    onUpdate: () -> Unit = {},
    viewModel: EditPostProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    var postContent by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("Chưa có vị trí") }
    var visibility by remember { mutableStateOf(PostVisibility.PUBLIC) }

    // Dùng để đảm bảo việc đọc data vào textbox chỉ diễn ra 1 lần
    var isDataLoaded by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        if (postId.isNotEmpty()) {
            postId.toLongOrNull()?.let { viewModel.loadPostData(it) }
        }
    }

    LaunchedEffect(uiState) {
        if (uiState is EditPostState.Success && !isDataLoaded) {
            val post = (uiState as EditPostState.Success).post
            postContent = post.caption ?: ""
            location = post.location ?: "Chưa có vị trí"
            // Nếu backend trả về visibility thì set ở đây (tạm giả lập vì FeedPostResponse có thể chưa có enum này)
            visibility = PostVisibility.PUBLIC
            isDataLoaded = true
        }
    }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is EditPostEvent.UpdateSuccess -> {
                    onUpdate() // Đóng và back lại
                }
                is EditPostEvent.UpdateError -> {
                    // Logic hiện thông báo lỗi (snackbar/toast)
                }
            }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Chỉnh sửa", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    TextButton(onClick = onCancel) {
                        Text("Hủy", color = Color.Gray, fontSize = 16.sp)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color.White
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
            when (val state = uiState) {
                is EditPostState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is EditPostState.Error -> {
                    Text(text = state.message, color = Color.Red, modifier = Modifier.align(Alignment.Center))
                }
                is EditPostState.Success -> {
                    val post = state.post
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                    ) {
                        // 1. User info
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AsyncImage(
                                model = post.userAvatar,
                                contentDescription = "Avatar",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(Color.LightGray)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = post.username,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 16.sp
                            )
                        }

                        // 2. Post Image
                        val firstImageUrl = post.media.firstOrNull()?.url
                        if (!firstImageUrl.isNullOrEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .aspectRatio(0.8f) // Tỉ lệ ảnh dọc
                                    .padding(horizontal = 16.dp, vertical = 8.dp)
                                    .clip(RoundedCornerShape(8.dp))
                            ) {
                                AsyncImage(
                                    model = firstImageUrl,
                                    contentDescription = "Post Image",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                                // Nút X (chưa xử lý backend xoá ảnh riêng lẻ, nên có thể bỏ hoặc để placeholder)
                            }
                        }

                        // 3. Edit input
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
                                .clickable { /* Mở picker (sẽ code sau) */ }
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
                                text = location.ifEmpty { "Chưa có vị trí" },
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
                                .clickable { /* Mở picker (sẽ code sau) */ }
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
                                text = if (visibility == PostVisibility.PUBLIC) "Công khai" else "Bạn bè",
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
                            onClick = {
                                postId.toLongOrNull()?.let { pid ->
                                    viewModel.updatePost(
                                        postId = pid,
                                        caption = postContent,
                                        location = location,
                                        visibility = visibility
                                    )
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                                .padding(bottom = 24.dp)
                                .height(52.dp)
                                .background(
                                    brush = Brush.horizontalGradient(
                                        colors = listOf(Color(0xFFFE8C00), Color(0xFFF85D00))
                                    ),
                                    shape = RoundedCornerShape(26.dp)
                                ),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Transparent,
                                contentColor = Color.White
                            ),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text("Cập nhật", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}