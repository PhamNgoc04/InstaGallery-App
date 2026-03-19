package com.codewithngoc.instagallery.ui.features.auth.forgotpassword

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPasswordScreen(navController: NavController) {
    var email by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var showSuccess by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = Color.White
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Back button
            Row(modifier = Modifier.fillMaxWidth()) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Quay lại", tint = Color.DarkGray)
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Lock icon
            Surface(
                modifier = Modifier.size(80.dp),
                shape = CircleShape,
                color = Color(0xFFFFE0CC)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text("🔒", fontSize = 36.sp)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                "Quên mật khẩu?",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                "Nhập email để nhận link đặt lại mật khẩu",
                fontSize = 14.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            if (showSuccess) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        "✅ Link đặt lại mật khẩu đã được gửi đến email của bạn!",
                        modifier = Modifier.padding(16.dp),
                        color = Color(0xFF2E7D32),
                        fontSize = 14.sp
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Email field
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                leadingIcon = { Text("✉️") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFFF6B35),
                    cursorColor = Color(0xFFFF6B35)
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Submit button
            Button(
                onClick = {
                    isLoading = true
                    showSuccess = true
                    isLoading = false
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(26.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF6B35)),
                enabled = email.isNotBlank() && !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text("Gửi", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Back to login
            TextButton(onClick = { navController.popBackStack() }) {
                Text(
                    "Quay lại Đăng nhập",
                    color = Color(0xFFFF6B35),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
