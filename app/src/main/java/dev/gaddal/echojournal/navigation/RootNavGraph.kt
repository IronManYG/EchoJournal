package dev.gaddal.echojournal.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import dev.gaddal.echojournal.core.presentation.ui.navigation.MainScreen
import dev.gaddal.echojournal.core.presentation.ui.navigation.journalNavGraph

@Composable
fun RootNavGraph(
    navController: NavHostController,
) {
    NavHost(
        navController = navController,
        startDestination = MainScreen.Journal
    ) {
        journalNavGraph(navController)
    }
}