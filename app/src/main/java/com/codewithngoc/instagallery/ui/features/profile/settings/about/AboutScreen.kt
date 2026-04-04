package com.codewithngoc.instagallery.ui.features.profile.settings.about

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.codewithngoc.instagallery.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(navController: NavController) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Về ứng dụng", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Trở về")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        containerColor = MaterialTheme.colorScheme.surface
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(48.dp))
            
            Box(modifier = Modifier.size(100.dp).clip(CircleShape).background(Color(0xFFFE8C00)), contentAlignment = Alignment.Center) {
                Text("App", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 24.sp)
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "InstaGallery",
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp
            )
            Text(
                "Phiên bản 1.0.0",
                fontSize = 14.sp,
                color = Color.Gray
            )
            
            Spacer(modifier = Modifier.height(48.dp))
            Text(
                "InstaGallery là nền tảng chia sẻ những bức ảnh nghệ thuật, thư viện ảnh cá nhân và cung cấp dịch vụ đặt lịch nhiếp ảnh gia chuyên nghiệp. Nơi lưu giữ những khoảnh khắc đẹp nhất của bạn.",
                fontSize = 15.sp,
                lineHeight = 24.sp,
                color = Color.DarkGray,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            
            Spacer(modifier = Modifier.weight(1f))
            Text(
                "© 2026 CodeWithNgoc. All rights reserved.",
                fontSize = 12.sp,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 32.dp)
            )
        }
    }
}
