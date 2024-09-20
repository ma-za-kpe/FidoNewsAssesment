package com.fido.newappassesment.ui.navigation

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.fido.newappassesment.ui.screens.FullArticleScreen
import com.fido.newappassesment.ui.screens.LifecycleAwareMainScreen
import com.fido.newappassesment.ui.screens.MainScreen


sealed class Screen(val route: String) {
    object Main : Screen("main")
    object FullArticle : Screen("full_article/{articleUrl}") {
        fun createRoute(articleUrl: String): String {
            return "full_article/${articleUrl}"
        }
    }
}

@Composable
fun MainNavGraph(
    navController: NavHostController = rememberNavController()
) {
    NavHost(navController = navController, startDestination = Screen.Main.route) {
        composable(Screen.Main.route) {
            LifecycleAwareMainScreen(
                onArticleSelected = { article ->
                    val encodedUrl = Uri.encode(article.url)
                    navController.navigate(Screen.FullArticle.createRoute(encodedUrl))
                }
            )
        }
        composable(
            route = Screen.FullArticle.route,
            arguments = listOf(navArgument("articleUrl") { type = NavType.StringType })
        ) { backStackEntry ->
            val articleUrl = backStackEntry.arguments?.getString("articleUrl")
            requireNotNull(articleUrl) { "Article URL is required" }
            FullArticleScreen(articleUrl = Uri.decode(articleUrl), navController)
        }
    }
}
