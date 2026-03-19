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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
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
    commentViewModel: CommentViewModel = hiltViewModel()
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
    LaunchedEffect(Unit) {
        viewModel.observeLikeEvents(likeViewModel)

        commentViewModel.newCommentEvent.collect { (commentedPostId, _) ->
            val currentState = uiState
            if (currentState is PostDetailUiState.Success && currentState.post.postId == commentedPostId) {
                viewModel.incrementCommentCount()
            }
        }
    }

    // Backend mới không cần checkLiked riêng
    LaunchedEffect(uiState) {
        // No-op
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

                    // TODO: Tạm hiển thị đơn giản
                    // PostItem yêu cầu FeedPostResponse, PostDetailViewModel dùng PostResponse
                    // Khi backend có getPostById, cần map PostResponse → display
                    Text(
                        text = post.caption ?: "Chi tiết bài viết",
                        modifier = Modifier.padding(16.dp)
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