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
import androidx.navigation.NavController
import coil.compose.AsyncImage

data class ChatMessage(
    val id: Int,
    val content: String,
    val senderId: Long,
    val senderName: String,
    val time: String,
    val isMe: Boolean
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatDetailScreen(
    conversationId: Long,
    navController: NavController
) {
    var inputText by remember { mutableStateOf("") }
    val messages = remember {
        listOf(
            ChatMessage(1, "Chào bạn, mình muốn đặt lịch chụp ảnh gia đình 📸", 2, "Alex", "14:30", false),
            ChatMessage(2, "Chào bạn! Bạn muốn chụp vào ngày nào?", 1, "Me", "14:31", true),
            ChatMessage(3, "Cuối tuần này được không ạ?", 2, "Alex", "14:33", false),
            ChatMessage(4, "Được chứ! Thứ 7 lúc 9h sáng nhé 😊", 1, "Me", "14:34", true)
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        AsyncImage(
                            model = null,
                            contentDescription = "Avatar",
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape),
                            placeholder = ColorPainter(Color.LightGray),
                            error = ColorPainter(Color.LightGray)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("Alex Photo", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF4CAF50))
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("📷 Photographer", fontSize = 12.sp, color = Color.Gray)
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
        bottomBar = {
            // Input bar
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.White,
                shadowElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Emoji button
                    IconButton(onClick = { }, modifier = Modifier.size(36.dp)) {
                        Text("😊", fontSize = 20.sp)
                    }

                    // Input field
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

                    // Camera button
                    IconButton(onClick = { }, modifier = Modifier.size(36.dp)) {
                        Text("📷", fontSize = 18.sp)
                    }

                    // Send button
                    IconButton(
                        onClick = { inputText = "" },
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
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            reverseLayout = false,
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Timestamp header
            item {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("14:30", fontSize = 12.sp, color = Color.Gray)
                }
            }

            items(messages) { message ->
                ChatBubble(message)
            }
        }
    }
}

@Composable
private fun ChatBubble(message: ChatMessage) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (message.isMe) Alignment.End else Alignment.Start
    ) {
        if (!message.isMe) {
            Text(
                message.senderName,
                fontSize = 12.sp,
                color = Color.Gray,
                modifier = Modifier.padding(start = 12.dp, bottom = 2.dp)
            )
        }

        Row(
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = if (message.isMe) Arrangement.End else Arrangement.Start,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (!message.isMe) {
                AsyncImage(
                    model = null,
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
                    bottomStart = if (message.isMe) 16.dp else 4.dp,
                    bottomEnd = if (message.isMe) 4.dp else 16.dp
                ),
                color = if (message.isMe) Color(0xFFFF6B35) else Color.White,
                shadowElevation = 1.dp
            ) {
                Text(
                    message.content,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                    color = if (message.isMe) Color.White else Color.Black,
                    fontSize = 15.sp
                )
            }
        }

        Text(
            message.time,
            fontSize = 11.sp,
            color = Color.Gray,
            modifier = Modifier.padding(
                start = if (message.isMe) 0.dp else 40.dp,
                end = if (message.isMe) 4.dp else 0.dp,
                top = 2.dp
            )
        )
    }
}
