package com.codewithngoc.instagallery.ui.features.profile

import android.os.Build
import androidx.annotation.DrawableRes
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.codewithngoc.instagallery.R
import com.codewithngoc.instagallery.data.model.PostResponse
import com.codewithngoc.instagallery.data.model.ProfileUiState
import com.codewithngoc.instagallery.data.model.User
import com.codewithngoc.instagallery.data.model.UserState
import com.codewithngoc.instagallery.ui.features.homefeed.HomeInsBottomBar
import com.codewithngoc.instagallery.ui.features.homefeed.PostActions
import com.codewithngoc.instagallery.ui.features.homefeed.PostHeader
import com.codewithngoc.instagallery.ui.features.homefeed.PostItem
import com.codewithngoc.instagallery.ui.features.homefeed.commentsheet.CommentBottomSheet
import com.codewithngoc.instagallery.ui.features.homefeed.commentsheet.CommentViewModel
import com.codewithngoc.instagallery.ui.features.homefeed.postmorebottomsheet.PostMoreBottomSheet
import com.codewithngoc.instagallery.ui.navigation.Screen

// --- Composable chính ---
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ProfileScreen(
    navController: NavController,
    userId: Int,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    // State cho BottomSheet
    var showCommentBottomSheet by remember { mutableStateOf(false) }
    var selectedPostIdForComment by remember { mutableStateOf<Int?>(null) }
    var showMoreBottomSheet by remember { mutableStateOf(false) }
    var selectedPostIdForMore by remember { mutableStateOf<Int?>(null) }

    val commentViewModel: CommentViewModel = hiltViewModel()

    LaunchedEffect(userId) {
        viewModel.loadProfile(userId)
    }

    LaunchedEffect(Unit) {
        commentViewModel.newCommentEvent.collect { (postId, comment) ->
            val currentState = state
            if (currentState is ProfileUiState.Success) {
                val updatedPosts = currentState.posts.map { post ->
                    if (post.postId == postId) post.copy(commentCount = post.commentCount + 1)
                    else post
                }
                viewModel.updatePosts(updatedPosts)
            }
        }
    }


    Scaffold(
        topBar = { ProfileTopBar(navController = navController) },
        bottomBar = { HomeInsBottomBar(navController = navController) },
        containerColor = Color.White
    ) { paddingValues ->
        when (state) {
            is ProfileUiState.Loading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            is ProfileUiState.Error -> {
                val message = (state as ProfileUiState.Error).message
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = "❌ $message")
                }
            }

            is ProfileUiState.Success -> {
                val success = state as ProfileUiState.Success
                LazyColumn(modifier = Modifier.padding(paddingValues)) {
                    // 1. Header Profile
                    item {
                        ProfileHeader(
                            user = success.user,
                            stats = success.stats
                        )
                    }
                    // 2. Story Highlights (dummy data vì backend chưa có)
                    item {
                        StoryHighlights(
                            stories = listOf(
                                StoryHighlight(R.drawable.ic_register_logo, "News"),
                                StoryHighlight(R.drawable.ic_register_logo, "Work"),
                                StoryHighlight(R.drawable.ic_register_logo, "Travel"),
                                StoryHighlight(R.drawable.ic_register_logo, "Food")
                            ),
                            modifier = Modifier.padding(top = 16.dp)
                        )
                    }
                    // 3. Divider
                    item {
                        HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
                    }
                    // 4. Danh sách bài đăng
                    items(items = success.posts, key = { it.postId }) { post ->
                        PostItem (
                            post = post,
                            onPostClick = { postId ->
                                navController.navigate(Screen.PostDetail.createRoute(postId.toString()))
                            },
                            onMoreClick = { postId ->
                                selectedPostIdForMore = postId
                                showMoreBottomSheet = true
                            },
                            onProfileClick = { userId ->
                                navController.navigate(Screen.Profile.route)
                            },
                            onLikeClick = { postId ->
                                viewModel.onLikePost(postId)
                            },
                            onCommentClick = { postId ->
                                selectedPostIdForComment = postId
                                showCommentBottomSheet = true
                            }
                        )
                        HorizontalDivider()
                    }

                }
            }
        }
    }
    // Comment BottomSheet
    if (showCommentBottomSheet && selectedPostIdForComment != null) {
        CommentBottomSheet(
            postId = selectedPostIdForComment!!,
            onDismiss = { showCommentBottomSheet = false; selectedPostIdForComment = null }
        )
    }

    // More BottomSheet
    if (showMoreBottomSheet && selectedPostIdForMore != null) {
        PostMoreBottomSheet(
            postId = selectedPostIdForMore!!,
            onDismiss = { showMoreBottomSheet = false; selectedPostIdForMore = null }
        )
    }
}

// --- Các Composable con giữ nguyên logic, chỉ sửa type ---

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileTopBar(navController: NavController) {
    CenterAlignedTopAppBar(
        title = { Text("My Profile", fontWeight = FontWeight.Bold) },
        actions = {
            IconButton(
                onClick = { /*TODO: Navigate to settings*/ },
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_setting),
                    contentDescription = "Settings",
                    modifier = Modifier.size(24.dp)
                )
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
    )
}

@Composable
fun ProfileHeader(
    user: User,
    stats: List<UserState>
) {
    Column(modifier = Modifier.padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = user.profilePictureUrl,
                contentDescription = "User Avatar",
                modifier = Modifier
                    .size(90.dp)
                    .clip(CircleShape)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                stats.forEach { stat ->
                    StateItem(count = stat.count, label = stat.label)
                }
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        Text(user.fullName, fontWeight = FontWeight.Bold)
        Text("@${user.username}", color = Color.Gray)
        user.bio?.let { Text(it) }
    }
}

@Composable
fun StateItem(count: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(count, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Text(label, color = Color.Gray, fontSize = 14.sp)
    }
}

@Composable
fun StoryHighlights(stories: List<StoryHighlight>, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        stories.forEach { story ->
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .border(1.dp, Color.LightGray, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painterResource(id = story.imageRes),
                        contentDescription = story.label,
                        modifier = Modifier.size(32.dp)
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(story.label, fontSize = 12.sp)
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MyPostItem(
    post: PostResponse,
    onPostClick: (Int) -> Unit,
    onMoreClick: (Int) -> Unit
) {
    Column(modifier = Modifier.background(Color.White)) {
        // Header bài đăng
        PostHeader(
            author = post.author,
            onProfileClick = { /* TODO */ },
            onMoreClick = { onMoreClick(post.postId) },
            postCreatedAt = post.createdAt ?: ""
        )

        // Ảnh bài đăng
        val firstImageUrl = post.media.firstOrNull()?.mediaFileUrl
        if (firstImageUrl != null) {
            AsyncImage(
                model = firstImageUrl,
                contentDescription = "Post image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp),
                contentScale = ContentScale.Crop
            )
        }

        // Action buttons
        PostActions(
            likeCount = post.likeCount,
            commentCount = post.commentCount,
            onLikeClick = { /*TODO*/ },
            onCommentClick = { /*TODO*/ }
        )

        // Caption
        Text(
            text = post.caption ?: "",
            fontSize = 14.sp,
            lineHeight = 20.sp,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        HorizontalDivider()
    }
}

data class StoryHighlight(@DrawableRes val imageRes: Int, val label: String)
