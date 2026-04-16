package com.codewithngoc.instagallery.ui.features.messages.chatdetail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
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
import com.codewithngoc.instagallery.data.model.ChatMessageResponse
import com.codewithngoc.instagallery.data.utils.formatTimeAgo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatDetailScreen(
    conversationId: Long,
    navController: NavController,
    viewModel: ChatDetailViewModel = hiltViewModel()
) {
    var inputText by remember { mutableStateOf("") }
    val messages by viewModel.messages.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val partnerName by viewModel.partnerName.collectAsState()
    val partnerAvatar by viewModel.partnerAvatar.collectAsState()
    val currentUserId = viewModel.currentUserId

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        AsyncImage(
                            model = partnerAvatar,
                            contentDescription = "Avatar",
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape),
                            placeholder = ColorPainter(Color.LightGray),
                            error = ColorPainter(Color.LightGray)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                partnerName ?: "...",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF4CAF50))
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Online", fontSize = 12.sp, color = Color.Gray)
                            }
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Quay lại")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        // ✅ FIX: bottomBar với imePadding để không bị che bởi bàn phím và navigation bar
        bottomBar = {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()  // tránh bị thanh nav bar che
                    .imePadding(),            // đẩy lên khi bàn phím xuất hiện
                color = Color.White,
                shadowElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { }, modifier = Modifier.size(36.dp)) {
                        Text("😊", fontSize = 20.sp)
                    }

                    OutlinedTextField(
                        value = inputText,
                        onValueChange = { inputText = it },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("Nhập tin nhắn...", color = Color.Gray) },
                        shape = RoundedCornerShape(24.dp),
                        singleLine = false,
                        maxLines = 3,
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = Color.LightGray,
                            focusedBorderColor = Color(0xFFFF6B35)
                        )
                    )

                    IconButton(onClick = { }, modifier = Modifier.size(36.dp)) {
                        Text("📷", fontSize = 18.sp)
                    }

                    IconButton(
                        onClick = {
                            if (inputText.isNotBlank()) {
                                viewModel.sendMessage(inputText)
                                inputText = ""
                            }
                        },
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFFF6B35))
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.Send,
                            "Gửi",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        },
        containerColor = Color(0xFFF5F5F5)
    ) { padding ->
        val listState = androidx.compose.foundation.lazy.rememberLazyListState()

        // ✅ Key theo ID tin mới nhất — scroll xuống kể cả khi size không đổi (temp → server msg swap)
        val lastMessageId = messages.lastOrNull()?.id
        LaunchedEffect(lastMessageId) {
            if (messages.isNotEmpty()) {
                listState.animateScrollToItem(messages.lastIndex)
            }
        }

        LazyColumn(
            state = listState,
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            reverseLayout = false, // ASC sort → không cần reverse
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (isLoading) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Color(0xFFFF6B35))
                    }
                }
            } else {
                items(messages) { message ->
                    val isMe = message.senderId == currentUserId
                    ChatBubble(message, isMe)
                }
            }
        }
    }
}

@Composable
private fun ChatBubble(message: ChatMessageResponse, isMe: Boolean) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (isMe) Alignment.End else Alignment.Start
    ) {
        Row(
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = if (isMe) Arrangement.End else Arrangement.Start,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (!isMe) {
                AsyncImage(
                    model = message.senderAvatar,
                    contentDescription = "Avatar",
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape),
                    placeholder = ColorPainter(Color.LightGray),
                    error = ColorPainter(Color.LightGray)
                )
                Spacer(modifier = Modifier.width(6.dp))
            }

            Surface(
                shape = RoundedCornerShape(
                    topStart = 16.dp,
                    topEnd = 16.dp,
                    bottomStart = if (isMe) 16.dp else 4.dp,
                    bottomEnd = if (isMe) 4.dp else 16.dp
                ),
                color = if (isMe) Color(0xFFFF6B35) else Color.White,
                shadowElevation = 1.dp
            ) {
                Text(
                    message.content ?: "",
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                    color = if (isMe) Color.White else Color.Black,
                    fontSize = 15.sp
                )
            }
        }

        Text(
            formatTimeAgo(message.createdAt ?: ""),
            fontSize = 11.sp,
            color = Color.Gray,
            modifier = Modifier.padding(
                start = if (isMe) 0.dp else 40.dp,
                end = if (isMe) 4.dp else 0.dp,
                top = 2.dp
            )
        )
    }
}
