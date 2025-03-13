package com.example.feedarticlesjetpackcompose.network


import com.example.feedarticlesjetpackcompose.network.dtosResponse.NewArticleDto
import com.example.feedarticlesjetpackcompose.network.dtosResponse.RegisterDto
import com.example.feedarticlesjetpackcompose.network.dtosResponse.UpdateArticleDto
import com.example.feedarticlesjetpackcompose.network.dtosResponse.ArticleDto
import com.example.feedarticlesjetpackcompose.network.dtosResponse.AuthResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path


interface ApiService {

    @PUT(ApiRoutes.USER)
    suspend fun register(
        @Body registerDto: RegisterDto
    ): Response<AuthResponse>?

    @FormUrlEncoded
    @POST(ApiRoutes.USER)
    suspend fun login(
        @Field("login") login: String,
        @Field("mdp") password: String,
    ): Response<AuthResponse>?

    @GET(ApiRoutes.ARTICLES)
    suspend fun getAllArticles(
        @Header("token") token: String
    ) : Response<List<ArticleDto>>?

    @GET(ApiRoutes.ARTICLES+"{id}")
    suspend fun getArticle(
        @Path("id") idArticle: Long,
        @Header("token") token: String
    ): Response<ArticleDto>?

    @POST(ApiRoutes.ARTICLES+"/{id}")
    suspend fun updateArticle(
        @Path("id") idArticle: Long,
        @Header("token") token: String,
        @Body updateArticleDto: UpdateArticleDto
    ): Response<Unit>?

    @DELETE(ApiRoutes.ARTICLES+"{id}")
    suspend fun deleteArticle(
        @Path("id") idArticle: Long,
        @Header("token") token: String,
    ): Response<Unit>?

    @PUT(ApiRoutes.ARTICLES)
    suspend fun createArticle(
        @Header("token") token: String,
        @Body newArticleDto: NewArticleDto
    ): Response<Unit>?
}
