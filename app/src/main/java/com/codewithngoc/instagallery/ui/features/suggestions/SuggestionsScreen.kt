package com.codewithngoc.instagallery.ui.features.suggestions

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import com.codewithngoc.instagallery.data.model.SearchUserResult

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SuggestionsScreen(
    navController: NavController,
    viewModel: SuggestionsViewModel = hiltViewModel()
) {
    val suggestions by viewModel.suggestions.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gợi ý cho bạn", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Quay lại")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color.White
    ) { padding ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFFFF6B35))
            }
        } else if (error != null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(error ?: "Có lỗi", color = Color.Red)
            }
        } else {
            LazyColumn(
                modifier = Modifier.padding(padding).fillMaxSize()
            ) {
                items(suggestions) { user ->
                    SuggestionUserItem(
                        user = user,
                        onFollowClick = { /* Handle follow */ },
                        onUserClick = { navController.navigate("user_profile/${user.id}") }
                    )
                }
            }
        }
    }
}

@Composable
private fun SuggestionUserItem(
    user: SearchUserResult,
    onFollowClick: () -> Unit,
    onUserClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onUserClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = user.profilePictureUrl,
            contentDescription = "Avatar",
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop,
            placeholder = ColorPainter(Color.LightGray),
            error = ColorPainter(Color.LightGray)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(user.username, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            Text(user.fullName, color = Color.Gray, style = MaterialTheme.typography.bodyMedium)
        }
        Button(
            onClick = onFollowClick,
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF6B35))
        ) {
            Text("Follow", fontWeight = FontWeight.Bold)
        }
    }
}
