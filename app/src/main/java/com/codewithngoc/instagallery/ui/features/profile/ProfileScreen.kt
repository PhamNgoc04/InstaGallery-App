package com.codewithngoc.instagallery.ui.features.profile

import android.os.Build
import androidx.annotation.DrawableRes
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.compose.ui.platform.LocalLifecycleOwner
import coil.compose.AsyncImage
import com.codewithngoc.instagallery.R
import com.codewithngoc.instagallery.data.model.FeedPostResponse
import com.codewithngoc.instagallery.data.model.ProfileUiState
import com.codewithngoc.instagallery.data.model.User
import com.codewithngoc.instagallery.data.model.UserState
import com.codewithngoc.instagallery.data.utils.formatTimeAgo
import com.codewithngoc.instagallery.ui.features.homefeed.HomeInsBottomBar
import com.codewithngoc.instagallery.ui.features.homefeed.PostActions
import com.codewithngoc.instagallery.ui.features.homefeed.PostHeader
import com.codewithngoc.instagallery.ui.features.homefeed.PostItem
import com.codewithngoc.instagallery.ui.features.homefeed.commentsheet.CommentBottomSheet
import com.codewithngoc.instagallery.ui.features.homefeed.commentsheet.CommentViewModel
import com.codewithngoc.instagallery.ui.features.homefeed.likeAction.LikeViewModel
import com.codewithngoc.instagallery.ui.features.homefeed.postmorebottomsheet.PostMoreBottomSheet
import com.codewithngoc.instagallery.ui.navigation.Screen

