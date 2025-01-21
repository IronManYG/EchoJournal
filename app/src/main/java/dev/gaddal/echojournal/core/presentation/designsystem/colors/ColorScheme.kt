package dev.gaddal.echojournal.core.presentation.designsystem.colors

import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

/**
 * ColorScheme: default
 */
val ColorScheme = lightColorScheme(
    primary = Primary30,
    onPrimary = Primary100,
    primaryContainer = Primary50,
    onPrimaryContainer = Primary10, //
    inversePrimary = Secondary80,
    secondary = Secondary30,
    secondaryContainer = Secondary50,
    background = NeutralVariant99,
    surface = Primary100,
    onSurface = NeutralVariant10,
    surfaceVariant = Color(0xFFE1E2EC),
    onSurfaceVariant = NeutralVariant30,
    surfaceTint = Color(0xFF475D92),
    inverseOnSurface = Secondary95,
    onError = Error100,
    errorContainer = Error95,
    onErrorContainer = Error20,
    outline = NeutralVariant50,
    outlineVariant = NeutralVariant80,
)