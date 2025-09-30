package com.codewithngoc.instagallery.ui.features.homefeed.detailspost

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.codewithngoc.instagallery.ui.features.homefeed.PostItem
import com.codewithngoc.instagallery.ui.features.homefeed.commentsheet.CommentBottomSheet
import com.codewithngoc.instagallery.ui.features.homefeed.commentsheet.CommentViewModel
import com.codewithngoc.instagallery.ui.features.homefeed.likeAction.LikeViewModel
import com.codewithngoc.instagallery.ui.features.homefeed.postmorebottomsheet.PostMoreBottomSheet

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostDetailScreen(
    navController: NavController,
    viewModel: PostDetailViewModel = hiltViewModel(),
    commentViewModel: CommentViewModel = hiltViewModel() // <-- Inject CommentViewModel
) {

    // Tìm đến back stack của graph cha
    val mainGraphBackStackEntry = remember(navController.currentBackStackEntry) {
        navController.getBackStackEntry("main_graph")
    }
    // Lấy ViewModel được scope cho graph đó
    val likeViewModel: LikeViewModel = hiltViewModel(mainGraphBackStackEntry)

    val uiState by viewModel.uiState.collectAsState()
    val likedPosts by likeViewModel.likedPosts.collectAsState()

    // --- QUẢN LÝ STATE CHO BOTTOMSHEET ---
    var showCommentBottomSheet by remember { mutableStateOf(false) }
    var showMoreBottomSheet by remember { mutableStateOf(false) }

    // --- LẮNG NGHE SỰ KIỆN TỪ CÁC VIEWMODEL KHÁC ---
    // Effect này chạy 1 lần để thiết lập các listener
    LaunchedEffect(Unit) {
        // 1. Yêu cầu ViewModel lắng nghe sự kiện like/unlike để cập nhật likeCount real-time
        viewModel.observeLikeEvents(likeViewModel)

        // 2. Lắng nghe sự kiện có bình luận mới để cập nhật commentCount
        commentViewModel.newCommentEvent.collect { (commentedPostId, _) ->
            val currentState = uiState
            if (currentState is PostDetailUiState.Success && currentState.post.postId == commentedPostId) {
                viewModel.incrementCommentCount()
            }
        }
    }

    // Effect này chạy mỗi khi uiState thay đổi (đặc biệt là khi load xong post)
    LaunchedEffect(uiState) {
        // 3. Kiểm tra trạng thái like ban đầu khi post được tải thành công
        if (uiState is PostDetailUiState.Success) {
            val post = (uiState as PostDetailUiState.Success).post
            likeViewModel.checkLiked(post.postId)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Post") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            when (val state = uiState) {
                is PostDetailUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is PostDetailUiState.Error -> {
                    Text(
                        text = state.message,
                        modifier = Modifier.align(Alignment.Center),
                        color = MaterialTheme.colorScheme.error
                    )
                }
                is PostDetailUiState.Success -> {
                    val post = state.post
                    val isLiked = likedPosts[post.postId] ?: false

                    // TÁI SỬ DỤNG PostItem VỚI ĐẦY ĐỦ CHỨC NĂNG
                    PostItem(
                        post = post,
                        isLiked = isLiked,
                        onPostClick = { },
                        onProfileClick = { /* TODO: Điều hướng đến trang cá nhân */ },
                        onLikeClick = { likeViewModel.toggleLike(post.postId, post.likeCount) },
                        onCommentClick = { showCommentBottomSheet = true }, // <-- Mở comment
                        onMoreClick = { showMoreBottomSheet = true }      // <-- Mở more options
                    )

                    // --- HIỂN THỊ CÁC BOTTOMSHEET ---
                    if (showCommentBottomSheet) {
                        CommentBottomSheet(
                            onDismiss = { showCommentBottomSheet = false },
                            postId = post.postId
                        )
                    }

                    if (showMoreBottomSheet) {
                        PostMoreBottomSheet(
                            onDismiss = { showMoreBottomSheet = false },
                            postId = post.postId
                        )
                    }
                }
            }
        }
    }
}