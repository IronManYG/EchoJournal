package dev.gaddal.echojournal.core.presentation.designsystem

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import dev.gaddal.echojournal.R

val NoEntriesIcon: ImageVector
    @Composable
    get() = ImageVector.vectorResource(id = R.drawable.no_entries)

val StressesMood: ImageVector
    @Composable
    get() = ImageVector.vectorResource(id = R.drawable.mood_stresses)

val SadMood: ImageVector
    @Composable
    get() = ImageVector.vectorResource(id = R.drawable.mood_sad)

val NeutralMood: ImageVector
    @Composable
    get() = ImageVector.vectorResource(id = R.drawable.mood_neutral)

val PeacefulMood: ImageVector
    @Composable
    get() = ImageVector.vectorResource(id = R.drawable.mood_peaceful)

val ExcitedMood: ImageVector
    @Composable
    get() = ImageVector.vectorResource(id = R.drawable.mood_excited)