package com.fido.newappassesment.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.fido.newappassesment.data.models.Article
import com.fido.newappassesment.ui.components.ErrorMessage
import com.fido.newappassesment.ui.components.LoadingIndicator
import com.fido.newappassesment.ui.viewmodel.ArticlesState
import com.fido.newappassesment.ui.viewmodel.NewsViewModel
import com.fido.newappassesment.ui.viewmodel.SourcesState

// TODO: remove comments
// ensure that the sources and articles are refreshed when the screen resumes (e.g., when coming
// back from another screen or when the app returns to the foreground).
@Composable
fun LifecycleAwareMainScreen(
    viewModel: NewsViewModel = hiltViewModel(),
    onArticleSelected: (Article) -> Unit
) {
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    // DisposableEffect observe the lifecycle of the composable:
    DisposableEffect(lifecycle) {
        // create a lifecycle observer inside the effect to observe the ON_RESUME event
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                // Refreshes the sources:
                viewModel.refreshSources()
                // Refresh articles for the currently selected source
                (viewModel.sourcesState.value as? SourcesState.Success)?.let { state ->
                    state.sources.getOrNull(viewModel.selectedTabIndex.value)?.let { source ->
                        viewModel.refreshArticles(source.id)
                    }
                }
            }
        }
        lifecycle.addObserver(observer)
        onDispose {
            lifecycle.removeObserver(observer)
        }
    }

    MainScreen(viewModel = viewModel, onArticleSelected = onArticleSelected)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: NewsViewModel = hiltViewModel(),
    onArticleSelected: (Article) -> Unit
) {
    val sourcesState by viewModel.sourcesState.collectAsState()
    val articlesState by viewModel.articlesState.collectAsState()
    val selectedTabIndex by viewModel.selectedTabIndex.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.loadSources()
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("News App") }) },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            when (val state = sourcesState) {
                is SourcesState.Loading -> LoadingIndicator()
                is SourcesState.Error -> {
                    ErrorMessage(state.message)
                    LaunchedEffect(state) {
                        showSnackbar(snackbarHostState, "Error loading sources: ${state.message}")
                    }
                }
                is SourcesState.Success -> {
                    ScrollableTabRow(
                        selectedTabIndex = selectedTabIndex,
                        edgePadding = 0.dp
                    ) {
                        state.sources.forEachIndexed { index, source ->
                            Tab(
                                selected = selectedTabIndex == index,
                                onClick = {
                                    viewModel.setSelectedTabIndex(index)
                                    viewModel.loadArticles(source.id)
                                },
                                text = { Text(source.name) }
                            )
                        }
                    }

                    when (val articleState = articlesState) {
                        is ArticlesState.Initial -> Text("Select a source to see articles")
                        is ArticlesState.Loading -> LoadingIndicator()
                        is ArticlesState.Error -> {
                            ErrorMessage(articleState.message)
                            LaunchedEffect(articleState) {
                                showSnackbar(snackbarHostState, "Error loading articles: ${articleState.message}")
                            }
                        }
                        is ArticlesState.Success -> {
                            ArticlesList(
                                articles = articleState.articles,
                                onArticleClick = onArticleSelected,
                                isRefreshing = isRefreshing,
                            )
                        }
                    }
                }
            }
        }
    }
}

suspend fun showSnackbar(snackbarHostState: SnackbarHostState, message: String) {
    snackbarHostState.showSnackbar(message)
}