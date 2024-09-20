package com.fido.newappassesment.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fido.newappassesment.data.models.Article
import com.fido.newappassesment.data.models.NewsSource
import com.fido.newappassesment.data.repository.NewsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewsViewModel @Inject constructor(
    private val newsRepository: NewsRepository
) : ViewModel() {

    private val _sourcesState = MutableStateFlow<SourcesState>(SourcesState.Loading)
    val sourcesState: StateFlow<SourcesState> = _sourcesState.asStateFlow()

    private val _articlesState = MutableStateFlow<ArticlesState>(ArticlesState.Initial)
    val articlesState: StateFlow<ArticlesState> = _articlesState.asStateFlow()

    private val _selectedTabIndex = MutableStateFlow(0)
    val selectedTabIndex: StateFlow<Int> = _selectedTabIndex.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    init {
        loadSources()
    }

    fun loadSources() {
        viewModelScope.launch {
            _sourcesState.value = SourcesState.Loading
            try {
                val sources = newsRepository.getSources()
                _sourcesState.value = SourcesState.Success(sources)
                if (sources.isNotEmpty()) {
                    loadArticles(sources.first().id)
                }
            } catch (e: Exception) {
                _sourcesState.value = SourcesState.Error(e.message ?: "Unknown error occurred")
            }
        }
    }

    fun loadArticles(sourceId: String) {
        viewModelScope.launch {
            _articlesState.value = ArticlesState.Loading
            try {
                val articles = newsRepository.getArticles(sourceId)
                _articlesState.value = ArticlesState.Success(articles)
            } catch (e: Exception) {
                _articlesState.value = ArticlesState.Error(e.message ?: "Unknown error occurred")
            }
        }
    }

    fun refreshSources() {
        viewModelScope.launch {
            _isRefreshing.value = true
            try {
                newsRepository.refreshSources()
                loadSources()
            } catch (e: Exception) {
                _sourcesState.value = SourcesState.Error(e.message ?: "Failed to refresh sources")
            } finally {
                _isRefreshing.value = false
            }
        }
    }

    fun refreshArticles(sourceId: String) {
        viewModelScope.launch {
            _isRefreshing.value = true
            try {
                newsRepository.refreshArticles(sourceId)
                loadArticles(sourceId)
            } catch (e: Exception) {
                _articlesState.value = ArticlesState.Error(e.message ?: "Failed to refresh articles")
            } finally {
                _isRefreshing.value = false
            }
        }
    }

    fun setSelectedTabIndex(index: Int) {
        _selectedTabIndex.value = index
        (sourcesState.value as? SourcesState.Success)?.sources?.getOrNull(index)?.let { source ->
            loadArticles(source.id)
        }
    }
}

sealed class SourcesState {
    object Loading : SourcesState()
    data class Success(val sources: List<NewsSource>) : SourcesState()
    data class Error(val message: String) : SourcesState()
}

sealed class ArticlesState {
    object Initial : ArticlesState()
    object Loading : ArticlesState()
    data class Success(val articles: List<Article>) : ArticlesState()
    data class Error(val message: String) : ArticlesState()
}
