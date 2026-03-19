package com.codewithngoc.instagallery.ui.features.booking

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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

data class BookingPreview(
    val id: Long,
    val photographerName: String,
    val date: String,
    val time: String?,
    val status: String,
    val price: String,
    val avatar: String? = null
)

@Composable
fun BookingListScreen(navController: NavController) {
    val filters = listOf("Tất cả", "Chờ xác nhận", "Đã xác nhận", "Hoàn thành")
    var selectedFilter by remember { mutableIntStateOf(0) }

    val bookings = remember {
        listOf(
            BookingPreview(1, "Alex Photo", "15/03/2026", "14:00", "PENDING", "1.200.000 VNĐ"),
            BookingPreview(2, "Mai Studio", "20/03/2026", "09:00", "CONFIRMED", "800.000 VNĐ"),
            BookingPreview(3, "Bob Photo", "01/03/2026", null, "COMPLETED", "950.000 VNĐ")
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 16.dp)
    ) {
        // Title
        Text(
            "Lịch đặt chụp",
            modifier = Modifier.padding(horizontal = 16.dp),
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Filter chips
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) {
            items(filters.size) { index ->
                FilterChip(
                    onClick = { selectedFilter = index },
                    label = { Text(filters[index], fontSize = 13.sp) },
                    selected = selectedFilter == index,
                    shape = RoundedCornerShape(20.dp),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFFFF6B35),
                        selectedLabelColor = Color.White
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Booking list
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
        ) {
            items(bookings) { booking ->
                BookingCard(booking = booking, onClick = { })
            }
        }
    }
}

@Composable
private fun BookingCard(booking: BookingPreview, onClick: () -> Unit) {
    val statusColor = when (booking.status) {
        "PENDING" -> Color(0xFFFF9800)
        "CONFIRMED" -> Color(0xFF4CAF50)
        "COMPLETED" -> Color(0xFF2196F3)
        "CANCELLED" -> Color.Red
        else -> Color.Gray
    }
    val statusText = when (booking.status) {
        "PENDING" -> "Chờ xác nhận"
        "CONFIRMED" -> "Đã xác nhận"
        "COMPLETED" -> "Hoàn thành"
        "CANCELLED" -> "Đã hủy"
        else -> booking.status
    }

    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                AsyncImage(
                    model = booking.avatar,
                    contentDescription = "Avatar",
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop,
                    placeholder = ColorPainter(Color.LightGray),
                    error = ColorPainter(Color.LightGray)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(booking.photographerName, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("📅", fontSize = 13.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            "${booking.date}${if (booking.time != null) " • ${booking.time}" else ""}",
                            fontSize = 13.sp,
                            color = Color.Gray
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Status badge
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = statusColor.copy(alpha = 0.15f)
                ) {
                    Text(
                        statusText,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        fontSize = 12.sp,
                        color = statusColor,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                // Price
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("💰", fontSize = 13.sp)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        booking.price,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = Color(0xFFFF6B35)
                    )
                }
            }
        }
    }
}
