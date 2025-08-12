package com.codewithngoc.instagallery.data.utils

import android.content.Context
import android.net.Uri
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream

fun Context.uriToMultipart(partName: String, uri: Uri): MultipartBody.Part {
    val inputStream = contentResolver.openInputStream(uri)
        ?: throw IllegalArgumentException("Không thể đọc file từ Uri")
    val tempFile = File(cacheDir, "upload_${System.currentTimeMillis()}.jpg")
    FileOutputStream(tempFile).use { outputStream ->
        inputStream.copyTo(outputStream)
    }
    val requestBody = tempFile.readBytes()
        .toRequestBody("image/*".toMediaTypeOrNull())
    return MultipartBody.Part.createFormData(partName, tempFile.name, requestBody)
}
