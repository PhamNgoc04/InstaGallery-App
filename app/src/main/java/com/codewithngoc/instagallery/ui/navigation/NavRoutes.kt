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
import androidx.navigation.navigation
import com.codewithngoc.instagallery.data.InstaGallerySession
import com.codewithngoc.instagallery.ui.features.auth.AuthScreen
import com.codewithngoc.instagallery.ui.features.auth.forgotpassword.ForgotPasswordScreen
import com.codewithngoc.instagallery.ui.features.auth.login.SignInScreen
import com.codewithngoc.instagallery.ui.features.auth.login.SignInViewModel
import com.codewithngoc.instagallery.ui.features.auth.signup.SignUpScreen
import com.codewithngoc.instagallery.ui.features.booking.BookingListScreen
import com.codewithngoc.instagallery.ui.features.booking.CreateBookingScreen
import com.codewithngoc.instagallery.ui.features.booking.PhotographerListScreen
import com.codewithngoc.instagallery.ui.features.booking.RatingScreen
import com.codewithngoc.instagallery.ui.features.explore.ExploreScreen
import com.codewithngoc.instagallery.ui.features.followers.FollowersScreen
import com.codewithngoc.instagallery.ui.features.homefeed.HomeFeedScreen
import com.codewithngoc.instagallery.ui.features.homefeed.detailspost.PostDetailScreen
import com.codewithngoc.instagallery.ui.features.messages.MessagesScreen
import com.codewithngoc.instagallery.ui.features.messages.chatdetail.ChatDetailScreen
import com.codewithngoc.instagallery.ui.features.newpost.NewPostScreen
import com.codewithngoc.instagallery.ui.features.newpost.editpost.EditPostScreen
import com.codewithngoc.instagallery.ui.features.news.NewsScreen
import com.codewithngoc.instagallery.ui.features.notifications.NotificationScreen
import com.codewithngoc.instagallery.ui.features.profile.LogOutScreen
import com.codewithngoc.instagallery.ui.features.profile.ProfileScreen
import com.codewithngoc.instagallery.ui.features.profile.editprofilepost.EditPostProfileScreen
import com.codewithngoc.instagallery.ui.features.profile.settings.SettingsScreen
import com.codewithngoc.instagallery.ui.features.profile.settings.changepassword.ChangePasswordScreen
import com.codewithngoc.instagallery.ui.features.profile.settings.changelanguage.ChangeLanguageScreen
import com.codewithngoc.instagallery.ui.features.profile.settings.editprofile.EditProfileScreen
import com.codewithngoc.instagallery.ui.features.search.SearchScreen
import com.codewithngoc.instagallery.ui.features.userprofile.UserProfileScreen
import com.codewithngoc.instagallery.ui.splash.SplashScreen

sealed class Screen(val route: String) {
    object Auth : Screen("auth")
    object Login : Screen("login")
    object SignUp : Screen("signup")
    object Logout : Screen("logout")
    object ForgotPassword : Screen("forgot_password")

    //================
    object HomeFeed : Screen("homefeed")
    object Explore : Screen("explore")
    object Search : Screen("search")
    object News : Screen("news")
    object NewPost : Screen("new_post")
    object Profile : Screen("profile")
    object Notifications : Screen("notifications")

    //==========
    object PostDetail : Screen("postdetail/{postId}") {
        fun createRoute(postId: String) = "postdetail/$postId"
    }

    object EditPost : Screen("edit_post_screen/{encodedUri}") {
        fun createRoute(encodedUri: String) = "edit_post_screen/$encodedUri"
    }

    object Splash : Screen("splash")

    object EditPostProfile : Screen("editPost/{postId}") {
        fun createRoute(postId: String) = "editPost/$postId"
    }

    object Settings : Screen("settings")
    object ChangePassword : Screen("change_password")
    object ChangeLanguage : Screen("change_language")
    object EditProfile : Screen("edit_profile")

    // Social
    object UserProfile : Screen("user_profile/{userId}") {
        fun createRoute(userId: Long) = "user_profile/$userId"
    }

    object Followers : Screen("followers/{userId}") {
        fun createRoute(userId: Long) = "followers/$userId"
    }

    // Messaging
    object Messages : Screen("messages")
    object ChatDetail : Screen("chat_detail/{conversationId}") {
        fun createRoute(conversationId: Long) = "chat_detail/$conversationId"
    }

