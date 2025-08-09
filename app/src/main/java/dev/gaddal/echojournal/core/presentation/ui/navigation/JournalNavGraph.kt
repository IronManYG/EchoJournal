package dev.gaddal.echojournal.core.presentation.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import androidx.navigation.toRoute
import dev.gaddal.echojournal.journal.entries.EntriesScreenRoot
import dev.gaddal.echojournal.journal.entry.EntryScreenRoot

fun NavGraphBuilder.journalNavGraph(navController: NavHostController) {
    navigation<MainScreen.Journal>(
        startDestination = JournalScreen.Entries,
    ) {
        composable<JournalScreen.Entries> {
            EntriesScreenRoot(
                onSettingsClick = {
                    navController.navigate(JournalScreen.Settings)
                },
                onCreateNewEntryTrigger = {
                    navController.navigate(JournalScreen.NewEntry) {
                        popUpTo(JournalScreen.NewEntry) {
                            inclusive = true
                        }
                    }
                },
                onEntryClick = { id ->
                    navController.navigate(JournalScreen.EntryDetails(id))
                },
            )
        }
        composable<JournalScreen.NewEntry> { backStackEntry ->
            val newEntry = backStackEntry.toRoute<JournalScreen.NewEntry>()
            EntryScreenRoot(
                onBackClick = {
                    navController.navigateUp()
                }
            )
        }
        composable<JournalScreen.EntryDetails> { backStackEntry ->
            val entryDetails = backStackEntry.toRoute<JournalScreen.EntryDetails>()
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                Text("Entry details ${entryDetails.entryId}")
            }
        }
        composable<JournalScreen.Settings> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                Text("Settings")
            }
        }
    }
}
