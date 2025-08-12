package com.codewithngoc.instagallery.ui.features.homefeed

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
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
import com.codewithngoc.instagallery.data.model.MediaResponse
import com.codewithngoc.instagallery.data.model.MediaType
import com.codewithngoc.instagallery.data.model.PostResponse
import com.codewithngoc.instagallery.data.model.PostVisibility
import com.codewithngoc.instagallery.ui.navigation.Screen
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeFeedScreen(
    navController: NavController,
    viewModel: HomeFeedViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState.collectAsState()
    val posts by viewModel.posts.collectAsState()
    LaunchedEffect(Unit) {
        viewModel.navigationEvent.collectLatest { event ->
            when (event) {
                is HomeFeedViewModel.HomeFeedNavigationEvent.NavigateToPostDetail -> {
                    navController.navigate(Screen.PostDetail.createRoute(event.postId.toString())) {
                        launchSingleTop = true
                    }
                }
            }
        }
    }
    LaunchedEffect(Unit) {
        viewModel.loadAllPosts()
    }
    Scaffold(
        topBar = { HomeInsTopBar() },
        bottomBar = { HomeInsBottomBar(navController = navController) },
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            when (uiState.value) {
                is HomeFeedViewModel.PostEvent.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is HomeFeedViewModel.PostEvent.Error -> {
                    Text(
                        text = (uiState.value as HomeFeedViewModel.PostEvent.Error).message,
                        modifier = Modifier.align(Alignment.Center),
                        color = MaterialTheme.colorScheme.error
                    )
                }
                else -> {
                    PostList(
                        posts = posts,
                        onPostClick = { postId -> viewModel.onPostClick(postId) },
                        onProfileClick = { userId -> /* TODO: Navigate to Profile screen */ },
                        onLikeClick = { postId -> /* TODO: Call ViewModel function to handle like */ },
                        onCommentClick = { postId -> /* TODO: Handle comment action */ }
                    )
                }
            }
        }
    }
}
@Composable
fun PostList(
    modifier: Modifier = Modifier,
    posts: List<PostResponse>,
    onPostClick: (Int) -> Unit,
    onProfileClick: (Int) -> Unit,
    onLikeClick: (Int) -> Unit,
    onCommentClick: (Int) -> Unit,
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White),
        contentPadding = PaddingValues(top = 8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(posts) { post ->
            PostItem(
                post = post,
                onPostClick = { onPostClick(post.postId) }, // Mở trang chi tiết bài viết khi click vào ảnh
                onProfileClick = { onProfileClick(post.author.userId) }, // Mở trang cá nhân khi click vào avatar/tên
                onLikeClick = { onLikeClick(post.postId) }, // Xử lý sự kiện thích bài viết
                onCommentClick = { onCommentClick(post.postId) } // Xử lý sự kiện bình luận
            )
        }
    }
}
//--------------------------------------------------------------------------------------------------
@Composable
fun PostItem(
    post: PostResponse,
    onPostClick: (Int) -> Unit, // Đổi tên hàm để rõ ràng hơn
    onProfileClick: (Int) -> Unit, // Hành động khi click avatar
    onLikeClick: (Int) -> Unit,    // Hành động khi click like
    onCommentClick: (Int) -> Unit, // Hành động khi click comment
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(bottom = 16.dp) // Khoảng cách giữa các bài đăng
    ) {
        PostHeader(
            author = post.author,
            onProfileClick = onProfileClick, // Truyền hành động click avatar
            postTime = "5:30 PM"
        )
        // --- Post Image (Clickable) ---
        val firstImageUrl = post.media.firstOrNull()?.mediaFileUrl
        if (firstImageUrl != null) {
            AsyncImage(
                model = firstImageUrl,
                contentDescription = "Post image",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .clickable { onPostClick(post.postId) } // Chỉ click vào ảnh mới mở trang chi tiết
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
//--------------------------------------------------------------------------------------------------
@Composable
fun PostHeader(
    author: AuthorInfoResponse,
    onProfileClick: (Int) -> Unit,
    postTime: String
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
                    text = postTime,
                    color = Color.Gray,
                    fontSize = 12.sp
                )
            }
        }
        // Share Icon
        Image(
            painter = painterResource(id = R.drawable.group_share),
            contentDescription = "Share",
            modifier = Modifier.size(24.dp)
        )
    }
}
@Composable
fun PostActions(
    likeCount: Int,
    commentCount: Int,
    onLikeClick: () -> Unit,
    onCommentClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            // Like
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable { onLikeClick() }
            ) {
                Image(
                    painterResource(R.drawable.ic_tymm),
                    contentDescription = "Like",
                    modifier = Modifier.size(26.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = likeCount.toString(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Black
                )
            }
            // Comment
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable { onCommentClick() }
            ) {
                Image(
                    painterResource(R.drawable.ic_comment),
                    contentDescription = "Comment",
                    modifier = Modifier.size(26.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = commentCount.toString(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Black
                )
            }
        }
        // Các nút khác
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Image(
                painterResource(R.drawable.ic_gui),
                contentDescription = "Send",
                modifier = Modifier.size(26.dp)
            )
            Image(
                painterResource(R.drawable.ic_luu),
                contentDescription = "Save",
                modifier = Modifier.size(26.dp)
            )
        }
    }
}
@Composable
fun HomeInsTopBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(top = 25.dp)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_register_logo),
            contentDescription = "Logo",
            modifier = Modifier.size(30.dp)
        )
        Text(
            text = "InstaGallery",
            style = TextStyle(
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = colorResource(id = R.color.orange_button_login)
            )
        )
        Image(
            painter = painterResource(id = R.drawable.ic_search),
            contentDescription = "Search Icon",
            modifier = Modifier.size(30.dp)
        )
    }
}
@Composable
fun HomeInsBottomBar(navController: NavController) {
    val currentRoute = navController.currentBackStackEntry?.destination?.route
    val standardIconSize = 26.dp
    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 8.dp
    ) {
        BottomNavItem(
            isSelected = currentRoute == Screen.HomeFeed.route,
            onClick = { navController.navigate(Screen.HomeFeed.route) }
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_homefeed),
                contentDescription = "Home",
                modifier = Modifier.size(standardIconSize)
            )
        }
        BottomNavItem(
            isSelected = false,
            onClick = { /* TODO: mở News */ }
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_danhsach),
                contentDescription = "News",
                modifier = Modifier.size(standardIconSize)
            )
        }
        BottomNavItem(
            isSelected = currentRoute == Screen.NewPost.route,
            onClick = { navController.navigate(Screen.NewPost.route) }
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_them_bai),
                contentDescription = "Add Post",
                modifier = Modifier.size(standardIconSize)
            )
        }
        BottomNavItem(
            isSelected = false,
            onClick = { /* TODO: mở Notifications */ }
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_thong_bao),
                contentDescription = "Notifications",
                modifier = Modifier.size(standardIconSize)
            )
        }
        val isProfileSelected = currentRoute == Screen.Profile.route
        BottomNavItem(
            isSelected = isProfileSelected,
            onClick = { /* TODO: mở Profile */ }
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_register_logo),
                contentDescription = "Profile",
                modifier = Modifier
                    .size(standardIconSize)
                    .clip(CircleShape)
                    .border(
                        width = if (isProfileSelected) 2.dp else 1.dp,
                        color = if (isProfileSelected) Color(0xFFE91E63) else Color.Gray,
                        shape = CircleShape
                    )
            )
        }
    }
}
@Composable
fun RowScope.BottomNavItem(
    isSelected: Boolean,
    onClick: () -> Unit,
    icon: @Composable () -> Unit
) {
    NavigationBarItem(
        selected = isSelected,
        onClick = onClick,
        icon = icon,
        colors = NavigationBarItemDefaults.colors(
            selectedIconColor = Color(0xFFE91E63),
            unselectedIconColor = Color.Gray,
            indicatorColor = Color.Transparent
        )
    )
}
@Composable
fun PostDetailScreen(postId: String, navController: NavController) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = "Post Detail for id: $postId")
    }
}
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun HomeFeedScreenPreview() {
    val navController = rememberNavController()
    // You'd need a mock ViewModel here to provide sample data
    // For simplicity, we'll just show the PostList directly.
    val samplePosts = listOf(
        PostResponse(
            postId = 1,
            author = AuthorInfoResponse(userId = 1, username = "Baxter Johnson", profilePictureUrl = "https://example.com/baxter.png"),
            caption = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed eiusmod tempor amet...",
            location = "Park",
            visibility = PostVisibility.PUBLIC,
            media = listOf(MediaResponse(1, "https://images.unsplash.com/photo-1507525428034-b723cf961d3e", mediaType = MediaType.IMAGE, position = 0)),
            likeCount = 12,
            commentCount = 5
        ),
        PostResponse(
            postId = 2,
            author = AuthorInfoResponse(userId = 2, username = "Hank Lozano", profilePictureUrl = "https://example.com/hank.png"),
            caption = "Another great day at the park!",
            location = "Park",
            visibility = PostVisibility.PUBLIC,
            media = listOf(MediaResponse(2, "https://images.unsplash.com/photo-1519985176271-adb1088fa94c", mediaType = MediaType.IMAGE, position = 1)),
            likeCount = 20,
            commentCount = 8
        )
    )
    Scaffold(
        topBar = { HomeInsTopBar() },
        bottomBar = { HomeInsBottomBar(navController = navController) }
    ) { paddingValues ->
        PostList(
            modifier = Modifier.padding(paddingValues),
            posts = samplePosts,
            onPostClick = {},
            onProfileClick = {},
            onLikeClick = {},
            onCommentClick = {}
        )
    }
}