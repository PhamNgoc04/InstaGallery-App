package com.codewithngoc.instagallery.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.codewithngoc.instagallery.ui.features.auth.AuthScreen
import com.codewithngoc.instagallery.ui.features.auth.login.SignInScreen
import com.codewithngoc.instagallery.ui.features.auth.signup.SignUpScreen

sealed class Screen(val route: String) {
    object Auth : Screen("auth")
    object Login : Screen("login")
    object SignUp : Screen("signup")
    object Home : Screen("home")
}

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController(),
    startDestination: String = Screen.Auth.route,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(Screen.Auth.route) {
            AuthScreen(navController = navController)
        }

        composable(Screen.Login.route) {
            SignInScreen(navController = navController)
        }

        composable(Screen.SignUp.route) {
            SignUpScreen(
                navController = navController
            )
        }

        composable(Screen.Home.route) {
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                Text("Chào mừng đến với InstaGallery!", modifier = Modifier.align(Alignment.Center))

            }
        }
    }
}
