package com.codewithngoc.instagallery.ui.features.booking

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateBookingScreen(
    photographerId: Long,
    navController: NavController
) {
    var selectedDay by remember { mutableIntStateOf(16) }
    var duration by remember { mutableStateOf("2 giờ") }
    var details by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }

    val daysOfWeek = listOf("T2", "T3", "T4", "T5", "T6", "T7", "CN")
    val daysInMonth = (1..31).toList()
    val bookedDays = listOf(16, 17, 18, 19)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Đặt lịch chụp", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Quay lại")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.White,
                shadowElevation = 8.dp
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Tổng chi phí:", fontSize = 16.sp, color = Color.Gray)
                        Text(
                            "1.200.000 VNĐ",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFF6B35)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = { navController.navigate("booking_list") { popUpTo("booking_list") { inclusive = true } } },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(26.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF6B35))
                    ) {
                        Text("Đặt lịch", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        },
        containerColor = Color.White
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Photographer info card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F8F8))
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AsyncImage(
                        model = null,
                        contentDescription = "Avatar",
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape),
                        placeholder = ColorPainter(Color.LightGray),
                        error = ColorPainter(Color.LightGray)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text("Alex Photo", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("⭐", fontSize = 13.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("4.8", fontSize = 14.sp, color = Color(0xFFFF6B35))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Calendar header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { }) { Text("‹", fontSize = 24.sp) }
                Text("Tháng 3, 2026", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                IconButton(onClick = { }) { Text("›", fontSize = 24.sp) }
            }

            // Days of week header
            Row(modifier = Modifier.fillMaxWidth()) {
                daysOfWeek.forEach { day ->
                    Text(
                        day,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center,
                        fontSize = 13.sp,
                        color = Color.Gray,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Calendar grid
            val startOffset = 5 // March 2026 starts on Sunday
            val totalCells = startOffset + daysInMonth.size
            val rows = (totalCells + 6) / 7

            for (row in 0 until rows) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    for (col in 0..6) {
                        val cellIndex = row * 7 + col - startOffset
                        val day = if (cellIndex in daysInMonth.indices) cellIndex + 1 else null

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .padding(2.dp)
                                .let { mod ->
                                    if (day != null && day == selectedDay) {
                                        mod
                                            .clip(CircleShape)
                                            .background(Color(0xFFFF6B35))
                                    } else if (day != null && day in bookedDays) {
                                        mod
                                            .clip(CircleShape)
                                            .background(Color(0xFFFFE0CC))
                                    } else mod
                                }
                                .let { mod ->
                                    if (day != null) mod.clickable { selectedDay = day } else mod
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            if (day != null) {
                                Text(
                                    "$day",
                                    fontSize = 14.sp,
                                    color = when {
                                        day == selectedDay -> Color.White
                                        day < 15 -> Color.LightGray
                                        else -> Color.Black
                                    },
                                    fontWeight = if (day == selectedDay) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Duration
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Thời lượng:", fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                Box {
                    OutlinedButton(
                        onClick = { expanded = true },
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(duration)
                        Text(" ▾")
                    }
                    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        listOf("1 giờ", "2 giờ", "3 giờ", "4 giờ").forEach { opt ->
                            DropdownMenuItem(
                                text = { Text(opt) },
                                onClick = { duration = opt; expanded = false }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Details
            Text("Yêu cầu:", fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = details,
                onValueChange = { details = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                placeholder = { Text("Mô tả yêu cầu chụp ảnh...", color = Color.Gray) },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFFFF6B35))
            )

            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}
