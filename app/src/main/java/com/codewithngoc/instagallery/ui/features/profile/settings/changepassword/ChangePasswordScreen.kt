package com.codewithngoc.instagallery.ui.features.profile.settings.changepassword

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangePasswordScreen(navController: NavController) {
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var showCurrent by remember { mutableStateOf(false) }
    var showNew by remember { mutableStateOf(false) }
    var showConfirm by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
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
                .padding(horizontal = 24.dp)
        ) {
            Text(
                "Đổi mật khẩu",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Current password
            Text("Mật khẩu hiện tại", fontSize = 14.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = currentPassword,
                onValueChange = { currentPassword = it },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                visualTransformation = if (showCurrent) VisualTransformation.None else PasswordVisualTransformation(),
                leadingIcon = { Text("🔒") },
                trailingIcon = {
                    IconButton(onClick = { showCurrent = !showCurrent }) {
                        Text(if (showCurrent) "👁️" else "🙈", fontSize = 16.sp)
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFFFF6B35))
            )

            Spacer(modifier = Modifier.height(24.dp))

            // New password
            Text("Mật khẩu mới", fontSize = 14.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = newPassword,
                onValueChange = { newPassword = it },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                visualTransformation = if (showNew) VisualTransformation.None else PasswordVisualTransformation(),
                leadingIcon = { Text("🔒") },
                trailingIcon = {
                    IconButton(onClick = { showNew = !showNew }) {
                        Text(if (showNew) "👁️" else "🙈", fontSize = 16.sp)
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFFFF6B35))
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Confirm password
            Text("Xác nhận mật khẩu mới", fontSize = 14.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                visualTransformation = if (showConfirm) VisualTransformation.None else PasswordVisualTransformation(),
                leadingIcon = { Text("🔒") },
                trailingIcon = {
                    IconButton(onClick = { showConfirm = !showConfirm }) {
                        Text(if (showConfirm) "👁️" else "🙈", fontSize = 16.sp)
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFFFF6B35))
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Update button
            Button(
                onClick = { navController.popBackStack() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(26.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF6B35)),
                enabled = currentPassword.isNotBlank() && newPassword.isNotBlank() && confirmPassword == newPassword
            ) {
                Text("Cập nhật", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
