package com.fido.newappassesment.data.api

import com.fido.newappassesment.data.models.Article
import com.fido.newappassesment.data.models.NewsSource
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsApiService {
    @GET("v2/top-headlines/sources")
    suspend fun getSources(
        @Query("apiKey") apiKey: String,
        @Query("category") category: String? = null,
        @Query("language") language: String? = null,
        @Query("country") country: String? = null
    ): SourcesResponse

    @GET("v2/top-headlines")
    suspend fun getArticles(
        @Query("apiKey") apiKey: String,
        @Query("sources") sources: String
    ): ArticlesResponse
}

data class SourcesResponse(
    val status: String,
    val sources: List<NewsSource>
)

data class ArticlesResponse(
    val status: String,
    val articles: List<Article>
)