package com.codewithngoc.instagallery.ui.features.newpost.editpost

import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.codewithngoc.instagallery.R
import com.codewithngoc.instagallery.ui.features.homefeed.HomeFeedViewModel
import com.codewithngoc.instagallery.ui.navigation.Screen
import com.codewithngoc.instagallery.ui.theme.InstaGalleryAppTheme

@Composable
fun EditPostScreen(
    navController: NavController,
    selectedUri: Uri,
    viewModel: EditPostViewModel = hiltViewModel(),
    homeFeedViewModel: HomeFeedViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    // Set media 1 lần
    LaunchedEffect(selectedUri) {
        viewModel.onMediaSelected(selectedUri)
    }

    // Lắng nghe sự kiện điều hướng
    LaunchedEffect(Unit) {
        viewModel.navigationEvent.collect { event ->
            when (event) {
                EditPostViewModel.EditPostNavigationEvent.NavigateBackToHome -> {
                    navController.navigate(Screen.HomeFeed.route) {
                        popUpTo(Screen.HomeFeed.route) { inclusive = true }
                    }
                }
            }
        }
    }

    val uiState by viewModel.uiState.collectAsState()
    val caption by viewModel.caption.collectAsState()

    // Xử lý Error
    if (uiState is EditPostViewModel.EditPostEvent.Error) {
        val message = (uiState as EditPostViewModel.EditPostEvent.Error).message
        LaunchedEffect(message) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        topBar = {
            NewPostTopBar(
                navController,
                onShareClick = {
                    viewModel.uploadAndCreatePost(
                        caption = caption,
                        imageUri = selectedUri,
                        context = context,
                        navController = navController,
                        homeFeedViewModel = homeFeedViewModel
                    )
                }
            )
        },
        bottomBar = {
            NewPostBottomBar(
                onShareClick = {
                    viewModel.uploadAndCreatePost(
                        caption = caption,
                        imageUri = selectedUri,
                        context = context,
                        navController = navController,
                        homeFeedViewModel = homeFeedViewModel
                    )
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item {
                    AsyncImage(
                        model = selectedUri,
                        contentDescription = "Selected Image",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp),
                        contentScale = ContentScale.Crop
                    )

                    OutlinedTextField(
                        value = caption,
                        onValueChange = { viewModel.onCaptionChanged(it) },
                        label = { Text("Thêm chú thích...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    )

                    Divider(color = Color.LightGray, thickness = 0.5.dp, modifier = Modifier.padding(horizontal = 16.dp))
                    NewPostOption(iconRes = R.drawable.ic_music, text = "Thêm âm thanh")
                    NewPostOption(iconRes = R.drawable.ic_gui, text = "Gắn thẻ người khác")
                    NewPostOption(iconRes = R.drawable.ic_location, text = "Thêm vị trí")
                    NewPostOption(iconRes = R.drawable.ic_ai, text = "Thêm AI", showSwitch = true)

                    Divider(color = Color.LightGray, thickness = 0.5.dp, modifier = Modifier.padding(horizontal = 16.dp))
                    NewPostOption(iconRes = R.drawable.ic_object, text = "Đối tượng", trailingText = "Mọi người")
                    NewPostOption(iconRes = R.drawable.ic_share_on, text = "Cùng chia sẻ trên...", trailingText = "Đang tắt")
                    NewPostOption(iconRes = R.drawable.ic_more, text = "Lựa chọn khác")
                }
            }

            if (uiState is EditPostViewModel.EditPostEvent.Loading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.3f)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color.White)
                }
            }
        }
    }
}

@Composable
fun NewPostBottomBar(onShareClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Button(
            onClick = onShareClick,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Text("Chia sẻ", color = Color.White)
        }
    }
}

// Top Bar của màn hình đăng bài mới
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewPostTopBar(navController: NavController, onShareClick: () -> Unit) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                "Bài viết mới",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        },
        navigationIcon = {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Quay lại"
                )
            }
        },
        modifier = Modifier.background(Color.White)
    )
}

// Component cho các tùy chọn trong danh sách
@Composable
fun NewPostOption(iconRes: Int, text: String, trailingText: String? = null, showSwitch: Boolean = false) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { /* Xử lý click */ }
            .padding(vertical = 12.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                painter = painterResource(id = iconRes),
                contentDescription = text,
                modifier = Modifier.size(24.dp),
                tint = Color.Black
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = text, fontSize = 16.sp)
        }
        if (showSwitch) {
            Switch(checked = false, onCheckedChange = { /* Xử lý switch */ })
        } else if (trailingText != null) {
            Text(text = trailingText, color = Color.Gray)
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun NewPostScreenPreview() {
    val navController = rememberNavController()
    val mockUri = Uri.parse("https://picsum.photos/id/1018/1080/1920")
    InstaGalleryAppTheme {
        EditPostScreen(navController = navController, selectedUri = mockUri)
    }
}