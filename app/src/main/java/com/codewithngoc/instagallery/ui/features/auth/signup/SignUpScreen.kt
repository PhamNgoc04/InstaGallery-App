package com.codewithngoc.instagallery.ui.features.auth.signup

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.codewithngoc.instagallery.R
import com.codewithngoc.instagallery.ui.floatingLabelTextField.FloatingLabelTextField
import com.codewithngoc.instagallery.ui.navigation.Screen
import kotlinx.coroutines.flow.collectLatest

@Composable
fun SignUpScreen(
    navController: NavController,
    viewModel: SignUpViewModel = hiltViewModel(),
) {
    val context = LocalContext.current

    val name by viewModel.username.collectAsStateWithLifecycle()
    val fullName by viewModel.fullName.collectAsStateWithLifecycle()
    val email by viewModel.email.collectAsStateWithLifecycle()
    val password by viewModel.password.collectAsStateWithLifecycle()
    val confirmPassword by viewModel.confirmPassword.collectAsStateWithLifecycle()

    val nameError by viewModel.usernameError.collectAsStateWithLifecycle()
    val fullNameError by viewModel.fullNameError.collectAsStateWithLifecycle()
    val emailError by viewModel.emailError.collectAsStateWithLifecycle()
    val passwordError by viewModel.passwordError.collectAsStateWithLifecycle()
    val confirmPasswordError by viewModel.confirmPasswordError.collectAsStateWithLifecycle()

    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    val uiState by viewModel.uiState.collectAsState()
    val loading = uiState is SignUpViewModel.SignUpEvent.Loading

    val errorMessage = remember(uiState) {
        (uiState as? SignUpViewModel.SignUpEvent.Error)?.message
    }

    // Điều hướng
    LaunchedEffect(Unit) {
        viewModel.navigationEvent.collectLatest { event ->
            when (event) {
                is SignUpViewModel.SignUpNavigationEvent.NavigateToHome -> {
                    navController.navigate(Screen.HomeFeed.route) {
                        popUpTo(Screen.Auth.route) { inclusive = true }
                    }
                }
                is SignUpViewModel.SignUpNavigationEvent.NavigateToLogin -> {
                    navController.navigate(Screen.Login.route)
                }
            }
        }
    }

    // Hiển thị lỗi
    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Logo
        Icon(
            painter = painterResource(id = R.drawable.ic_register_logo),
            contentDescription = null,
            modifier = Modifier
                .size(80.dp)
                .padding(bottom = 16.dp)
        )

        Text(
            text = stringResource(R.string.register),
            fontSize = 24.sp,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        FloatingLabelTextField(
            value = name,
            onValueChange = viewModel::onNameChange,
            label = "Username",
            modifier = Modifier.fillMaxWidth(),
            isPassword = false,
            isError = nameError != null,
            errorMessage = nameError,
            keyboardType = KeyboardType.Text
        )
        Spacer(modifier = Modifier.height(16.dp))

        FloatingLabelTextField(
            value = fullName,
            onValueChange = viewModel::onFullNameChange,
            label = "Full Name",
            modifier = Modifier.fillMaxWidth(),
            isPassword = false,
            isError = fullNameError != null,
            errorMessage = fullNameError,
            keyboardType = KeyboardType.Text
        )
        Spacer(modifier = Modifier.height(16.dp))

        FloatingLabelTextField(
            value = email,
            onValueChange = viewModel::onEmailChange,
            label = "Email",
            modifier = Modifier.fillMaxWidth(),
            isPassword = false,
            isError = emailError != null,
            errorMessage = emailError,
            keyboardType = KeyboardType.Text
        )
        Spacer(modifier = Modifier.height(16.dp))

        FloatingLabelTextField(
            value = password,
            onValueChange = viewModel::onPasswordChange,
            label = "Password",
            modifier = Modifier.fillMaxWidth(),
            isPassword = true,
            isError = passwordError != null,
            errorMessage = passwordError,
            passwordVisible = passwordVisible,
            onPasswordVisibilityChange = { passwordVisible = !passwordVisible },
            keyboardType = KeyboardType.Text
        )
        Spacer(modifier = Modifier.height(16.dp))

        FloatingLabelTextField(
            value = confirmPassword,
            onValueChange = viewModel::onConfirmPasswordChange,
            label = "Confirm Password",
            modifier = Modifier.fillMaxWidth(),
            isPassword = true,
            isError = confirmPasswordError != null,
            errorMessage = confirmPasswordError,
            passwordVisible = confirmPasswordVisible,
            onPasswordVisibilityChange = { confirmPasswordVisible = !confirmPasswordVisible },
            keyboardType = KeyboardType.Text
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Button đăng ký
        OutlinedButton(
            onClick = { viewModel.onSignUpClick() },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, Color(0xFFDBDBDB)), // màu xám nhạt như Instagram
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = Color.White,
                contentColor = colorResource(R.color.orange_button_login)
            ),
            contentPadding = PaddingValues(vertical = 14.dp)
        ){
            if (loading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = colorResource(R.color.white),
                    strokeWidth = 2.dp
                )
            } else {
                Text(text = stringResource(R.string.register))
            }
        }

        if (!errorMessage.isNullOrBlank()) {
            Text(
                text = errorMessage,
                color = Color.Red,
                fontSize = 14.sp,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        // Điều hướng đến Login
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.have_an_account),
                fontSize = 14.sp,
                modifier = Modifier.alpha(0.6f)
            )
            TextButton(
                onClick = {
                    navController.navigate(Screen.Login.route)
                }
            ) {
                Text(
                    text = stringResource(R.string.login),
                    fontSize = 14.sp,
                    color = colorResource(R.color.orange_button_login)
                )
            }
        }
    }
}





























