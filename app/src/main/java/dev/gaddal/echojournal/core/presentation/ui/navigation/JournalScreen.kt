package dev.gaddal.echojournal.core.presentation.ui.navigation

import kotlinx.serialization.Serializable

sealed class JournalScreen : Screen() {
    @Serializable
    data object Entries : JournalScreen()

    @Serializable
    data object NewEntry : JournalScreen()

    @Serializable
    data class EntryDetails(val entryId: Int) : JournalScreen()

    @Serializable
    data object Settings : JournalScreen()
}
