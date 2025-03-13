package com.example.feedarticlesjetpackcompose.network.dtosResponse

import com.squareup.moshi.Json

data class AuthResponse(
    @Json(name = "id")
    val id: Long,
    @Json(name = "token")
    val token: String
)