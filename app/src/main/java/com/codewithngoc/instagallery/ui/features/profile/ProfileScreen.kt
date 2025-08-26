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
import coil.compose.AsyncImage
import com.codewithngoc.instagallery.R
import com.codewithngoc.instagallery.data.model.AuthorInfoResponse
import com.codewithngoc.instagallery.data.model.PostResponse
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
        commentViewModel.newCommentEvent.collect { (postId, _) ->
            viewModel.updateCommentCount(postId)
        }
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
                        ProfilePostItem (
                            post = post,
                            onPostClick = { postId ->
                                navController.navigate(Screen.PostDetail.createRoute(postId.toString()))
                            },
                            onProfileClick = {
                                navController.navigate(Screen.Profile.route)
                            },
                            onLikeClick = { },
                            onCommentClick = { postId ->
                                selectedPostIdForComment = postId
                                showCommentBottomSheet = true
                            },
                            onShareClick = { },
                            onEditClick = { },
                            onDeleteClick = { }
                        )
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
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileTopBar(navController: NavController) {
    CenterAlignedTopAppBar(
        title = { Text("My Profile", fontWeight = FontWeight.Bold) },
        actions = {
            IconButton(
                onClick = {
                    navController.navigate(Screen.Logout.route) {
                        // optional: đảm bảo chỉ navigate một lần nếu cần
                        launchSingleTop = true
                    }
                },
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
                    .clip(CircleShape)
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
        Text(user.fullName, fontWeight = FontWeight.Bold)
        Text("@${user.username}", color = Color.Gray)
        user.bio?.let { Text(it) }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ProfilePostItem(
    post: PostResponse,
    onPostClick: (Int) -> Unit, // Đổi tên hàm để rõ ràng hơn
    onProfileClick: (Int) -> Unit, // Hành động khi click avatar
    onLikeClick: (Int) -> Unit,    // Hành động khi click like
    onCommentClick: (Int) -> Unit, // Hành động khi click comment
    onShareClick: (Int) -> Unit,
    onEditClick: (Int) -> Unit,
    onDeleteClick: (Int) -> Unit )
{
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .wrapContentHeight()
            .padding(bottom = 16.dp) // Khoảng cách giữa các bài đăng
    ) {

        // ✅ Header riêng cho ProfileScreen (không còn More)
        ProfilePostHeaders(
            author = post.author,
            postId = post.postId,
            onProfileClick = onProfileClick,
            postCreatedAt = post.createdAt ?: "",
            onShareClick = { onShareClick(post.postId) },
            onEditClick = { onEditClick(post.postId) },
            onDeleteClick = { onDeleteClick(post.postId) }
        )

        // --- Post Image (Clickable) ---
        val firstImageUrl = post.media.firstOrNull()?.mediaFileUrl
        if (firstImageUrl != null) {
            AsyncImage(
                model = firstImageUrl,
                contentDescription = "Post image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .clickable { onPostClick(post.postId) }, // Chỉ click vào ảnh mới mở trang chi tiết
                contentScale = ContentScale.Fit
            )
        }
        PostActions(
            likeCount = post.likeCount,
            commentCount = post.commentCount,
            onLikeClick = { onLikeClick(post.postId) }, // Truyền hành động click like
            onCommentClick = { onCommentClick(post.postId) } // Truyền hành động click comment
        )
        // --- Caption ---
        Text(
            text = post.caption ?: "",
            fontSize = 14.sp,
            lineHeight = 20.sp,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ProfilePostHeaders(
    author: AuthorInfoResponse,
    postId: Int,
    onProfileClick: (Int) -> Unit,
    postCreatedAt: String?,
    onShareClick: (Int) -> Unit,
    onEditClick: (Int) -> Unit,
    onDeleteClick: (Int) -> Unit
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
            modifier = Modifier.clickable { onProfileClick(author.userId) } // Thêm clickable cho cả row
        ) {
            // Avatar
            AsyncImage(
                model = author.profilePictureUrl,
                contentDescription = "Profile Image",
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            // User Info
            Column(modifier = Modifier.padding(horizontal = 12.dp)) {
                Text(
                    text = author.username,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color.Black
                )
                Text(
                    text = formatTimeAgo(postCreatedAt ?: ""),
                    color = Color.Gray,
                    fontSize = 12.sp
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

data class StoryHighlight(@DrawableRes val imageRes: Int, val label: String)

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ProfileScreenPreview() {
    val navController = rememberNavController()

    // Gọi màn hình chính với userId mẫu
    ProfileScreen(
        navController = navController,
        userId = 1
    )
}
