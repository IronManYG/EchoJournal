package dev.gaddal.echojournal.core.presentation.ui.navigation

import kotlinx.serialization.Serializable

sealed class MainScreen : Screen() {
    @Serializable
    data object Auth : MainScreen()

    @Serializable
    data object Journal : MainScreen()
}