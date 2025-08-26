package com.codewithngoc.instagallery.ui.features.profile.editprofilepost

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

@Composable
fun EditPostProfileScreen(
    postId: String = "",
    userName: String = "Tommy Jackson",
    userAvatar: String = "https://i.pravatar.cc/150?img=3",
    postImage: String = "https://picsum.photos/600/400",
    postText: String = "Lorem ipsum dolor sit amet...",
    onCancel: () -> Unit = {},
    onUpdate: (String) -> Unit = {}
) {
    var content by remember { mutableStateOf(postText) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        // 🔹 Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onCancel) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }
            Text(
                text = "Edit Post",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Cancel",
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                modifier = Modifier.clickable { onCancel() }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 🔹 User Info
        Row(verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = userAvatar,
                contentDescription = "Avatar",
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(userName, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 🔹 Post Image
        Box(modifier = Modifier.fillMaxWidth()) {
            AsyncImage(
                model = postImage,
                contentDescription = "Post Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )

            IconButton(
                onClick = { /* TODO: Remove image */ },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
                    .background(Color.White, CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Remove Image",
                    tint = Color.Red
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 🔹 Content field
        OutlinedTextField(
            value = content,
            onValueChange = { content = it },
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp),
            textStyle = TextStyle(fontSize = 14.sp),
            placeholder = { Text("Write your post...") }
        )

        Spacer(modifier = Modifier.height(20.dp))

        // 🔹 Update button with gradient
        Button(
            onClick = { onUpdate(content) },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(24.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent
            ),
            contentPadding = PaddingValues() // remove default padding
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.horizontalGradient(
                            listOf(Color(0xFFFF9800), Color(0xFFE91E63))
                        ),
                        shape = RoundedCornerShape(24.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Update",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        }
    }
}
