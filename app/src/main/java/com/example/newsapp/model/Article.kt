package com.example.newsapp.data.model

data class Source(
    val id: String?,
    val name: String
)

data class Article(
    val source: Source,
    val title: String?,
    val description: String?,
    val url: String?,
    val urlToImage: String?,
    val publishedAt: String?
)

data class NewsResponse(
    val status: String,
    val totalResults: Int,
    val articles: List<Article>
)