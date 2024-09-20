package com.fido.newappassesment.data.repository

import com.fido.newappassesment.BuildConfig
import com.fido.newappassesment.data.api.NewsApiService
import com.fido.newappassesment.data.models.Article
import com.fido.newappassesment.data.models.NewsSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class NewsRepositoryImpl @Inject constructor(
    private val newsApiService: NewsApiService,
)  : NewsRepository {

    // only in-memory cache
    private var cachedSources: List<NewsSource>? = null
    private var cachedArticles: Map<String, List<Article>> = mutableMapOf()

    override suspend fun refreshArticles(sourceId: String) {
        (cachedArticles as MutableMap)[sourceId] = fetchArticles(sourceId)
    }

    private suspend fun fetchSources(
        category: String?,
        language: String?,
        country: String?
    ): List<NewsSource> = withContext(Dispatchers.IO) {
        newsApiService.getSources(BuildConfig.NEWS_API_KEY, category, language, country).sources
    }

    private suspend fun fetchArticles(sourceId: String): List<Article> = withContext(Dispatchers.IO) {
        newsApiService.getArticles(BuildConfig.NEWS_API_KEY, sourceId).articles
    }

    override suspend fun getSources(
        category: String?,
        language: String?,
        country: String?
    ): List<NewsSource> {
        return cachedSources ?: fetchSources(category, language, country).also {
            cachedSources = it
        }
    }

    override suspend fun getArticles(sourceId: String): List<Article> {
        return cachedArticles[sourceId] ?: fetchArticles(sourceId).also {
            (cachedArticles as MutableMap)[sourceId] = it
        }
    }

    override suspend fun refreshSources(category: String?, language: String?, country: String?) {
        cachedSources = fetchSources(category, language, country)
    }
}