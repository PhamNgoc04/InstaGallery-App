package com.codewithngoc.instagallery.ui.features.explore

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun ExploreScreen(navController: NavController) {
    val tags = listOf("🔥 Trending", "📸 Portrait", "💒 Wedding", "🌄 Landscape", "🏙️ Street")
    var selectedTag by remember { mutableIntStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Title
        Text(
            "Khám phá",
            modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 8.dp),
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )

        // Search bar (clickable to open SearchScreen)
        OutlinedTextField(
            value = "",
            onValueChange = { },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .clickable { navController.navigate("search") },
            placeholder = { Text("Tìm kiếm ảnh, người, tag...", color = Color.Gray) },
            shape = RoundedCornerShape(12.dp),
            singleLine = true,
            readOnly = true,
            enabled = false,
            leadingIcon = { Text("🔍", fontSize = 16.sp) },
            colors = OutlinedTextFieldDefaults.colors(
                disabledBorderColor = Color.LightGray,
                disabledTextColor = Color.Gray,
                disabledLeadingIconColor = Color.Gray
            )
        )

        // Tag chips
        LazyRow(
            modifier = Modifier.padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) {
            items(tags.size) { index ->
                FilterChip(
                    onClick = { selectedTag = index },
                    label = { Text(tags[index], fontSize = 13.sp) },
                    selected = selectedTag == index,
                    shape = RoundedCornerShape(20.dp),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFFFF6B35),
                        selectedLabelColor = Color.White
                    )
                )
            }
        }

        // Photo grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(2.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            items(15) { index ->
                Box(
                    modifier = Modifier
                        .aspectRatio(1f)
                        .background(
                            listOf(
                                Color(0xFFE8D5C4),
                                Color(0xFFC9D6DF),
                                Color(0xFFD4CFCF),
                                Color(0xFFE8DFD5)
                            )[index % 4]
                        )
                        .clickable { },
                    contentAlignment = Alignment.Center
                ) {
                    Text("📷", fontSize = 20.sp)
                }
            }
        }
    }
}
