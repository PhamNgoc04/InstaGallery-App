package com.codewithngoc.instagallery.ui.splash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.codewithngoc.instagallery.data.InstaGallerySession
import com.codewithngoc.instagallery.ui.features.auth.login.SignInViewModel
import com.codewithngoc.instagallery.ui.navigation.Screen
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    navController: NavHostController,
    session: InstaGallerySession = hiltViewModel<SignInViewModel>().session
) {
    LaunchedEffect(Unit) {
        // ✅ FIX #4: delay TRƯỚC khi đọc token → tránh race condition khi token đang được refresh
        delay(1500)
        val token = session.getToken()

        if (token.isNullOrEmpty()) {
            navController.navigate(Screen.Auth.route) {
                popUpTo(Screen.Splash.route) { inclusive = true }
            }
        } else {
            // ✅ FIX #3: Navigate vào "main_graph" thay vì trực tiếp "homefeed"
            // → đảm bảo getBackStackEntry("main_graph") trong HomeInsBottomBar không throw exception
            navController.navigate("main_graph") {
                popUpTo(Screen.Splash.route) { inclusive = true }
            }
        }
    }

    // Splash UI đơn giản — màu nền thương hiệu
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        // Hiển thị logo nếu có drawable ic_register_logo, hoặc có thể dùng Text
        // Image(painter = painterResource(R.drawable.ic_register_logo), contentDescription = "Logo")
    }
}

