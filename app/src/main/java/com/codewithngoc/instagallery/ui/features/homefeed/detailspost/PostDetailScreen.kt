package com.codewithngoc.instagallery.ui.features.homefeed.detailspost

import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.codewithngoc.instagallery.data.model.CommentResponse
import com.codewithngoc.instagallery.ui.features.homefeed.PostItem
import com.codewithngoc.instagallery.ui.features.homefeed.commentsheet.CommentInputSection
import com.codewithngoc.instagallery.ui.features.homefeed.commentsheet.CommentItem
import com.codewithngoc.instagallery.ui.features.homefeed.commentsheet.CommentViewModel
import com.codewithngoc.instagallery.ui.features.homefeed.likeAction.LikeViewModel
import com.codewithngoc.instagallery.ui.features.homefeed.postmorebottomsheet.PostMoreBottomSheet
import com.codewithngoc.instagallery.ui.navigation.Screen

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostDetailScreen(
    navController: NavController,
    viewModel: PostDetailViewModel = hiltViewModel()
) {

    val mainGraphBackStackEntry = remember(navController.currentBackStackEntry) {
        navController.getBackStackEntry("main_graph")
    }
    val likeViewModel: LikeViewModel = hiltViewModel(mainGraphBackStackEntry)
    val commentViewModel: CommentViewModel = hiltViewModel(mainGraphBackStackEntry)

    val uiState by viewModel.uiState.collectAsState()
    val likedPosts by likeViewModel.likedPosts.collectAsState()

    var showMoreBottomSheet by remember { mutableStateOf(false) }
    
    // Yêu cầu load comment khi PostDetailUiState chuyển Success
    var commentsLoadedForPostId by remember { mutableStateOf<Long?>(null) }
    val currentContext = LocalContext.current
    
    // Trạng thái theo dõi bình luận đang được trả lời
    var replyingToComment by remember { mutableStateOf<CommentResponse?>(null) }

    LaunchedEffect(Unit) {
        viewModel.observeLikeEvents(likeViewModel)

        commentViewModel.newCommentEvent.collect { (commentedPostId, _) ->
            val currentState = uiState
            if (currentState is PostDetailUiState.Success && currentState.post.postId == commentedPostId) {
                viewModel.incrementCommentCount()
            }
        }
    }

    val commentEvent by commentViewModel.commentEvent.collectAsState()
    LaunchedEffect(commentEvent) {
        if (commentEvent is CommentViewModel.CommentEvent.Error) {
            Toast.makeText(
                currentContext,
                (commentEvent as CommentViewModel.CommentEvent.Error).message,
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Bài đăng", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            if (uiState is PostDetailUiState.Success) {
                val post = (uiState as PostDetailUiState.Success).post
                var commentText by remember { mutableStateOf("") }
                
                Column(modifier = Modifier.navigationBarsPadding().imePadding()) {
                    HorizontalDivider(color = Color(0xFFEEEEEE), thickness = 1.dp)
                    CommentInputSection(
                        commentText = commentText,
                        onCommentTextChange = { commentText = it },
                        onSendClick = {
                            if (commentText.isNotBlank()) {
                                commentViewModel.addComment(
                                    postId = post.postId,
                                    content = commentText,
                                    parentId = replyingToComment?.commentId
                                )
                                commentText = ""
                                replyingToComment = null
                            }
                        },
                        replyingTo = replyingToComment,
                        onCancelReply = { replyingToComment = null },
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                    )
                }
            }
        },
        containerColor = Color.White
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
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
                    
                    // Trigger load comments
                    if (commentsLoadedForPostId != post.postId) {
                        commentViewModel.loadComments(post.postId)
                        commentsLoadedForPostId = post.postId
                    }

                    val commentsMap by commentViewModel.commentsMap.collectAsState()
                    val comments = commentsMap[post.postId] ?: emptyList()

                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        item {
                            val isLiked = likedPosts[post.postId] ?: false
                            PostItem(
                                post = post,
                                isLiked = isLiked,
                                isDetail = true, // Cấp thuộc tính này để tắt maxLines = 2
                                onPostClick = { }, // Không mở trang chi tiết nữa vì đang ở trong nó
                                onProfileClick = { userId -> 
                                    navController.navigate(Screen.UserProfile.createRoute(userId))
                                },
                                onLikeClick = { 
                                    likeViewModel.toggleLike(post.postId, post.likeCount)
                                },
                                onCommentClick = { 
                                    // Ở trang này thì tự scroll xuống comment luôn hoặc focus input
                                },
                                onMoreClick = { 
                                    showMoreBottomSheet = true
                                }
                            )
                        }

                        item {
                            Text(
                                text = "Bình luận (${post.commentCount})",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                            HorizontalDivider(modifier = Modifier.padding(bottom = 8.dp), thickness = 0.5.dp, color = Color.LightGray)
                        }

                        items(comments) { comment ->
                            CommentItem(
                                comment = comment,
                                onReplyClick = { replyingToComment = it }
                            )
                        }
                        
                        item {
                            Spacer(modifier = Modifier.height(20.dp))
                        }
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