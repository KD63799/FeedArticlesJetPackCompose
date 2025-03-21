package com.example.feedarticlesjetpackcompose.network.dtosResponse

import com.squareup.moshi.Json

data class ArticleDto(
    @Json(name = "id")
    val id: Long,
    @Json(name = "titre")
    val titre: String,
    @Json(name = "descriptif")
    val descriptif: String,
    @Json(name = "url_image")
    val urlImage: String,
    @Json(name = "categorie")
    val categorie: Int,
    @Json(name = "created_at")
    val createdAt: String,
    @Json(name = "id_u")
    val idU: Long
)
