package com.codewithngoc.instagallery.ui.features.homefeed

import android.os.Build
import androidx.annotation.DrawableRes
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
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
import coil.request.ImageRequest
import coil.size.Size
import com.codewithngoc.instagallery.R
import com.codewithngoc.instagallery.data.model.AuthorInfoResponse
import com.codewithngoc.instagallery.data.model.MediaResponse
import com.codewithngoc.instagallery.data.model.MediaType
import com.codewithngoc.instagallery.data.model.PostResponse
import com.codewithngoc.instagallery.data.model.PostVisibility
import com.codewithngoc.instagallery.data.utils.formatTimeAgo
import com.codewithngoc.instagallery.ui.features.homefeed.commentsheet.CommentBottomSheet
import com.codewithngoc.instagallery.ui.features.homefeed.commentsheet.CommentViewModel
import com.codewithngoc.instagallery.ui.features.homefeed.likeAction.LikeViewModel
import com.codewithngoc.instagallery.ui.features.homefeed.postmorebottomsheet.PostMoreBottomSheet
import com.codewithngoc.instagallery.ui.navigation.Screen
import kotlinx.coroutines.flow.collectLatest
import java.time.Instant

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeFeedScreen(
    navController: NavController,
    viewModel: HomeFeedViewModel = hiltViewModel(),
    likeViewModel: LikeViewModel = hiltViewModel(),
) {
    // Biến trạng thái để hiển thị Loading
    val uiState = viewModel.uiState.collectAsState()

    // Biến trạng thái để hiển thị danh sách posts
    val posts by viewModel.posts.collectAsState()

    // Theo dõi trạng thái liked từ LikeViewModel
    val likedPosts by likeViewModel.likedPosts.collectAsState()

    // Biến trạng thái để hiển thị CommentBottomSheet
    var showCommentDialog by remember { mutableStateOf(false) }

    // Biến trạng thái để hiển thị MoreBottomSheet
    var selectedPostIdForComment by remember { mutableStateOf<Int?>(null) }

    // ✅ Khởi tạo CommentViewModel dùng để hiển thị CommentBottomSheet
    val commentViewModel: CommentViewModel = hiltViewModel()

    // Biến trạng thái cho Share/More BottomSheet
    var showMoreBottomSheet by remember { mutableStateOf(false) }
    var selectedPostIdForMore by remember { mutableStateOf<Int?>(null) }

    // ✅ Theo dõi sự kiện từ CommentViewModel
    LaunchedEffect(Unit) {
        commentViewModel.newCommentEvent.collect { (postId, _) ->
            viewModel.updateCommentCount(postId)
        }
    }

    // Gọi loadLikes & checkLiked khi posts thay đổi
    LaunchedEffect(posts) {
        posts.forEach { post ->
            likeViewModel.loadLikes(post.postId)
            likeViewModel.checkLiked(post.postId)
        }
    }

    // Theo dõi lắng nghe sự kiện từ LikeVieModel
    LaunchedEffect(Unit) {
        viewModel.observeLikeEvents(likeViewModel)
    }

    // ✅ Theo dõi sự kiện từ HomeFeedViewModel
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

//    LaunchedEffect(Unit) {
//        viewModel.loadAllPosts()
//    }

    // Hiển thị Scaffold
    Scaffold(
        topBar = { HomeInsTopBar() }, // ✅ Sử dụng HomeInsTopBar
        bottomBar = { HomeInsBottomBar(navController = navController) }, // ✅ Sử dụng HomeInsBottomBar
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            when (uiState.value) {
//                is HomeFeedViewModel.PostEvent.Loading -> {
//                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
//                }
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
                        likedPosts = likedPosts,
                        onPostClick = { postId -> viewModel.onPostClick(postId) },
                        onProfileClick = { userId -> /* TODO: Navigate to Profile screen */ },
                        onLikeClick = { postId -> likeViewModel.toggleLike(postId) },
                        onCommentClick = { postId ->
                            selectedPostIdForComment = postId
                            showCommentDialog = true
                        },
                        onMoreClick = { postId -> // ✅ Thêm callback onMoreClick
                            selectedPostIdForMore = postId
                            showMoreBottomSheet = true
                        }
                    )
                }
            }
        }
    }

    // ✅ Hiển thị CommentBottomSheet
    if (showCommentDialog && selectedPostIdForComment != null) {
        CommentBottomSheet( // Gọi CommentBottomSheet thay vì CommentDialog
            onDismiss = {
                showCommentDialog = false
                selectedPostIdForComment = null
            },
            postId = selectedPostIdForComment!!
        )
    }

    // ✅ Hiển thị MoreBottomSheet
    if (showMoreBottomSheet && selectedPostIdForMore != null) {
        PostMoreBottomSheet(
            onDismiss = { showMoreBottomSheet = false },
            postId = selectedPostIdForMore!!
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun PostList(
    modifier: Modifier = Modifier,
    posts: List<PostResponse>,
    likedPosts : Map<Int, Boolean>,
    onPostClick: (Int) -> Unit,
    onProfileClick: (Int) -> Unit,
    onLikeClick: (Int) -> Unit,
    onCommentClick: (Int) -> Unit,
    onMoreClick: (Int) -> Unit,
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White),
        contentPadding = PaddingValues(top = 8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(posts) { post ->
            val isLiked = likedPosts[post.postId] ?: false
            PostItem(
                post = post,
                isLiked = isLiked,
                onPostClick = { onPostClick(post.postId) }, // Mở trang chi tiết bài viết khi click vào ảnh
                onProfileClick = { onProfileClick(post.author.userId) }, // Mở trang cá nhân khi click vào avatar/tên
                onLikeClick = { onLikeClick(post.postId) }, // Xử lý sự kiện thích bài viết
                onCommentClick = { onCommentClick(post.postId) }, // Xử lý sự kiện bình luận
                onMoreClick = { onMoreClick(post.postId) } // Xử lý sự kiện click More
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun PostItem(
    post: PostResponse,
    isLiked: Boolean,
    onPostClick: (Int) -> Unit, // Đổi tên hàm để rõ ràng hơn
    onProfileClick: (Int) -> Unit, // Hành động khi click avatar
    onLikeClick: (Int) -> Unit,    // Hành động khi click like
    onCommentClick: (Int) -> Unit, // Hành động khi click comment
    onMoreClick: (Int) -> Unit, // Hành động khi click More
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .wrapContentHeight()
            .padding(bottom = 16.dp) // Khoảng cách giữa các bài đăng
    ) {
        PostHeader(
            author = post.author,
            onProfileClick = onProfileClick, // Truyền hành động click avatar
            onMoreClick = { onMoreClick(post.postId) },
            postCreatedAt = post.createdAt?: "",
        )
        // --- Post Image (Clickable) ---
        val firstImageUrl = post.media.firstOrNull()?.mediaFileUrl
        if (firstImageUrl != null) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(firstImageUrl.takeIf { !it.isNullOrBlank() })
                    .crossfade(true)
                    .size(Size(1080, 1080)) // hạn chế load ảnh quá lớn
                    .build(),
                contentDescription = "Post Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
//                    .clip(RoundedCornerShape(12.dp)) // optional cho đẹp
                    .clickable { onPostClick(post.postId) },
                contentScale = ContentScale.Crop, // Fit nhìn dễ lỗi layout, Crop thường đẹp hơn
                placeholder = painterResource(R.drawable.placeholder_post),
                error = painterResource(R.drawable.ic_error) // ✅ thay bằng 1 vector drawable
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
fun PostHeader(
    author: AuthorInfoResponse,
    onProfileClick: (Int) -> Unit,
    onMoreClick: (Int) -> Unit,
    postCreatedAt: String?,
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
                model = ImageRequest.Builder(LocalContext.current)
                    .data(author.profilePictureUrl.takeIf { !it.isNullOrBlank() })
                    .crossfade(true)
                    .build(),
                contentDescription = "Profile Image",
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop,
                placeholder = ColorPainter(Color.LightGray), // ✅ thay shape bằng màu xám
                error = painterResource(R.drawable.ic_error) // ✅ thay bằng 1 vector drawable
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
//        // Share Icon
//        // ✅ Nút more icon để mở bottom sheet
        IconButton(onClick = { onMoreClick(author.userId) }) {
            Icon(
                painter = painterResource(id = R.drawable.ic_more),
                contentDescription = "More options",
                tint = Color.Gray,
                modifier = Modifier.size(24.dp)
            )
        }

    }
}



@Composable
fun PostActions(
    likeCount: Int,
    commentCount: Int,
    isLiked: Boolean,
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
                val likeIcon = if (isLiked) R.drawable.ic_heart_filled else R.drawable.ic_tymm
                val likeTint = (if (isLiked) Color.Red else Color.Black).also {

                    Icon(
                        painter = painterResource(likeIcon),
                        contentDescription = "Like",
                        tint = it,
                        modifier = Modifier.size(26.dp)
                    )
                }

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
fun HomeInsBottomBar(
    navController: NavController
) {

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
            onClick = {
                    navController.navigate(Screen.Profile.route)
            }
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

@RequiresApi(Build.VERSION_CODES.O)
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
            commentCount = 5,
            createdAt = "2023-08-01T12:00:00Z"
        ),
        PostResponse(
            postId = 2,
            author = AuthorInfoResponse(userId = 2, username = "Hank Lozano", profilePictureUrl = "https://example.com/hank.png"),
            caption = "Another great day at the park!",
            location = "Park",
            visibility = PostVisibility.PUBLIC,
            media = listOf(MediaResponse(2, "https://images.unsplash.com/photo-1519985176271-adb1088fa94c", mediaType = MediaType.IMAGE, position = 1)),
            likeCount = 20,
            commentCount = 8,
            createdAt = "2023-08-02T15:30:00Z"
        )
    )

    // ✅ Mock likedPosts để Preview không lỗi
    val likedPosts = mapOf(
        1 to true,  // postId 1 đã like
        2 to false  // postId 2 chưa like
    )

    Scaffold(
        topBar = { HomeInsTopBar() },
        bottomBar = { HomeInsBottomBar(navController = navController) }
    ) { paddingValues ->
        PostList(
            modifier = Modifier.padding(paddingValues),
            posts = samplePosts,
            likedPosts = likedPosts,
            onPostClick = {},
            onProfileClick = {},
            onLikeClick = {},
            onCommentClick = {},
            onMoreClick = {}
        )
    }
}