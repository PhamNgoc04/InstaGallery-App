package com.codewithngoc.instagallery.ui.navigation

import android.net.Uri
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.codewithngoc.instagallery.ui.features.auth.AuthScreen
import com.codewithngoc.instagallery.ui.features.auth.login.SignInScreen
import com.codewithngoc.instagallery.ui.features.auth.signup.SignUpScreen
import com.codewithngoc.instagallery.ui.features.homefeed.HomeFeedScreen
import com.codewithngoc.instagallery.ui.features.homefeed.PostDetailScreen
import com.codewithngoc.instagallery.ui.features.newpost.NewPostScreen
import com.codewithngoc.instagallery.ui.features.newpost.editpost.EditPostScreen

sealed class Screen(val route: String) {
    object Auth : Screen("auth")
    object Login : Screen("login")
    object SignUp : Screen("signup")
    object HomeFeed : Screen("homefeed")

    object PostDetail : Screen("postdetail/{postId}") {
        fun createRoute(postId: String) = "postdetail/$postId"
    }

    object NewPost : Screen("new_post")
    object Profile : Screen("profile")

    object EditPost : Screen("edit_post_screen/{encodedUri}") {
        fun createRoute(encodedUri: String) = "edit_post_screen/$encodedUri"
    }

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

        composable(Screen.HomeFeed.route) {
            HomeFeedScreen(
                navController = navController
            )
        }

        composable(
            route = Screen.PostDetail.route,
            arguments = listOf(navArgument("postId") { type = NavType.StringType })
        ) { backStackEntry ->
            val postId = backStackEntry.arguments?.getString("postId") ?: ""
            PostDetailScreen(postId = postId, navController = navController)
        }

        composable(Screen.NewPost.route) {
            NewPostScreen(navController)
        }

        composable(
            route = Screen.EditPost.route,
            arguments = listOf(navArgument("encodedUri") { type = NavType.StringType })
        ) { backStackEntry ->
            val encodedUri = backStackEntry.arguments?.getString("encodedUri") ?: ""
            val uriString = Uri.decode(encodedUri)
            val uri = Uri.parse(uriString)
            EditPostScreen(selectedUri = uri, navController = navController)
        }

    }
}