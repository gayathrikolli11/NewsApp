package com.example.newsapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.newsapp.navigation.NavGraph
import com.example.newsapp.ui.theme.NewsAppTheme
import com.example.newsapp.ui.viewmodel.NewsViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NewsAppTheme {
                val navController = rememberNavController()
                val viewModel: NewsViewModel = viewModel()
                NavGraph(
                    navController = navController,
                    viewModel = viewModel
                )
            }
        }
    }
}