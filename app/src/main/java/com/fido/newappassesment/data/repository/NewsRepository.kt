package com.fido.newappassesment.data.repository

import com.fido.newappassesment.data.models.Article
import com.fido.newappassesment.data.models.NewsSource

interface NewsRepository {

    // local
    suspend fun getSources(
        category: String? = null,
        language: String? = null,
        country: String? = null
    ): List<NewsSource>

    suspend fun getArticles(sourceId: String): List<Article>

    // remote: these methods update the in-memory cache with fresh data from the API
    suspend fun refreshSources(
        category: String? = null,
        language: String? = null,
        country: String? = null
    )

    suspend fun refreshArticles(sourceId: String)
}