package com.codewithngoc.instagallery.ui.features.profile.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.codewithngoc.instagallery.ui.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Settings",
                        modifier = Modifier.fillMaxWidth(),
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        containerColor = Color(0xFFF2F2F7) // Màu nền xám nhạt giống iOS
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            // Phần Account
            SettingsSectionHeader("Account")
            SettingsItem(title = "Edit Profile", onClick = { /* TODO: Handle click */ })
            SettingsItem(title = "Change Password", onClick = { /* TODO: Handle click */ })
            SettingsItem(title = "Language", isLastItem = true, onClick = { /* TODO: Handle click */ })

            Spacer(modifier = Modifier.height(24.dp))

            // Phần Other
            SettingsSectionHeader("Other")
            SettingsItem(title = "Privacy Policy", onClick = { /* TODO: Handle click */ })
            SettingsItem(title = "Contact Us", onClick = { /* TODO: Handle click */ })
            SettingsItem(title = "About App", onClick = { /* TODO: Handle click */ })
            SettingsItem(title = "Logout", isLastItem = true, onClick = {
                navController.navigate(Screen.Logout.route) {
                    launchSingleTop = true
                }
            })
        }
    }
}

@Composable
fun SettingsSectionHeader(title: String) {
    Text(
        text = title,
        fontWeight = FontWeight.Bold,
        color = Color.Black,
        modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 8.dp)
    )
}

@Composable
fun SettingsItem(title: String, isLastItem: Boolean = false, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(start = 16.dp) // Chỉ padding lề trái cho Column
            .let { if (!isLastItem) it.padding(end = 16.dp) else it } // Lề phải nếu không phải item cuối
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 14.dp)
                // Nếu là item cuối thì không cần padding lề phải cho Row
                .let { if (isLastItem) it.padding(end = 16.dp) else it },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = title,
                fontSize = 17.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = Color.Gray,
                modifier = Modifier.size(16.dp)
            )
        }
        if (!isLastItem) {
            HorizontalDivider(
                color = Color.LightGray.copy(alpha = 0.5f),
                thickness = 0.5.dp,
                // Không cần modifier vì đã padding cho Column cha
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SettingsScreenPreview() {
    MaterialTheme { // Thay thế bằng Theme của ứng dụng bạn
        SettingsScreen(
            navController = NavController(LocalContext.current)
        )
    }
}
