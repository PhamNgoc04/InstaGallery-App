package com.codewithngoc.instagallery.ui.features.profile.settings.editprofile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.navigation.NavController
import coil.compose.AsyncImage
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    navController: NavController,
    viewModel: EditProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    val username by viewModel.username.collectAsState()
    val fullName by viewModel.fullName.collectAsState()
    val bio by viewModel.bio.collectAsState()
    val email by viewModel.email.collectAsState()
    val phone by viewModel.phone.collectAsState()
    val birthDate by viewModel.birthDate.collectAsState()
    val location by viewModel.location.collectAsState()
    val avatarUrl by viewModel.avatarUrl.collectAsState()

    LaunchedEffect(uiState) {
        if (uiState is EditProfileState.UpdateSuccess) {
            navController.popBackStack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Chỉnh sửa hồ sơ", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Quay lại")
                    }
                },
                actions = {
                    TextButton(onClick = { viewModel.updateProfile() }) {
                        Text("Lưu", color = Color(0xFFFF6B35), fontWeight = FontWeight.Bold)
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
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Avatar with camera icon
            Box(contentAlignment = Alignment.BottomEnd) {
                AsyncImage(
                    model = avatarUrl,
                    contentDescription = "Avatar",
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop,
                    placeholder = ColorPainter(Color.LightGray),
                    error = ColorPainter(Color.LightGray)
                )
                Surface(
                    modifier = Modifier.size(32.dp),
                    shape = CircleShape,
                    color = Color(0xFFFF6B35)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text("📷", fontSize = 14.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Fields
            ProfileField("Username:", username) { viewModel.username.value = it }
            ProfileField("Tên:", fullName) { viewModel.fullName.value = it }
            ProfileField("Chức danh:", bio) { viewModel.bio.value = it }
            ProfileField("Email:", email) { viewModel.email.value = it }
            ProfileField("Số điện thoại:", phone) { viewModel.phone.value = it }
            ProfileField("Ngày sinh:", birthDate) { viewModel.birthDate.value = it }
            ProfileField("Vị trí:", location) { viewModel.location.value = it }

            Spacer(modifier = Modifier.height(24.dp))

            // Update button
            Button(
                onClick = { viewModel.updateProfile() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(26.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF6B35)),
                enabled = uiState !is EditProfileState.Loading
            ) {
                if (uiState is EditProfileState.Loading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                } else {
                    Text("Cập nhật", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            
            if (uiState is EditProfileState.Error) {
                Text(
                    text = (uiState as EditProfileState.Error).message,
                    color = Color.Red,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}

@Composable
private fun ProfileField(label: String, value: String, onValueChange: (String) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            label,
            modifier = Modifier.width(100.dp),
            fontSize = 14.sp,
            color = Color.Gray
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = Color.Transparent,
                focusedBorderColor = Color(0xFFFF6B35)
            )
        )
    }
    HorizontalDivider(color = Color.LightGray.copy(alpha = 0.3f))
}
