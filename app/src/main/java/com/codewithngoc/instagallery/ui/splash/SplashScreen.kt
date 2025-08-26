package com.codewithngoc.instagallery.ui.splash

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.codewithngoc.instagallery.data.InstaGallerySession
import com.codewithngoc.instagallery.ui.features.auth.login.SignInViewModel
import com.codewithngoc.instagallery.ui.navigation.Screen
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest

@Composable
fun SplashScreen(
    navController: NavHostController,
    session: InstaGallerySession = hiltViewModel<SignInViewModel>().session
) {
    LaunchedEffect(Unit) {
        val token = session.getToken()
        delay(1500) // cho splash hiển thị một chút

        if (token.isNullOrEmpty()) {
            navController.navigate(Screen.Auth.route) {
                popUpTo(Screen.Splash.route) { inclusive = true }
            }
        } else {
            navController.navigate(Screen.HomeFeed.route) {
                popUpTo(Screen.Splash.route) { inclusive = true }
            }
        }
    }

//    Box(
//        modifier = Modifier.fillMaxSize(),
//        contentAlignment = Alignment.Center
//    ) {
//        Text("InstaGallery")
//    }
}
