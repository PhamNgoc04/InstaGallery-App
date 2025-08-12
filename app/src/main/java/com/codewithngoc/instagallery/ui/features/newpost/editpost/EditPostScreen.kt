package com.codewithngoc.instagallery.ui.features.newpost.editpost

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.codewithngoc.instagallery.R
import com.codewithngoc.instagallery.ui.navigation.Screen
import com.codewithngoc.instagallery.ui.theme.InstaGalleryAppTheme

@Composable
fun EditPostScreen(
    navController: NavController,
    selectedUri: Uri
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // 1. Hiển thị ảnh đã chọn
        AsyncImage(
            model = selectedUri,
            contentDescription = "Selected Image",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Fit
        )

        // 3. Top bar
        EditPostTopBar(navController = navController)

        // 4. Toolbar chỉnh sửa ảnh
        EditPostToolBar(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 80.dp) // Offset để không che nút "Tiếp"
        )

        // 5. Nút "Tiếp"
        FloatingActionButton(
            onClick = {
                navController.navigate(Screen.FinalizePost.createRoute(Uri.encode(selectedUri.toString())))
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 16.dp, end = 16.dp),
            containerColor = MaterialTheme.colorScheme.primary
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_dropdown), // Giả định bạn có icon này
                contentDescription = "Tiếp",
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

// Top Bar của màn hình chỉnh sửa
@Composable
fun EditPostTopBar(navController: NavController) {
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
            tint = Color.White,
            modifier = Modifier.clickable { navController.popBackStack() }
        )
        // Phần Profile/Stories
        Row(
            modifier = Modifier
                .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                .padding(vertical = 4.dp, horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_register_logo), // Thay bằng avatar người dùng
                contentDescription = "Profile Picture",
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Phép màu", color = Color.White, fontWeight = FontWeight.Bold)
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add",
                tint = Color.White,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
}

// Toolbar dưới cùng
@Composable
fun EditPostToolBar(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp, horizontal = 8.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        ToolBarButton(iconRes = R.drawable.ic_music, text = "Nhạc")
        ToolBarButton(iconRes = R.drawable.ic_text, text = "Văn bản")
        ToolBarButton(iconRes = R.drawable.ic_overlay, text = "Lớp phủ")
        ToolBarButton(iconRes = R.drawable.ic_filter, text = "Bộ lọc")
        ToolBarButton(iconRes = R.drawable.ic_edit, text = "Chỉnh sửa")
    }
}

// Nút trong Toolbar
@Composable
fun ToolBarButton(iconRes: Int, text: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { /* Handle click */ }
    ) {
        Image(
            painter = painterResource(id = iconRes),
            contentDescription = text,
            modifier = Modifier.size(24.dp)
        )
        Text(text = text, color = Color.White, fontSize = 12.sp)
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun EditPostScreenPreview() {
    val navController = rememberNavController()
    // Tạo mock Uri để hiển thị trong Preview
    val mockUri = Uri.parse("https://picsum.photos/id/1018/1080/1920")
    InstaGalleryAppTheme {
        EditPostScreen(navController = navController, selectedUri = mockUri)
    }
}