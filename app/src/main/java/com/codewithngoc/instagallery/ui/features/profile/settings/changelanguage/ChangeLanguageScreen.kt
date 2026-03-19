package com.codewithngoc.instagallery.ui.features.profile.settings.changelanguage

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.navigation.NavController

data class LanguageItem(val flag: String, val name: String, val code: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangeLanguageScreen(navController: NavController) {
    val languages = listOf(
        LanguageItem("🇻🇳", "Tiếng Việt", "vi"),
        LanguageItem("🇬🇧", "English", "en"),
        LanguageItem("🇯🇵", "Nhật Bản", "ja"),
        LanguageItem("🇮🇳", "Hindi", "hi"),
        LanguageItem("🇸🇦", "Arabic", "ar"),
        LanguageItem("🇵🇹", "Portuguese", "pt"),
        LanguageItem("🇷🇺", "Russian", "ru"),
        LanguageItem("🇰🇷", "Korean", "ko")
    )
    var selected by remember { mutableStateOf("vi") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ngôn ngữ", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Quay lại")
                    }
                },
                actions = {
                    TextButton(onClick = { navController.popBackStack() }) {
                        Text("Hủy", color = Color.DarkGray)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color.White
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(languages) { lang ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selected = lang.code }
                            .padding(horizontal = 24.dp, vertical = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(lang.flag, fontSize = 28.sp)
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(lang.name, fontSize = 17.sp, color = Color.Black)
                        }
                        RadioButton(
                            selected = selected == lang.code,
                            onClick = { selected = lang.code },
                            colors = RadioButtonDefaults.colors(selectedColor = Color(0xFFFF6B35))
                        )
                    }
                    if (lang != languages.last()) {
                        HorizontalDivider(color = Color.LightGray.copy(alpha = 0.3f))
                    }
                }
            }

            // Update button
            Button(
                onClick = { navController.popBackStack() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
                    .height(52.dp),
                shape = RoundedCornerShape(26.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF6B35))
            ) {
                Text("Cập nhật", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
