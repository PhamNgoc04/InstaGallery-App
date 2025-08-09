package com.codewithngoc.instagallery.ui.floatingLabelTextField

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import androidx.compose.ui.unit.sp
import com.codewithngoc.instagallery.R
import androidx.compose.ui.res.vectorResource

@Composable
fun FloatingLabelTextField(
    value: String,
    onValueChange: (String) -> Unit,
    onFocusChanged : (Boolean) -> Unit = {},
    label: String,
    modifier: Modifier = Modifier,
    isPassword: Boolean = false,
    passwordVisible: Boolean = false,
    onPasswordVisibilityChange: () -> Unit = {},
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Done,
    onImeAction: () -> Unit = {},
    isError: Boolean = false,
    errorMessage: String? = null
) {
    var isFocused by remember { mutableStateOf(false) }

    val transition by animateFloatAsState(
        targetValue = if (isFocused || value.isNotEmpty()) 1f else 0f,
        label = "LabelTransition"
    )

    Box(
        modifier = modifier
            .height(64.dp)
            .border(
                width = 1.dp,
                color = when {
                    isError -> Color.Red
                    isFocused -> Color.Black
                    else -> Color(0xFFDBDBDB)
                },
                shape = RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        // Label
        Text(
            text = label,
            color = if (isFocused) Color.Black else Color.Gray,
            fontSize = lerp(14.sp, 12.sp, transition),
            fontWeight = FontWeight.Medium,
            modifier = Modifier
                .align(
                    // Căn giữa theo chiều dọc khi chưa focus
                    if (isFocused || value.isNotEmpty()) Alignment.TopStart else Alignment.CenterStart
                )
                .offset(y = lerp(0.dp, (-8).dp, transition)) // Điều chỉnh offset để căn giữa
        )

        Row(
            modifier = modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Text field
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                singleLine = true,
                textStyle = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 18.sp,
                    color = Color.Black
                ),
                keyboardOptions = KeyboardOptions(
                    keyboardType = keyboardType,
                    imeAction = imeAction
                ),
                keyboardActions = KeyboardActions (onAny = { onImeAction() }),
                visualTransformation = if (isPassword && !passwordVisible) PasswordVisualTransformation() else VisualTransformation.None,
                modifier = Modifier
                    .weight(1f)
                    .padding(top = if (isFocused || value.isNotEmpty()) 15.dp else 15.dp) // Chỉnh sửa focus và icon tại đây
                    .onFocusChanged { forcusState ->
                        isFocused = forcusState.isFocused
                        onFocusChanged(forcusState.isFocused)
                    }
            )

            if (isPassword) {
                Icon(
                    painter = painterResource(id = if (passwordVisible) R.drawable.ic_hienmatkhau else R.drawable.ic_anmatkhau),
                    contentDescription = if (passwordVisible) "Ẩn mật khẩu" else "Hiện mật khẩu",
                    tint = if (isFocused) Color.Unspecified else Color.Unspecified, // Màu sắc mặc định
                    modifier = Modifier
                        .size(24.dp)
                        .align(Alignment.CenterVertically)
                        .clickable { onPasswordVisibilityChange() },
                )
            }
        }
    }

    // Hiển thị thông báo lỗi
    if (isError && !errorMessage.isNullOrEmpty()) {
        Text(
            text = errorMessage,
            color = Color.Red,
            fontSize = 12.sp,
            textAlign = TextAlign.Left,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, top = 4.dp)
        )
    }
}


@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF, showSystemUi = true)
@Composable
fun FloatingLabelTextFieldPreview() {
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    FloatingLabelTextField(
        value = password,
        onValueChange = { password = it },
        label = "Password",
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        isPassword = false,
        passwordVisible = passwordVisible,
        onPasswordVisibilityChange = { passwordVisible = !passwordVisible },
        keyboardType = KeyboardType.Email,
        imeAction = ImeAction.Next
    )
}