package com.codewithngoc.instagallery.data.model

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class UploadResponse (
    @SerializedName("url")
    val url: String
)