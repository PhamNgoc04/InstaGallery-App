package com.codewithngoc.instagallery.data.model

import com.google.gson.annotations.SerializedName

data class SignInRequest(
    @SerializedName("email") val email: String,
    @SerializedName("passwordHash") val password: String
)