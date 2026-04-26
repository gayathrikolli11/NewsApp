package com.example.newsapp.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.newsapp.data.model.Article
import com.example.newsapp.data.remote.NewsApiService
import com.example.newsapp.data.remote.NewsPagingSource
import kotlinx.coroutines.flow.Flow

class NewsRepository(private val api: NewsApiService) {

    fun getHeadlines(apiKey: String, query: String? = null): Flow<PagingData<Article>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                prefetchDistance = 5,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                NewsPagingSource(api, apiKey, query)
            }
        ).flow
    }
}