    // Booking
    object PhotographerList : Screen("photographer_list")
    object CreateBooking : Screen("create_booking/{photographerId}") {
        fun createRoute(photographerId: Long) = "create_booking/$photographerId"
    }
    object BookingList : Screen("booking_list")
    object Rating : Screen("rating/{bookingId}") {
        fun createRoute(bookingId: Long) = "rating/$bookingId"
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController(),
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
            SignUpScreen(navController = navController)
        }

        composable(Screen.Logout.route) {
            LogOutScreen(navController = navController)
        }

        composable(Screen.ForgotPassword.route) {
            ForgotPasswordScreen(navController = navController)
        }

        navigation(
            route = "main_graph",
            startDestination = Screen.HomeFeed.route
        ) {
            composable(Screen.HomeFeed.route) {
                HomeFeedScreen(navController = navController)
            }

            composable(Screen.Explore.route) {
                ExploreScreen(navController = navController)
            }

            composable(Screen.Search.route) {
                SearchScreen(navController = navController)
            }

            composable(Screen.News.route) {
                NewsScreen(navController = navController)
            }

            composable(Screen.Notifications.route) {
                NotificationScreen(navController = navController)
            }

            composable(
                route = Screen.PostDetail.route,
                arguments = listOf(navArgument("postId") { type = NavType.StringType })
            ) { backStackEntry ->
                PostDetailScreen(navController = navController)
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

            composable(route = Screen.Profile.route) {
                val currentUserId = session.getUserId()
                if (currentUserId != -1L) {
                    ProfileScreen(navController, userId = currentUserId)
                } else {
                    LaunchedEffect(Unit) {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(navController.graph.startDestinationId) { inclusive = true }
                        }
                    }
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("User not logged in")
                    }
                }
            }

            // Edit Post from Profile
            composable(
                route = Screen.EditPostProfile.route,
                arguments = listOf(navArgument("postId") { type = NavType.StringType })
            ) { backStackEntry ->
                EditPostProfileScreen(
                    onCancel = { },
                    onUpdate = { navController.popBackStack() },
                    onBack = { navController.popBackStack() }
                )
            }

            // Settings
            composable(Screen.Settings.route) {
                SettingsScreen(navController = navController)
            }

            composable(Screen.ChangePassword.route) {
                ChangePasswordScreen(navController = navController)
            }

            composable(Screen.ChangeLanguage.route) {
                ChangeLanguageScreen(navController = navController)
            }

            composable(Screen.EditProfile.route) {
                EditProfileScreen(navController = navController)
            }

            // User Profile (other users)
            composable(
                route = Screen.UserProfile.route,
                arguments = listOf(navArgument("userId") { type = NavType.LongType })
            ) { backStackEntry ->
                val userId = backStackEntry.arguments?.getLong("userId") ?: 0L
                UserProfileScreen(userId = userId, navController = navController)
            }

            // Followers
            composable(
                route = Screen.Followers.route,
                arguments = listOf(navArgument("userId") { type = NavType.LongType })
            ) { backStackEntry ->
                val userId = backStackEntry.arguments?.getLong("userId") ?: 0L
                FollowersScreen(userId = userId, navController = navController)
            }

            // Messaging
            composable(Screen.Messages.route) {
                MessagesScreen(navController = navController)
            }

            composable(
                route = Screen.ChatDetail.route,
                arguments = listOf(navArgument("conversationId") { type = NavType.LongType })
            ) { backStackEntry ->
                val conversationId = backStackEntry.arguments?.getLong("conversationId") ?: 0L
                ChatDetailScreen(conversationId = conversationId, navController = navController)
            }

            // Booking
            composable(Screen.PhotographerList.route) {
                PhotographerListScreen(navController = navController)
            }

            composable(
                route = Screen.CreateBooking.route,
                arguments = listOf(navArgument("photographerId") { type = NavType.LongType })
            ) { backStackEntry ->
                val photographerId = backStackEntry.arguments?.getLong("photographerId") ?: 0L
                CreateBookingScreen(photographerId = photographerId, navController = navController)
            }

            composable(Screen.BookingList.route) {
                BookingListScreen(navController = navController)
            }

            composable(
                route = Screen.Rating.route,
                arguments = listOf(navArgument("bookingId") { type = NavType.LongType })
            ) { backStackEntry ->
                val bookingId = backStackEntry.arguments?.getLong("bookingId") ?: 0L
                RatingScreen(bookingId = bookingId, navController = navController)
            }
        }
    }
}