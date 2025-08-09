package com.codewithngoc.instagallery.ui.features.auth

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.codewithngoc.instagallery.R
import com.codewithngoc.instagallery.ui.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(navController: NavController) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {},
                navigationIcon = {},
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        containerColor = Color.White
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(top = 80.dp, start = 24.dp, end = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.weight(1f))

            // Logo và tên ứng dụng
            Text(
                text = "InstaGallery",
                fontSize = 36.sp,
                fontWeight = FontWeight.ExtraBold,
                color = colorResource(R.color.orange_button_login),
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Text(
                text = "Chào mừng bạn đến với InstaGallery,\nnơi bạn có thể lưu giữ những khoảnh khắc đẹp nhất!",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray),
                modifier = Modifier.padding(vertical = 16.dp)
            )

            // Các nút Đăng ký & Đăng nhập
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 38.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                Button(
                    onClick = { navController.navigate(Screen.SignUp.route) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorResource(R.color.orange_button_login),
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(16.dp),
                    contentPadding = PaddingValues(vertical = 14.dp)
                ) {
                    Text(
                        text = "Đăng ký",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                OutlinedButton(
                    onClick = { navController.navigate(Screen.Login.route) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, Color(0xFFDBDBDB)), // màu xám nhạt như Instagram
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = Color.White,
                        contentColor = colorResource(R.color.orange_button_login)
                    ),
                    contentPadding = PaddingValues(vertical = 14.dp)
                ) {
                    Text(
                        text = "Đăng nhập",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            SocialSeparator()

            Spacer(modifier = Modifier.height(28.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                SocialIcon(
                    iconRes = R.drawable.ic_google,
                    description = "Google",
                    onClick = { /* TODO */ }
                )
                SocialIcon(
                    iconRes = R.drawable.ic_fb,
                    description = "Facebook",
                    onClick = { /* TODO */ }
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun SocialSeparator() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Divider(
            modifier = Modifier.weight(1f),
            color = Color(0xFFDDDDDD),
            thickness = 1.dp
        )
        Text(
            text = "  Hoặc  ",
            style = MaterialTheme.typography.labelLarge.copy(color = Color.Gray),
            modifier = Modifier.padding(horizontal = 8.dp)
        )
        Divider(
            modifier = Modifier.weight(1f),
            color = Color(0xFFDDDDDD),
            thickness = 1.dp
        )
    }
}

@Composable
fun SocialIcon(
    iconRes: Int,
    description: String,
    onClick: () -> Unit
) {
    IconButton(
        onClick = onClick,
        modifier = Modifier
            .size(56.dp)
            .clip(CircleShape),
        colors = IconButtonDefaults.iconButtonColors(
            containerColor = Color(0xFFF5F5F5)
        )
    ) {
        Image(
            painter = painterResource(id = iconRes),
            contentDescription = description,
            modifier = Modifier.size(28.dp)
        )
    }
}


@Preview(showBackground = true, widthDp = 360, heightDp = 700)
@Composable
fun AuthScreenPreview() {
    MaterialTheme {
        AuthScreen(rememberNavController())
    }
}