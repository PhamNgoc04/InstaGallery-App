package com.codewithngoc.instagallery.data.model

import com.google.gson.annotations.SerializedName

data class UploadResponse (
    @SerializedName("url")
    val url: String
)