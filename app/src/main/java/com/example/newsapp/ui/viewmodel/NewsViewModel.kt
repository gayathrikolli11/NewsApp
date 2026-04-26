package com.example.newsapp.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.newsapp.BuildConfig
import com.example.newsapp.data.FavoritesManager
import com.example.newsapp.data.model.Article
import com.example.newsapp.data.remote.RetrofitInstance
import com.example.newsapp.data.repository.NewsRepository
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.logEvent
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
class NewsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = NewsRepository(RetrofitInstance.api)
    private val favoritesManager = FavoritesManager(application)

    private val analytics: FirebaseAnalytics by lazy {
        FirebaseAnalytics.getInstance(application)
    }

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    val articles: Flow<PagingData<Article>> = _searchQuery
        .debounce(300)
        .distinctUntilChanged()
        .flatMapLatest { query ->
            repository.getHeadlines(
                apiKey = BuildConfig.NEWS_API_KEY,
                query = query.ifBlank { null }
            )
        }
        .cachedIn(viewModelScope)

    val favorites: Flow<List<Article>> = favoritesManager.favorites

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
        if (query.isNotBlank()) {
            analytics.logEvent("search_query") {
                param("query", query)
            }
        }
    }

    fun toggleFavorite(article: Article, isFavorite: Boolean) {
        viewModelScope.launch {
            if (isFavorite) {
                favoritesManager.removeFavorite(article)
            } else {
                favoritesManager.addFavorite(article)
            }
        }
    }

    fun logArticleClick(title: String, source: String) {
        analytics.logEvent("article_click") {
            param("article_title", title)
            param("article_source", source)
        }
    }

    fun logOpenArticle(url: String, source: String) {
        analytics.logEvent("open_article") {
            param("article_url", url)
            param("article_source", source)
        }
    }
}