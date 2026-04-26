package com.example.newsapp.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.newsapp.data.model.Article
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "favorites")

class FavoritesManager(private val context: Context) {

    private val gson = Gson()
    private val favoritesKey = stringPreferencesKey("favorites_list")

    val favorites: Flow<List<Article>> = context.dataStore.data.map { prefs ->
        val json = prefs[favoritesKey] ?: return@map emptyList()
        val type = object : TypeToken<List<Article>>() {}.type
        gson.fromJson(json, type) ?: emptyList()
    }

    suspend fun addFavorite(article: Article) {
        context.dataStore.edit { prefs ->
            val current = getCurrentList(prefs)
            if (current.none { it.url == article.url }) {
                val updated = current + article
                prefs[favoritesKey] = gson.toJson(updated)
            }
        }
    }

    suspend fun removeFavorite(article: Article) {
        context.dataStore.edit { prefs ->
            val current = getCurrentList(prefs)
            val updated = current.filter { it.url != article.url }
            prefs[favoritesKey] = gson.toJson(updated)
        }
    }

    private fun getCurrentList(prefs: Preferences): List<Article> {
        val json = prefs[favoritesKey] ?: return emptyList()
        val type = object : TypeToken<List<Article>>() {}.type
        return gson.fromJson(json, type) ?: emptyList()
    }
}