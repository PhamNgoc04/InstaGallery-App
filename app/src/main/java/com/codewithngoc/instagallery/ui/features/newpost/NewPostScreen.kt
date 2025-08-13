package com.codewithngoc.instagallery.ui.features.newpost

import android.Manifest
import android.content.ContentResolver
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.codewithngoc.instagallery.R
import com.codewithngoc.instagallery.ui.navigation.Screen
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun NewPostScreen(navController: NavController) {
    val context = LocalContext.current
    var mediaUris by remember { mutableStateOf<List<Uri>>(emptyList()) }
    var selectedUri by remember { mutableStateOf<Uri?>(null) }

    val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        Manifest.permission.READ_MEDIA_IMAGES
    } else {
        Manifest.permission.READ_EXTERNAL_STORAGE
    }

    val permissionState = rememberPermissionState(permission = permission)

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                mediaUris = getMediaUris(context.contentResolver)
                selectedUri = mediaUris.firstOrNull()
            }
        }
    )

    LaunchedEffect(Unit) {
        galleryLauncher.launch(permission)
    }


    Scaffold(
        topBar = { NewPostTopBar(navController, selectedUri) },
        containerColor = Color.Black
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            if (selectedUri != null) {
                SelectedMediaSection(selectedUri = selectedUri)
            } else {
                // Placeholder if no image is selected
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(350.dp)
                        .background(Color.DarkGray)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            MediaGalleryHeader()

            if (permissionState.status.isGranted) {
                MediaGallery(
                    mediaUris = mediaUris,
                    selectedUri = selectedUri,
                    onMediaSelected = { uri -> selectedUri = uri }
                )
            } else {
                // Show a message or a button to request permission again
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "Cần quyền truy cập thư viện để hiển thị ảnh.", color = Color.Gray)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewPostTopBar(navController: NavController,  selectedUri: Uri?) {
    TopAppBar(
        title = {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text("Bài viết mới")
            }
        },
        navigationIcon = {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.Default.Close, contentDescription = "Close")
            }
        },
        actions = {
            Text(
                text = "Tiếp",
                color = if (selectedUri != null) MaterialTheme.colorScheme.primary else Color.Gray,
                modifier = Modifier
                    .padding(end = 16.dp)
                    .clickable(enabled = selectedUri != null) {
                        selectedUri?.let {
                            navController.navigate(Screen.EditPost.createRoute(Uri.encode(it.toString())))
                        }
                    }
            )
        },
        colors = TopAppBarDefaults.smallTopAppBarColors(
            containerColor = Color.Black.copy(alpha = 0.85f),
            titleContentColor = Color.White,
            navigationIconContentColor = Color.White,
            actionIconContentColor = MaterialTheme.colorScheme.primary
        )
    )
}

@Composable
fun SelectedMediaSection(selectedUri: Uri?) {
    Surface (
        modifier = Modifier
            .fillMaxWidth()
            .height(350.dp),
        shape = MaterialTheme.shapes.medium,
        shadowElevation = 8.dp,
        color = Color.Black
    ) {
        if (selectedUri != null) {
            AsyncImage(
                model = selectedUri,
                contentDescription = "Selected Image",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit
            )
        } else {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "Vui lòng chọn ảnh.", color = Color.Gray)
            }
        }
    }
}

@Composable
fun MediaGalleryHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "Gần đây",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Icon(
                painter = painterResource(id = R.drawable.ic_dropdown),
                contentDescription = "Dropdown",
                tint = Color.White,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
        IconButton(onClick = { /* TODO: mở dialog chọn bộ lọc media */ }) {
            Icon(
                painter = painterResource(id = R.drawable.ic_filter),
                contentDescription = "Filter",
                tint = Color.White
            )
        }
    }
}

@Composable
fun MediaGallery(mediaUris: List<Uri>,selectedUri: Uri?, onMediaSelected: (Uri) -> Unit) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        items(mediaUris) { uri ->
            val isSelected = uri == selectedUri
            Box (
                modifier = Modifier
                    .aspectRatio(1f)
                    .clickable { onMediaSelected(uri) }
                    .border(
                        width = if (isSelected) 3.dp else 0.dp,
                        color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent
                    )

            ) {
                AsyncImage(
                    model = uri,
                    contentDescription = "Gallery Image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

// Utility function to get all image URIs from the device
private fun getMediaUris(contentResolver: ContentResolver): List<Uri> {
    val imageUris = mutableListOf<Uri>()
    val projection = arrayOf(MediaStore.Images.Media._ID)
    val query = contentResolver.query(
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
        projection,
        null,
        null,
        MediaStore.Images.Media.DATE_ADDED + " DESC" // Sort by date added
    )
    query?.use { cursor ->
        val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
        while (cursor.moveToNext()) {
            val id = cursor.getLong(idColumn)
            val contentUri = Uri.withAppendedPath(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                id.toString()
            )
            imageUris.add(contentUri)
        }
    }
    return imageUris
}

// Utility function to generate mock URIs for the preview
private fun createMockMediaUris(count: Int): List<Uri> {
    val mockUris = mutableListOf<Uri>()
    for (i in 0 until count) {
        // Using a placeholder URL as a mock URI. This won't work on a real device,
        // but it's sufficient for the Coil library to display in the preview.
        mockUris.add(Uri.parse("https://picsum.photos/id/${1000 + i}/200/200"))
    }
    return mockUris
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun NewPostScreenPreview() {
    val navController = rememberNavController()

    // Create mock data for the preview
    val mockMediaUris = createMockMediaUris(50)
    val selectedMockUri = mockMediaUris.firstOrNull()

    // The Preview composable will render the NewPostScreen with mock data.
    // It's important to provide a mock state for each dependency the screen has.
    Scaffold(
        topBar = {
            NewPostTopBar(navController = navController, selectedUri = selectedMockUri)
        },
        containerColor = Color.Black
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            if (selectedMockUri != null) {
                SelectedMediaSection(selectedUri = selectedMockUri)
            }

            Spacer(modifier = Modifier.height(8.dp))

            MediaGalleryHeader()

            MediaGallery(
                mediaUris = mockMediaUris,
                selectedUri = selectedMockUri,
                onMediaSelected = { /* no-op in preview */ }
            )
        }
    }
}