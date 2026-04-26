package com.example.newsapp.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.compose.runtime.Composable
import androidx.navigation.toRoute
import com.example.newsapp.ui.screens.DetailScreen
import com.example.newsapp.ui.screens.FavoritesScreen
import com.example.newsapp.ui.screens.MainScreen
import com.example.newsapp.ui.viewmodel.NewsViewModel
import kotlinx.serialization.Serializable

@Serializable
object Main

@Serializable
object Favorites

@Serializable
data class Detail(
    val url: String,
    val title: String,
    val source: String,
    val date: String
)

data class BottomNavItem(
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val route: Any
)

val bottomNavItems = listOf(
    BottomNavItem("Home", Icons.Filled.Home, Icons.Outlined.Home, Main),
    BottomNavItem("Favorites", Icons.Filled.Bookmark, Icons.Outlined.BookmarkBorder, Favorites)
)

@Composable
fun NavGraph(navController: NavHostController, viewModel: NewsViewModel) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val showBottomBar = currentRoute?.contains(Detail::class.simpleName ?: "") != true

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    bottomNavItems.forEach { item ->
                        val selected = currentRoute?.contains(item.route::class.simpleName ?: "") == true
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(Main) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                                    contentDescription = item.label
                                )
                            },
                            label = { Text(item.label) }
                        )
                    }
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Main,
            enterTransition = {
                slideInHorizontally(initialOffsetX = { it }, animationSpec = tween(300))
            },
            exitTransition = {
                slideOutHorizontally(targetOffsetX = { -it }, animationSpec = tween(300))
            },
            popEnterTransition = {
                slideInHorizontally(initialOffsetX = { -it }, animationSpec = tween(300))
            },
            popExitTransition = {
                slideOutHorizontally(targetOffsetX = { it }, animationSpec = tween(300))
            }
        ) {
            composable<Main> {
                MainScreen(viewModel = viewModel, onArticleClick = { article ->
                    val url = article.url ?: return@MainScreen
                    navController.navigate(
                        Detail(
                            url = url,
                            title = article.title ?: "",
                            source = article.source.name,
                            date = article.publishedAt ?: ""
                        )
                    )
                })
            }
            composable<Favorites> {
                FavoritesScreen(
                    viewModel = viewModel,
                    onArticleClick = { article ->
                        val url = article.url ?: return@FavoritesScreen
                        navController.navigate(
                            Detail(
                                url = url,
                                title = article.title ?: "",
                                source = article.source.name,
                                date = article.publishedAt ?: ""
                            )
                        )
                    }
                )
            }
            composable<Detail> { backStackEntry ->
                val detail: Detail = backStackEntry.toRoute()
                DetailScreen(
                    url = detail.url,
                    title = detail.title,
                    source = detail.source,
                    date = detail.date,
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}