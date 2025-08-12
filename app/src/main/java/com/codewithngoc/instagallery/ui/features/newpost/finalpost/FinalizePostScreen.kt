package com.codewithngoc.instagallery.ui.features.newpost.finalpost

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
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

@Composable
fun FinalizePostScreen(
    navController: NavController,
    selectedUri: Uri,
    viewModel: FinalizePostViewModel = hiltViewModel()
) {
    val caption by viewModel.caption.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.navigationEvent.collect { event ->
            if (event is FinalizePostViewModel.FinalizePostNavigationEvent.NavigateToHomeFeed) {
                navController.popBackStack(Screen.HomeFeed.route, inclusive = false)
            }
        }
    }

    Scaffold(
        topBar = { FinalizePostTopBar(navController) },
        bottomBar = { FinalizePostBottomBar(viewModel::uploadPost, uiState) },
        containerColor = Color.White
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            PostSummary(selectedUri = selectedUri, caption = caption, onCaptionChanged = viewModel::onCaptionChanged)
            Spacer(modifier = Modifier.height(16.dp))
            Divider(color = Color.Gray.copy(alpha = 0.3f), thickness = 1.dp)
            PostOptions()
        }
    }
}

@Composable
fun FinalizePostTopBar(navController: NavController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Close,
            contentDescription = "Close",
            tint = Color.Black,
            modifier = Modifier.clickable { navController.popBackStack() }
        )
        Text(
            text = "Bài viết mới",
            color = Color.Black,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
//        Text(
//            text = "Chia sẻ",
//            color = MaterialTheme.colorScheme.primary,
//            fontSize = 16.sp,
//            fontWeight = FontWeight.Bold,
//            modifier = Modifier.clickable { /* TODO: Trigger post upload */ }
//        )
    }
}

@Composable
fun PostSummary(selectedUri: Uri, caption: String, onCaptionChanged: (String) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        AsyncImage(
            model = selectedUri,
            contentDescription = "Selected Media",
            modifier = Modifier
                .size(60.dp)
                .background(Color.Gray),
            contentScale = ContentScale.Crop
        )
        BasicTextField(
            value = caption,
            onValueChange = onCaptionChanged,
            textStyle = TextStyle(fontSize = 16.sp, color = Color.Black),
            modifier = Modifier
                .weight(1f)
                .padding(start = 16.dp),
            decorationBox = { innerTextField ->
                if (caption.isEmpty()) {
                    Text("Thêm chú thích...", color = Color.Gray)
                }
                innerTextField()
            }
        )
    }
}

@Composable
fun PostOptions() {
    Column(modifier = Modifier.padding(16.dp)) {
        PostOptionItem(
            iconRes = R.drawable.ic_music,
            text = "Thêm âm thanh",
            subText = "august - Taylo..."
        )
        PostOptionItem(
            iconRes = R.drawable.ic_gui,
            text = "Gắn thẻ người khác"
        )
        PostOptionItem(
            iconRes = R.drawable.ic_gui,
            text = "Thêm vị trí"
        )
        PostOptionItem(
            iconRes = R.drawable.ic_gui,
            text = "Thêm liên kết"
        )
    }
}

@Composable
fun PostOptionItem(iconRes: Int, text: String, subText: String? = null) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .clickable { /* Handle option click */ }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(id = iconRes),
                contentDescription = text,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = text, fontSize = 16.sp, color = Color.Black)
            if (subText != null) {
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = subText, fontSize = 14.sp, color = Color.Gray)
            }
        }
        Image(
            painter = painterResource(id = R.drawable.ic_dropdown),
            contentDescription = "Navigate",
            modifier = Modifier.size(16.dp)
        )
    }
}

@Composable
fun FinalizePostBottomBar(onPostClicked: () -> Unit, uiState: FinalizePostViewModel.FinalizePostEvent) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Button(
            onClick = onPostClicked,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            ),
            enabled = uiState !is FinalizePostViewModel.FinalizePostEvent.Loading
        ) {
            when (uiState) {
                is FinalizePostViewModel.FinalizePostEvent.Loading -> {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                }
                else -> {
                    Text("Chia sẻ", color = Color.White, fontSize = 18.sp)
                }
            }
        }
    }
}