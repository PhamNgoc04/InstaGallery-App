package com.codewithngoc.instagallery.utils

import android.content.Context
import android.net.Uri
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream

object ImagePickerUtils {
    
    /**
     * Chuyển đổi Uri lấy từ thiết bị (Gallery) thành File 
     * để Repository tự tạo MultipartBody.Part chuyển lên Server.
     */
    fun uriToFile(context: Context, uri: Uri): File? {
        val inputStream = context.contentResolver.openInputStream(uri) ?: return null
        val tempFile = File.createTempFile("upload_", ".jpg", context.cacheDir)
        
        val outputStream = FileOutputStream(tempFile)
        inputStream.copyTo(outputStream)
        
        inputStream.close()
        outputStream.close()
        
        return tempFile
    }
}
