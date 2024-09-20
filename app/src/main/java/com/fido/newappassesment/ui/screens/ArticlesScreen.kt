package com.fido.newappassesment.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.fido.newappassesment.data.models.Article
import com.fido.newappassesment.ui.components.LoadingIndicator

@Composable
fun ArticlesList(
    articles: List<Article>,
    onArticleClick: (Article) -> Unit,
    isRefreshing: Boolean
) {
    LazyColumn {
        if (isRefreshing) {
            item { LoadingIndicator() }
        }
        items(articles) { article ->
            ArticleItem(article = article, onClick = { onArticleClick(article) })
        }
    }
}

@Composable
fun ArticleItem(article: Article, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable(onClick = onClick)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = article.title, style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = article.description, style = MaterialTheme.typography.bodyMedium)
        }
    }
}