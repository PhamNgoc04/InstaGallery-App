package com.codewithngoc.instagallery.ui.features.auth.login

import android.widget.Toast
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.codewithngoc.instagallery.R
import com.codewithngoc.instagallery.ui.floatingLabelTextField.FloatingLabelTextField
import com.codewithngoc.instagallery.ui.navigation.Screen
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignInScreen(
    navController: NavController,
    viewModel: SignInViewModel = hiltViewModel()
) {

    Box(modifier = Modifier.fillMaxSize()) {

        // Thu thập trạng thái UI từ ViewModel
        val email by viewModel.email.collectAsStateWithLifecycle()
        val password by viewModel.password.collectAsStateWithLifecycle()

        val emailError by viewModel.emailError.collectAsStateWithLifecycle()
        val passwordError by viewModel.passwordError.collectAsStateWithLifecycle()

        var passwordVisible by remember { mutableStateOf(false) }

        val uiState by viewModel.uiState.collectAsState()

        val loading = remember(uiState) {
            uiState is SignInViewModel.SignInEvent.Loading
        }

        val errorMessage = remember(uiState) {
            when (uiState) {
                is SignInViewModel.SignInEvent.Error -> (uiState as SignInViewModel.SignInEvent.Error).message
                else -> null
            }
        }

        val context = LocalContext.current
        LaunchedEffect(Unit) {
            viewModel.navigationEvent.collectLatest { event ->
                when (event) {
                    is SignInViewModel.SignInNavigationEvent.NavigateToHome -> {
                        navController.navigate((Screen.Home.route)) {
                            popUpTo(Screen.Auth.route) {
                                inclusive = true
                            }
                            launchSingleTop = true
                        }
                    }

                    is SignInViewModel.SignInNavigationEvent.NavigateToSignUp -> {
                        navController.navigate(Screen.SignUp.route)
                    }
                }
            }
        }

        // Hiển thị thông báo lỗi nếu có
        LaunchedEffect(uiState) {
            if (uiState is SignInViewModel.SignInEvent.Error) {
                val message = (uiState as SignInViewModel.SignInEvent.Error).message
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 80.dp, start = 24.dp, end = 24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Tên ứng dụng
            Text(
                text = "InstaGallery",
                fontSize = 36.sp,
                fontWeight = FontWeight.ExtraBold,
                color = colorResource(R.color.orange_button_login),
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            val labelTextStyle = MaterialTheme.typography.bodySmall.copy(
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )

            FloatingLabelTextField(
                value = email,
                onValueChange = viewModel::onEmailChange,
                label = stringResource(R.string.email),
                modifier = Modifier.fillMaxWidth(),
                isError = emailError != null, // Truyền trạng thái lỗi
                errorMessage = emailError, // Truyền thông báo lỗi
                keyboardType = KeyboardType.Text
            )


            Spacer(modifier = Modifier.height(16.dp))

            FloatingLabelTextField(
                value = password,
                onValueChange = viewModel::onPasswordChange,
                label = stringResource(R.string.password),
                modifier = Modifier.fillMaxWidth(),
                isPassword = true,
                isError = passwordError != null, // Truyền trạng thái lỗi
                errorMessage = passwordError, // Truyền thông báo lỗi
                passwordVisible = passwordVisible,
                onPasswordVisibilityChange = { passwordVisible = !passwordVisible },
                keyboardType = KeyboardType.Text
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = {
                    viewModel::onSignInClick
                }) {
                    Text(
                        text = stringResource(R.string.forgot_password),
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedButton(
                onClick = { viewModel.onSignInClick() },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, Color(0xFFDBDBDB)), // màu xám nhạt như Instagram
                enabled = uiState !is SignInViewModel.SignInEvent.Loading, // disable khi loading
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = Color.White,
                    contentColor = colorResource(R.color.orange_button_login)
                ),
                contentPadding = PaddingValues(vertical = 14.dp)
            ){
                Text(
                    text = stringResource(R.string.login),
                    fontSize = 18.sp,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }

            if (!errorMessage.isNullOrBlank()) {
                Text(
                    text = errorMessage,
                    color = Color.Red,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.dont_have_account),
                    color = colorResource(R.color.black),
                    fontSize = 16.sp,
                    modifier = Modifier.alpha(0.5f)
                )

                Spacer(modifier = Modifier.width(5.dp))

                TextButton(
                    onClick = { }
                ) {
                    Text(
                        text = stringResource(R.string.register),
                        color = colorResource(R.color.orange_button_login),
                        fontSize = 16.sp
                    )
                }

            }
        }

        if (loading) {
            CircularProgressIndicator(
                modifier = Modifier
                    .size(48.dp)
                    .align(Alignment.Center),
                color = colorResource(R.color.orange_button_login)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SignInScreenPreview() {
    SignInScreen(navController = rememberNavController())
}






















