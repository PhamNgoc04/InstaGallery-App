package com.codewithngoc.instagallery.ui.features.newpost.editpost

import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.codewithngoc.instagallery.R
import com.codewithngoc.instagallery.ui.features.homefeed.HomeFeedViewModel
import com.codewithngoc.instagallery.ui.floatingLabelTextField.FloatingLabelTextField
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
    var isAIEnabled by remember { mutableStateOf(false) }

    // Xử lý sự kiện đóng dialog
    var showCloseDialog by remember { mutableStateOf(false) }

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
    val isCaptionEmpty = caption.isEmpty()

    // Xử lý Error
    if (uiState is EditPostViewModel.EditPostEvent.Error) {
        val message = (uiState as EditPostViewModel.EditPostEvent.Error).message
        LaunchedEffect(message) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        topBar = {
            EditPostTopBar(
                navController,
                onCloseClick = { showCloseDialog = true },
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
            EditPostBottomBar(
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
                .wrapContentHeight()
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(selectedUri)
                            .crossfade(true)
                            .build(),
                        contentDescription = "Selected Image",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp)
                            .clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Fit,
                        placeholder = painterResource(R.drawable.placeholder_post),
                        error = painterResource(R.drawable.error_post)
                    )


                    TextField(
                        value = caption,
                        onValueChange = { viewModel.onCaptionChanged(it) },
                        placeholder = {
                            // Placeholder sẽ tự động biến mất khi có văn bản hoặc khi focus
                            Text("Thêm chú thích...")
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        // Loại bỏ các viền mặc định của TextField
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            disabledContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            disabledIndicatorColor = Color.Transparent
                        )
                    )

                    Divider(color = Color.LightGray, thickness = 0.5.dp, modifier = Modifier.padding(horizontal = 16.dp))

                    EditPostOption(
                        iconRes = R.drawable.ic_music,
                        text = "Thêm âm thanh",
                        onOptionClick = { Toast.makeText(context, "Mở dialog chọn nhạc", Toast.LENGTH_SHORT).show() }
                    )
                    EditPostOption(
                        iconRes = R.drawable.ic_gui,
                        text = "Gắn thẻ người khác",
                        onOptionClick = { Toast.makeText(context, "Mở dialog gắn thẻ", Toast.LENGTH_SHORT).show() }
                    )
                    EditPostOption(
                        iconRes = R.drawable.ic_location,
                        text = "Thêm vị trí",
                        onOptionClick = { Toast.makeText(context, "Mở dialog chọn vị trí", Toast.LENGTH_SHORT).show() }
                    )
                    EditPostOption(
                        iconRes = R.drawable.ic_ai,
                        text = "Thêm AI",
                        showSwitch = true,
                        isSwitchChecked = isAIEnabled,
                        onSwitchChange = { isAIEnabled = it; Toast.makeText(context, "AI đã ${if (it) "bật" else "tắt"}", Toast.LENGTH_SHORT).show() }
                    )

                    Divider(color = Color.LightGray, thickness = 0.5.dp, modifier = Modifier.padding(horizontal = 16.dp))

                    EditPostOption(
                        iconRes = R.drawable.ic_object,
                        text = "Đối tượng",
                        trailingText = "Mọi người",
                        onOptionClick = { Toast.makeText(context, "Mở dialog chọn đối tượng", Toast.LENGTH_SHORT).show() }
                    )
                    EditPostOption(
                        iconRes = R.drawable.ic_share_on,
                        text = "Cùng chia sẻ trên...",
                        trailingText = "Đang tắt",
                        onOptionClick = { Toast.makeText(context, "Mở dialog chia sẻ trên", Toast.LENGTH_SHORT).show() }
                    )
                    EditPostOption(
                        iconRes = R.drawable.ic_more,
                        text = "Lựa chọn khác",
                        onOptionClick = { Toast.makeText(context, "Mở dialog tùy chọn khác", Toast.LENGTH_SHORT).show() }
                    )
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

    // Show the dialog if the state is true
    if (showCloseDialog) {
        ExitConfirmationDialog(
            onDismiss = { showCloseDialog = false },
            onDiscard = {
                // Discard changes and navigate back to the NewPostScreen
                showCloseDialog = false
                navController.popBackStack()
            },
            onSaveDraft = {
                // Handle saving draft logic here
                showCloseDialog = false
                Toast.makeText(context, "Đã lưu bản nháp", Toast.LENGTH_SHORT).show()
                // You might also want to navigate back after saving
            },
            onContinue = { showCloseDialog = false } // Dismiss dialog and stay on screen
        )
    }
}

// Top Bar của màn hình đăng bài mới
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditPostTopBar(
    navController: NavController,
    onCloseClick: () -> Unit,
    onShareClick: () -> Unit) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                "Bài viết mới",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        },
        navigationIcon = {
            IconButton(onClick = { onCloseClick() }) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Quay lại"
                )
            }
        },
        modifier = Modifier.background(Color.White)
    )
}

@Composable
fun ExitConfirmationDialog(
    onDismiss: () -> Unit,
    onDiscard: () -> Unit, // Renamed from onRestart for clarity
    onSaveDraft: () -> Unit,
    onContinue: () -> Unit
) {
    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Bắt đầu lại?", color = Color.White) },
        text = { Text(text = "Nếu quay lại bây giờ, bạn sẽ mất bản nháp này.", color = Color.Gray) },
        containerColor = Color.Black.copy(alpha = 0.85f),
        titleContentColor = Color.White,
        textContentColor = Color.White,
        confirmButton = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TextButton(onClick = onDiscard) {
                    Text("Bắt đầu lại", color = Color.Red, fontWeight = FontWeight.Bold)
                }
                Divider(color = Color.DarkGray)
                TextButton(onClick = onSaveDraft) {
                    Text("Lưu bản nháp", color = Color.White)
                }
                Divider(color = Color.DarkGray)
                TextButton(onClick = onContinue) {
                    Text("Tiếp tục chỉnh sửa", color = Color.White)
                }
            }
        }
    )
}

@Composable
fun EditPostBottomBar(onShareClick: () -> Unit) {
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


// Component cho các tùy chọn trong danh sách
@Composable
fun EditPostOption(
    iconRes: Int,
    text: String,
    trailingText: String? = null,
    showSwitch: Boolean = false,
    isSwitchChecked: Boolean = false,
    onOptionClick: (() -> Unit)? = null,
    onSwitchChange: ((Boolean) -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = onOptionClick != null) { onOptionClick?.invoke() }
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
            Switch(
                checked = isSwitchChecked,
                onCheckedChange = { onSwitchChange?.invoke(it) }
            )
        } else if (trailingText != null) {
            Text(text = trailingText, color = Color.Gray)
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun EditPostScreenPreview() {
    val navController = rememberNavController()
    val mockUri = Uri.parse("https://picsum.photos/id/1018/1080/1920")
    InstaGalleryAppTheme {
        EditPostScreen(navController = navController, selectedUri = mockUri)
    }
}
