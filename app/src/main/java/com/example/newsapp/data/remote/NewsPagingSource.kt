package com.example.newsapp.data.remote

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.newsapp.data.model.Article

class NewsPagingSource(
    private val api: NewsApiService,
    private val apiKey: String,
    private val query: String?
) : PagingSource<Int, Article>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Article> {
        val page = params.key ?: 1
        return try {
            val response = if (query.isNullOrBlank()) {
                api.getTopHeadlines(page = page, pageSize = 20, apiKey = apiKey)
            } else {
                api.searchNews(query = query, page = page, pageSize = params.loadSize, apiKey = apiKey)
            }
            // NewsAPI returns placeholder articles with "[Removed]" as title for deleted content
            val articles = response.articles.filter {
                it.title != null && it.title != "[Removed]"
            }
            LoadResult.Page(
                data = articles,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (articles.isEmpty()) null else page + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Article>): Int? {
        return state.anchorPosition?.let { anchor ->
            state.closestPageToPosition(anchor)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchor)?.nextKey?.minus(1)
        }
    }
}