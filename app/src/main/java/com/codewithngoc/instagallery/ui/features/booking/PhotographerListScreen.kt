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

data class PhotographerItem(
    val id: Long,
    val name: String,
    val specialties: String,
    val rating: Double,
    val reviewCount: Int,
    val hourlyRate: String,
    val location: String,
    val avatar: String? = null
)

@Composable
fun PhotographerListScreen(navController: NavController) {
    val filters = listOf("Tất cả", "Portrait", "Wedding", "Event", "Landscape")
    var selectedFilter by remember { mutableIntStateOf(0) }
    val photographers = remember {
        listOf(
            PhotographerItem(1, "Alex Photo", "Portrait • Wedding", 4.8, 24, "600K/giờ", "TP.HCM"),
            PhotographerItem(2, "Mai Studio", "Wedding • Event", 4.6, 18, "500K/giờ", "Hà Nội"),
            PhotographerItem(3, "Bob Creative", "Landscape • Portrait", 4.9, 32, "800K/giờ", "Đà Nẵng")
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 16.dp)
    ) {
        // Title
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Nhiếp ảnh gia", fontSize = 28.sp, fontWeight = FontWeight.Bold)
            IconButton(onClick = { }) {
                Text("⚙", fontSize = 22.sp)
            }
        }

        // Filter chips
        LazyRow(
            modifier = Modifier.padding(vertical = 8.dp),
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

        // Photographer list
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
        ) {
            items(photographers) { photographer ->
                PhotographerCard(
                    photographer = photographer,
                    onViewProfile = { navController.navigate("user_profile/${photographer.id}") }
                )
            }
        }
    }
}

@Composable
private fun PhotographerCard(photographer: PhotographerItem, onViewProfile: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                AsyncImage(
                    model = photographer.avatar,
                    contentDescription = "Avatar",
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop,
                    placeholder = ColorPainter(Color.LightGray),
                    error = ColorPainter(Color.LightGray)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(photographer.name, fontWeight = FontWeight.Bold, fontSize = 17.sp)
                    Text(photographer.specialties, color = Color.Gray, fontSize = 13.sp)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("⭐", fontSize = 14.sp)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("${photographer.rating}", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text(" (${photographer.reviewCount})", color = Color.Gray, fontSize = 13.sp)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("💰", fontSize = 14.sp)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(photographer.hourlyRate, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFFFF6B35))
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text("📍 ${photographer.location}", fontSize = 13.sp, color = Color.Gray)

            Spacer(modifier = Modifier.height(8.dp))

            TextButton(
                onClick = onViewProfile,
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Xem hồ sơ", color = Color(0xFFFF6B35), fontWeight = FontWeight.SemiBold)
            }
        }
    }
}
