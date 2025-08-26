package com.codewithngoc.instagallery.ui.navigation

import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.codewithngoc.instagallery.data.InstaGallerySession
import com.codewithngoc.instagallery.ui.features.auth.AuthScreen
import com.codewithngoc.instagallery.ui.features.auth.login.SignInScreen
import com.codewithngoc.instagallery.ui.features.auth.login.SignInViewModel
import com.codewithngoc.instagallery.ui.features.auth.signup.SignUpScreen
import com.codewithngoc.instagallery.ui.features.homefeed.HomeFeedScreen
import com.codewithngoc.instagallery.ui.features.homefeed.PostDetailScreen
import com.codewithngoc.instagallery.ui.features.newpost.NewPostScreen
import com.codewithngoc.instagallery.ui.features.newpost.editpost.EditPostScreen
import com.codewithngoc.instagallery.ui.features.profile.LogOutScreen
import com.codewithngoc.instagallery.ui.features.profile.ProfileScreen
import com.codewithngoc.instagallery.ui.features.profile.editprofilepost.EditPostProfileScreen
import com.codewithngoc.instagallery.ui.splash.SplashScreen

sealed class Screen(val route: String) {
    object Auth : Screen("auth")

    object Login : Screen("login")

    object SignUp : Screen("signup")

    object Logout : Screen("logout")

    object HomeFeed : Screen("homefeed")

    object PostDetail : Screen("postdetail/{postId}") {
        fun createRoute(postId: String) = "postdetail/$postId"
    }

    object NewPost : Screen("new_post")

    object Profile : Screen("profile")

    object EditPost : Screen("edit_post_screen/{encodedUri}") {
        fun createRoute(encodedUri: String) = "edit_post_screen/$encodedUri"
    }

    object Splash : Screen("splash")

}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController(),
//    startDestination: String = Screen.Auth.route,
    startDestination: String = Screen.Splash.route,
    modifier: Modifier = Modifier,
    session: InstaGallerySession = hiltViewModel<SignInViewModel>().session
) {

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(navController = navController)
        }

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

        composable(Screen.Logout.route) {
            LogOutScreen(navController = navController)
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

        composable(
            route = Screen.Profile.route
        ) {

            val currentUserId = session.getUserId()?.toIntOrNull()

            // Kiểm tra và truyền userId vào ProfileScreen
            if (currentUserId != null) {
                ProfileScreen(navController, userId = currentUserId)
            } else {
                // Xử lý trường hợp không có userId
                LaunchedEffect(Unit) {
                    // Chuyển hướng về màn hình đăng nhập
                    navController.navigate(Screen.Login.route) {
                        // Xóa các màn hình trước đó khỏi back stack
                        popUpTo(navController.graph.startDestinationId) {
                            inclusive = true
                        }
                    }
                }
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("User not logged in")
                }
            }
        }

        // Edit Post Screen
        composable(
            route = "editPost/{postId}",
            arguments = listOf(navArgument("postId") { type = NavType.StringType })
        ) { backStackEntry ->
            val postId = backStackEntry.arguments?.getString("postId") ?: ""

            EditPostProfileScreen(
                // bạn có thể fetch dữ liệu từ postId trong ViewModel
                onCancel = { navController.popBackStack() },
                onUpdate = { updatedContent ->
                    // TODO: Gọi API update bài viết bằng postId + updatedContent
                    navController.popBackStack()
                }
            )
        }
    }
}