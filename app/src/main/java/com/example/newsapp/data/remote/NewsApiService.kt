package com.example.newsapp.data.remote

import com.example.newsapp.data.model.NewsResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsApiService {

    @GET("v2/top-headlines")
    suspend fun getTopHeadlines(
        @Query("country") country: String = "us",
        @Query("page") page: Int = 1,
        @Query("pageSize") pageSize: Int = 20,
        @Query("apiKey") apiKey: String
    ): NewsResponse

    @GET("v2/everything")
    suspend fun searchNews(
        @Query("q") query: String,
        @Query("page") page: Int = 1,
        @Query("pageSize") pageSize: Int = 20,
        @Query("apiKey") apiKey: String
    ): NewsResponse
}