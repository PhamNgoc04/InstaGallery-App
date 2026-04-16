package com.codewithngoc.instagallery.ui.features.profile.portfolio

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun PortfolioScreen(
    navController: NavController,
    viewModel: PortfolioViewModel = hiltViewModel()
) {
    val portfolio by viewModel.portfolio.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Portfolio", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold) },
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
                Text(error ?: "Có lỗi xảy ra", color = Color.Red)
            }
        } else if (portfolio != null) {
            val p = portfolio!!
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                // Header Profiler
                Column(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    AsyncImage(
                        model = null, // Backend ko trả avatar ở đây? Phụ thuộc logic, thường lấy từ user endpoint
                        contentDescription = "Avatar",
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop,
                        placeholder = ColorPainter(Color.LightGray)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(p.description ?: "Chưa có giới thiệu", style = MaterialTheme.typography.bodyLarge, color = Color.DarkGray)
                    
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("${p.hourlyRate ?: 0} ${p.currency ?: "VND"}", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleLarge)
                            Text("Giá/giờ", color = Color.Gray, style = MaterialTheme.typography.labelMedium)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(p.serviceArea ?: "Toàn quốc", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                            Text("Khu vực", color = Color.Gray, style = MaterialTheme.typography.labelMedium)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { navController.navigate("create_booking/${p.userId}") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF6B35)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Đặt lịch ngay", fontWeight = FontWeight.Bold)
                    }
                }

                HorizontalDivider()

                // Tags
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Chuyên môn", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        p.specialties?.forEach { tag ->
                            Box(
                                modifier = Modifier
                                    .background(Color(0xFFFFF0EB), RoundedCornerShape(16.dp))
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text("#$tag", color = Color(0xFFFF6B35), style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }
                }

                HorizontalDivider()

                // Portfolio Photos (Mock layout cho lưới ảnh đẹp nhất)
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Bộ sưu tập nổi bật", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Note: API PortfolioResponse có thê m trường ảnh portfolio ko? Nếu ko, phải xài Endpoint album/posts.
                    Box(modifier = Modifier.fillMaxWidth().aspectRatio(16f/9f).background(Color.LightGray, RoundedCornerShape(8.dp)), contentAlignment = Alignment.Center) {
                        Text("Chưa có ảnh trong bộ sưu tập", color = Color.Gray)
                    }
                }
            }
        }
    }
}