// --- Composable chính ---
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ProfileScreen(
    navController: NavController,
    userId: Long,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    val mainGraphBackStackEntry = remember(navController.currentBackStackEntry) {
        navController.getBackStackEntry("main_graph")
    }
    val likeViewModel: LikeViewModel = hiltViewModel(mainGraphBackStackEntry)
    val commentViewModel: CommentViewModel = hiltViewModel(mainGraphBackStackEntry)

    // ✅ Theo dõi likedPosts từ LikeViewModel
    val likedPosts by likeViewModel.likedPosts.collectAsState()

    // State cho BottomSheet
    var showCommentBottomSheet by remember { mutableStateOf(false) }
    var selectedPostIdForComment by remember { mutableStateOf<Long?>(null) }
    var showMoreBottomSheet by remember { mutableStateOf(false) }
    var selectedPostIdForMore by remember { mutableStateOf<Long?>(null) }
    
    // State cho Xóa Post
    var showDeleteDialog by remember { mutableStateOf(false) }
    var postToDelete by remember { mutableStateOf<Long?>(null) }


    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner, userId) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.loadProfile(userId)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    // Theo dõi lắng nghe sự kiện từ LikeVieModel
    LaunchedEffect(Unit) {
        viewModel.observeLikeEvents(likeViewModel)
    }

    LaunchedEffect(Unit) {
        commentViewModel.newCommentEvent.collect { (postId, _) ->
            viewModel.updateCommentCount(postId)
        }
    }

    LaunchedEffect(state) {
        // Backend mới trả isLiked trong feed, không cần gọi checkLiked riêng
    }

    Scaffold(
        topBar = { ProfileTopBar(
            navController = navController
        ) },
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
                    Text(text = "❌ $message", style = MaterialTheme.typography.bodyLarge)
                }
            }

            is ProfileUiState.Success -> {
                val success = state as ProfileUiState.Success
                LazyColumn(modifier = Modifier.padding(paddingValues)) {
                    // 1. Header Profile
                    item {
                        ProfileHeader(
                            user = success.user,
                            states = success.states
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

                        val isLiked = likedPosts[post.postId] ?: false

                        ProfilePostItem (
                            post = post,
                            isLiked = isLiked,
                            onPostClick = { postId ->
                                navController.navigate(Screen.PostDetail.createRoute(postId.toString()))
                            },
                            onProfileClick = {
                                navController.navigate(Screen.Profile.route)
                            },
                            onLikeClick = {
                                likeViewModel.toggleLike(post.postId, post.likeCount)
                            },
                            onCommentClick = { postId ->
                                selectedPostIdForComment = postId
                                showCommentBottomSheet = true
                            },
                            onShareClick = { },
                            onEditClick = {
                                navController.navigate(
                                    Screen.EditPostProfile.createRoute(post.postId.toString())
                                )
                            },
                            onDeleteClick = { 
                                postToDelete = post.postId
                                showDeleteDialog = true
                            }
                        )
                    }
                }
            }
            else -> {}
        }
    }
    // Comment BottomSheet
    if (showCommentBottomSheet && selectedPostIdForComment != null) {
        CommentBottomSheet(
            postId = selectedPostIdForComment!!,
            onDismiss = { showCommentBottomSheet = false; selectedPostIdForComment = null }
        )
    }

    // Delete Confirmation Dialog
    if (showDeleteDialog && postToDelete != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false; postToDelete = null },
            title = { Text("Xóa bài viết", fontWeight = FontWeight.Bold) },
            text = { Text("Bạn có chắc chắn muốn xóa bài viết này không? Hành động này không thể hoàn tác.") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deletePost(postToDelete!!)
                    showDeleteDialog = false
                    postToDelete = null
                }) {
                    Text("Xóa", color = Color.Red, fontWeight = FontWeight.SemiBold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false; postToDelete = null }) {
                    Text("Hủy")
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileTopBar(navController: NavController) {
    CenterAlignedTopAppBar(
        title = { Text("Hồ sơ", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold) },
        actions = {
            IconButton(
                onClick = {
                    navController.navigate(Screen.Messages.route) {
                        launchSingleTop = true
                    }
                },
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_gui),
                    contentDescription = "Tin nhắn",
                    modifier = Modifier.size(24.dp),
                    tint = Color.DarkGray
                )
            }
            IconButton(
                onClick = {
                    navController.navigate(Screen.Settings.route) {
                        launchSingleTop = true
                    }
                },
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_setting),
                    contentDescription = "Cài đặt",
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
    states: List<UserState>
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
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(16.dp))
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                states.forEach { state ->
                    StateItem(count = state.count, label = state.label)
                }
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        Text(user.fullName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Text("@${user.username}", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
        user.bio?.let { Text(it, style = MaterialTheme.typography.bodyMedium) }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ProfilePostItem(
    post: FeedPostResponse,
    isLiked : Boolean,
    onPostClick: (Long) -> Unit,
    onProfileClick: (Long) -> Unit,
    onLikeClick: (Long) -> Unit,
    onCommentClick: (Long) -> Unit,
    onShareClick: (Long) -> Unit,
    onEditClick: (Long) -> Unit,
    onDeleteClick: (Long) -> Unit )
{
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .wrapContentHeight()
            .padding(bottom = 16.dp) // Khoảng cách giữa các bài đăng
    ) {

        // ✅ Header riêng cho ProfileScreen
        ProfilePostHeaders(
            username = post.username,
            userAvatar = post.userAvatar,
            userId = post.userId,
            postId = post.postId,
            onProfileClick = onProfileClick,
            postCreatedAt = post.createdAt,
            onShareClick = { onShareClick(post.postId) },
            onEditClick = { onEditClick(post.postId) },
            onDeleteClick = { onDeleteClick(post.postId) }
        )

        // --- Post Image (Clickable) ---
        val firstImageUrl = post.media.firstOrNull()?.url
        if (firstImageUrl != null) {
            AsyncImage(
                model = firstImageUrl,
                contentDescription = "Post image",
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f) // Giúp tự động scale mượt vuông thay vì hardcode .height(300.dp)
                    .clickable { onPostClick(post.postId) }, // Chỉ click vào ảnh mới mở trang chi tiết
                contentScale = ContentScale.Crop // Crop ảnh vuông
            )
        }
        PostActions(
            likeCount = post.likeCount,
            commentCount = post.commentCount,
            isLiked = isLiked,
            onLikeClick = { onLikeClick(post.postId) }, // Truyền hành động click like
            onCommentClick = { onCommentClick(post.postId) } // Truyền hành động click comment
        )
        // --- Caption ---
        Text(
            text = post.caption ?: "",
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ProfilePostHeaders(
    username: String,
    userAvatar: String?,
    userId: Long,
    postId: Long,
    onProfileClick: (Long) -> Unit,
    postCreatedAt: String?,
    onShareClick: (Long) -> Unit,
    onEditClick: (Long) -> Unit,
    onDeleteClick: (Long) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .weight(1f)
                .clickable { onProfileClick(userId) }
        ) {
            // Avatar
            AsyncImage(
                model = userAvatar,
                contentDescription = "Profile Image",
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop,
                placeholder = ColorPainter(Color.LightGray),
                error = ColorPainter(Color.LightGray)
            )
            // User Info
            Column(modifier = Modifier.padding(horizontal = 12.dp)) {
                Text(
                    text = username,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Text(
                    text = formatTimeAgo(postCreatedAt ?: ""),
                    color = Color.Gray,
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }

// --- Action Buttons (Share, Edit, Delete) ---
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { onShareClick(postId) }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_share_baiviet),
                    contentDescription = "Share",
                    modifier = Modifier.size(28.dp)
                )
            }
            IconButton(onClick = { onEditClick(postId) }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_chinhsua),
                    contentDescription = "Edit",
                    modifier = Modifier.size(28.dp)
                )
            }
            IconButton(onClick = { onDeleteClick(postId) }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_xoa),
                    contentDescription = "Delete",
                    tint = Color.Red,
                    modifier = Modifier.size(28.dp)
                )
            }
        }

    }
}

@Composable
fun StateItem(count: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(count, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Text(label, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
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
                Text(story.label, style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}

data class StoryHighlight(@DrawableRes val imageRes: Int, val label: String)

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ProfileScreenPreview() {
    val navController = rememberNavController()

    // Gọi màn hình chính với userId mẫu
    ProfileScreen(
        navController = navController,
        userId = 1L
    )
}
