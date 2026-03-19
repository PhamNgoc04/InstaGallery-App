package com.codewithngoc.instagallery.ui.features.messages

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage

data class ConversationPreview(
    val id: Long,
    val name: String,
    val lastMessage: String,
    val time: String,
    val hasUnread: Boolean = false,
    val avatar: String? = null
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessagesScreen(navController: NavController) {
    val conversations = remember {
        listOf(
            ConversationPreview(1, "Apollo Phelps", "Live, Love, Bark", "5:30 PM"),
            ConversationPreview(2, "Bailey Stein", "Dog kisses fix everything", "4:32 PM"),
            ConversationPreview(3, "Bandit Brown", "You can't buy love but...", "3:55 PM", true),
            ConversationPreview(4, "Benji Gordon", "I love it", "Hôm qua"),
            ConversationPreview(5, "Hank Lozano", "Adorable", "Hôm qua"),
            ConversationPreview(6, "Tommy Jackson", "Lorem ipsum dolor", "5:30 PM")
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Quay lại")
                    }
                },
                actions = {
                    IconButton(onClick = { }) {
                        Icon(Icons.Default.Edit, "Tin nhắn mới", tint = Color.DarkGray)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color.White
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            Text(
                "Messages",
                modifier = Modifier.padding(start = 16.dp, bottom = 8.dp),
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )

            LazyColumn {
                items(conversations) { conv ->
                    ConversationRow(
                        conversation = conv,
                        onClick = { navController.navigate("chat_detail/${conv.id}") }
                    )
                }
            }
        }
    }
}

@Composable
private fun ConversationRow(conversation: ConversationPreview, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Avatar
        AsyncImage(
            model = conversation.avatar,
            contentDescription = "Avatar",
            modifier = Modifier
                .size(52.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop,
            placeholder = ColorPainter(Color.LightGray),
            error = ColorPainter(Color.LightGray)
        )

        Spacer(modifier = Modifier.width(12.dp))

        // Content
        Column(modifier = Modifier.weight(1f)) {
            Text(
                conversation.name,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            Text(
                conversation.lastMessage,
                color = Color.Gray,
                fontSize = 14.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        // Time and unread
        Column(horizontalAlignment = Alignment.End) {
            Text(conversation.time, fontSize = 12.sp, color = Color.Gray)
            if (conversation.hasUnread) {
                Spacer(modifier = Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFF6B35))
                )
            }
        }
    }
}
