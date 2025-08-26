package com.codewithngoc.instagallery.ui.features.profile

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.codewithngoc.instagallery.ui.features.profile.settings.logout.LogOutViewModel
import com.codewithngoc.instagallery.ui.navigation.Screen

@Composable
fun LogOutScreen(
    navController: NavController,
    viewModel: LogOutViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.navigationEvent.collect {
            // Điều hướng về Login
            navController.navigate(Screen.Auth.route) {
                popUpTo(Screen.HomeFeed.route) { inclusive = true }
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            when(uiState) {
                is LogOutViewModel.LogOutEvent.Loading -> {
                    CircularProgressIndicator()
                }
                is LogOutViewModel.LogOutEvent.Error -> {
                    Text(
                        text = (uiState as LogOutViewModel.LogOutEvent.Error).message,
                        color = Color.Red
                    )
                }
                else -> {
                    Button(onClick = { viewModel.logout() }) {
                        Text("Đăng xuất")
                    }
                }
            }
        }
    }
}
