package com.codewithngoc.instagallery.ui.features.homefeed.postmorebottomsheet

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.codewithngoc.instagallery.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostMoreBottomSheet(
    onDismiss: () -> Unit,
    postId: Int
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        dragHandle = {
            // ... (Phần drag handle đã có) ...
        }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header: Lưu, Mã QR
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                MoreOptionButton(iconResId = R.drawable.ic_luu, text = "Lưu", onClick = {})
                MoreOptionButton(iconResId = R.drawable.ic_qrcode, text = "Mã QR", onClick = {})
            }
            Divider(modifier = Modifier.padding(vertical = 16.dp))

            // Danh sách các tùy chọn
            MoreOptionItem(iconResId = R.drawable.ic_star, text = "Thêm vào mục yêu thích", onClick = {})
            MoreOptionItem(iconResId = R.drawable.ic_unfollow, text = "Bỏ theo dõi", onClick = {})
            MoreOptionItem(iconResId = R.drawable.ic_tag, text = "Giới thiệu về tài khoản này", onClick = {})
            MoreOptionItem(iconResId = R.drawable.ic_info_outline, text = "Tại sao bạn nhìn thấy bài viết này", onClick = {})
            MoreOptionItem(iconResId = R.drawable.ic_hide, text = "Ẩn", onClick = {})
            MoreOptionItem(iconResId = R.drawable.ic_report, text = "Báo cáo", color = MaterialTheme.colorScheme.error, onClick = {})
        }
    }
}

@Composable
fun MoreOptionButton(iconResId: Int, text: String, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(8.dp)
    ) {
        Icon(painter = painterResource(id = iconResId), contentDescription = null, modifier = Modifier.size(36.dp))
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = text, fontSize = 12.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun MoreOptionItem(iconResId: Int, text: String, color: androidx.compose.ui.graphics.Color = Color.Black, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(painter = painterResource(id = iconResId), contentDescription = null, modifier = Modifier.size(24.dp), tint = color)
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = text, fontSize = 16.sp, color = color)
    }
}