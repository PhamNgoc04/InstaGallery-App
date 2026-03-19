package com.codewithngoc.instagallery.ui.features.homefeed.commentsheet

import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
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
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.codewithngoc.instagallery.R
import com.codewithngoc.instagallery.data.model.CommentResponse
import com.codewithngoc.instagallery.data.utils.formatTimeAgo


@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommentBottomSheet(
    onDismiss: () -> Unit,
    postId: Long,
    viewModel: CommentViewModel = hiltViewModel()
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var commentText by remember { mutableStateOf("") }

    val comments = viewModel.commentsMap.collectAsState().value[postId] ?: emptyList()
    val commentEvent by viewModel.commentEvent.collectAsState()
    val context = LocalContext.current

    var replyingToComment by remember { mutableStateOf<CommentResponse?>(null) } // Thêm trạng thái cho bình luận cha

    // Tải bình luận khi bottom sheet được hiển thị
    LaunchedEffect (postId) {
        viewModel.loadComments(postId)
    }

    // Hiện Toast khi có lỗi
    LaunchedEffect(commentEvent) {
        if (commentEvent is CommentViewModel.CommentEvent.Error) {
            Toast.makeText(
                context,
                (commentEvent as CommentViewModel.CommentEvent.Error).message,
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        containerColor = Color.White,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 4.dp)
                    .width(36.dp)
                    .height(4.dp)
                    .background(Color.Gray.copy(alpha = 0.4f), CircleShape)
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight(0.9f)
                .background(Color.White)
        ) {
            // Top bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("Bình luận", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.align(Alignment.CenterEnd)
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Gửi")
                }
            }

            // Column chứa danh sách comment và input
            Column (modifier = Modifier.fillMaxSize()) {
                // Comment list scrollable
                CommentList(
                    comments = comments,
                    modifier = Modifier.weight(1f),
                    onReplyClick = { parentComment -> replyingToComment = parentComment } // Thêm callback

                )

                // Input section cố định dưới
                CommentInputSection(
                    commentText = commentText,
                    onCommentTextChange = { commentText = it },
                    onSendClick = {
                        if (commentText.isNotBlank()) {
                            viewModel.addComment(
                                postId,
                                commentText,
                                parentId = replyingToComment?.commentId
                            )
                            commentText = ""
                            replyingToComment = null
                        }
                    },
                    replyingTo = replyingToComment, // Truyền thông tin bình luận cha vào Input Section
                    onCancelReply = { replyingToComment = null },
                    modifier = Modifier
                        .fillMaxWidth()
                )
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CommentList(
    comments: List<CommentResponse>,
    modifier: Modifier = Modifier,
    onReplyClick: (CommentResponse) -> Unit
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        items(comments) { comment ->
            CommentItem(comment, onReplyClick = onReplyClick)
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CommentItem(
    comment: CommentResponse,
    onReplyClick: (CommentResponse) -> Unit
) {
    val startPadding = if (comment.parentId != null) 48.dp else 0.dp

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = startPadding, top = 4.dp, bottom = 4.dp),
        verticalAlignment = Alignment.Top
    ) {
        AsyncImage(
            model = comment.avatar,
            contentDescription = "Avatar",
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
        )

        Spacer(modifier = Modifier.width(8.dp))

        Column(modifier = Modifier.weight(1f)) {
            // Username + content
            Text(
                text = comment.username,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = comment.content,
                fontSize = 14.sp,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(4.dp))
            // Time + Reply
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = formatTimeAgo(comment.createdAt),
                    fontSize = 12.sp,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "Trả lời",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Gray,
                    modifier = Modifier.clickable { onReplyClick(comment) }
                )
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Like icon
        IconButton(
            onClick = { /* TODO: like */ },
            modifier = Modifier.size(24.dp)
        ) {
            Icon(
                painterResource(id = R.drawable.ic_tymm),
                contentDescription = "Like",
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
fun CommentInputSection(
    commentText: String,
    onCommentTextChange: (String) -> Unit,
    onSendClick: () -> Unit,
    modifier: Modifier = Modifier,
    replyingTo: CommentResponse?,
    onCancelReply: () -> Unit
) {
    Column(modifier = modifier.background(Color.White)) {
        // UI trả lời
        if (replyingTo != null) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF0F0F0))
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Đang trả lời ${replyingTo.username}", color = Color.Gray)
                Spacer(modifier = Modifier.weight(1f))
                IconButton(onClick = onCancelReply) {
                    Icon(Icons.Default.Close, contentDescription = "Hủy", tint = Color.Gray)
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 6.dp)
                .background(Color(0xFFF2F2F2), shape = CircleShape)
                .padding(horizontal = 12.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_register_logo),
                contentDescription = "User Avatar",
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
            )
            Spacer(modifier = Modifier.width(8.dp))
            TextField(
                value = commentText,
                onValueChange = onCommentTextChange,
                placeholder = {
                    Text(
                        text = if (replyingTo != null) "Trả lời ${replyingTo.username}..." else "Thêm bình luận...",
                        color = Color.Gray
                    )
                },
                modifier = Modifier.weight(1f),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    cursorColor = Color.Black,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                maxLines = 1,
                shape = CircleShape
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Gửi",
                color = if (commentText.isNotBlank()) Color(0xFF0095F6) else Color.Gray,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable(enabled = commentText.isNotBlank()) { onSendClick() }
            )
        }
    }
}

