package com.codewithngoc.instagallery.ui.features.booking.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingDetailScreen(
    bookingId: Long,
    navController: NavController,
    viewModel: BookingDetailViewModel = hiltViewModel()
) {
    val booking by viewModel.booking.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Chi tiết Booking #$bookingId", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Quay lại")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color(0xFFF5F5F5)
    ) { padding ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFFFF6B35))
            }
        } else if (error != null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(error ?: "Có lỗi xảy ra", color = Color.Red)
            }
        } else if (booking != null) {
            val b = booking!!
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                // Hóa đơn Card
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Trạng thái", color = Color.Gray, style = MaterialTheme.typography.labelMedium)
                        Text(b.status, fontWeight = FontWeight.Bold, color = Color(0xFFFF6B35), style = MaterialTheme.typography.titleMedium)
                        
                        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                        DetailRow("Nhiếp ảnh gia ID", "${b.photographerId}")
                        DetailRow("Khách hàng ID", "${b.clientId}")
                        DetailRow("Tạo lúc", b.createdAt ?: "")
                        
                        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                        Text("Ghi chú yêu cầu", color = Color.Gray, style = MaterialTheme.typography.labelMedium)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(b.details ?: "Không có ghi chú", style = MaterialTheme.typography.bodyMedium)
                        
                        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Tổng tiền", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                            Text("${b.price ?: 0.0} VND", fontWeight = FontWeight.Bold, color = Color(0xFFFF6B35), style = MaterialTheme.typography.titleLarge)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Action Buttons
                if (b.status == "PENDING") {
                    Button(
                        onClick = { viewModel.updateStatus("CONFIRMED") },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Xác nhận lịch chụp", fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedButton(
                        onClick = { viewModel.updateStatus("CANCELLED") },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Hủy lịch", fontWeight = FontWeight.Bold)
                    }
                } else if (b.status == "COMPLETED") {
                    Button(
                        onClick = { navController.navigate("rating/${b.photographerId}") },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFC107)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Đánh giá nhiếp ảnh gia", fontWeight = FontWeight.Bold, color = Color.Black)
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = Color.Gray, style = MaterialTheme.typography.bodyMedium)
        Text(value, fontWeight = FontWeight.Medium, style = MaterialTheme.typography.bodyMedium)
    }
}
