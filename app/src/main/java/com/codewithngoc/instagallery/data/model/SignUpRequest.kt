package com.codewithngoc.instagallery.data.model

import com.google.gson.annotations.SerializedName

data class SignUpRequest(
    @SerializedName("username") val username: String,
    @SerializedName("fullName") val fullName: String,
    @SerializedName("email") val email: String,
    @SerializedName("passwordHash") val password: String
)