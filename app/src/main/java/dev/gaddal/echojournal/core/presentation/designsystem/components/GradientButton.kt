@file:Suppress("unused")

package dev.gaddal.echojournal.core.presentation.designsystem.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Simple extension to detect if the button is currently pressed.
 */
@Composable
fun MutableInteractionSource.collectIsPressedAsState(): State<Boolean> {
    val isPressed = remember { mutableStateOf(false) }
    LaunchedEffect(this) {
        interactions.collect { interaction ->
            when (interaction) {
                is PressInteraction.Press -> isPressed.value = true
                // Once user releases/cancels, reset to false
                is PressInteraction.Release,
                is PressInteraction.Cancel -> isPressed.value = false
            }
        }
    }
    return isPressed
}

/**
 * A custom gradient button that supports different brushes for enabled/disabled/pressed states.
 *
 * @param onClick Called when this button is clicked.
 * @param modifier Modifier to be applied to the button.
 * @param enabled Whether the button is enabled or not.
 * @param shape Shape of the button.
 * @param enabledBrush Gradient brush used when the button is enabled (and not pressed).
 * @param pressedBrush Gradient brush used when the button is pressed (still enabled).
 * @param disabledBrush Gradient brush used when the button is disabled.
 * @param enabledContentColor Color for the content (text, icon) when enabled.
 * @param pressedContentColor Color for the content (text, icon) when pressed.
 * @param disabledContentColor Color for the content when disabled.
 * @param elevation Shadow elevation when enabled (pressed or not). No elevation if disabled.
 * @param border Optional border around the button.
 * @param contentPadding Spacing around the button content.
 * @param interactionSource Interaction source that emits press/focus/hover events.
 * @param content The composable content (usually Text and/or Icon).
 */
@Composable
fun GradientButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape = ButtonDefaults.shape,

    enabledBrush: Brush = Brush.linearGradient(
        listOf(
            MaterialTheme.colorScheme.primaryContainer,
            MaterialTheme.colorScheme.primary
        )
    ),
    pressedBrush: Brush = Brush.linearGradient(
        listOf(
            MaterialTheme.colorScheme.primary,
            MaterialTheme.colorScheme.primaryContainer
        )
    ),
    disabledBrush: Brush = Brush.linearGradient(
        listOf(Color.LightGray, Color.LightGray)
    ),

    enabledContentColor: Color = MaterialTheme.colorScheme.onPrimary,
    pressedContentColor: Color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f),
    disabledContentColor: Color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),

    elevation: Dp = 0.dp,
    border: BorderStroke? = null,
    contentPadding: PaddingValues = PaddingValues(
        horizontal = 16.dp,
        vertical = 8.dp
    ),
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    content: @Composable RowScope.() -> Unit
) {
    // For accessibility semantics (TalkBack will read "Button")
    val semanticsModifier = Modifier.semantics { role = Role.Button }

    // Track if we're currently pressed
    val isPressed by interactionSource.collectIsPressedAsState()

    // Decide which brush to use:
    //  1) If not enabled, use disabledBrush
    //  2) If pressed, use pressedBrush
    //  3) Otherwise, use enabledBrush
    val finalBrush = when {
        !enabled -> disabledBrush
        isPressed -> pressedBrush
        else -> enabledBrush
    }

    // Decide which content color to use:
    //  1) If not enabled, use disabledContentColor
    //  2) If pressed, use pressedContentColor
    //  3) Otherwise, use enabledContentColor
    val finalContentColor = when {
        !enabled -> disabledContentColor
        isPressed -> pressedContentColor
        else -> enabledContentColor
    }

    // Shadow only if enabled
    val finalElevation = if (enabled) elevation else 0.dp

    Surface(
        onClick = onClick,
        modifier = modifier.then(semanticsModifier),
        enabled = enabled,
        shape = shape,
        color = Color.Transparent, // We'll draw the gradient ourselves
        contentColor = finalContentColor,
        shadowElevation = finalElevation,
        border = border,
        interactionSource = interactionSource
    ) {
        Box(
            modifier = Modifier
                .background(finalBrush)
                .defaultMinSize(
                    minWidth = ButtonDefaults.MinWidth,
                    minHeight = ButtonDefaults.MinHeight
                )
                .padding(contentPadding),
            contentAlignment = Alignment.Center
        ) {
            // Provide color & text style to children
            CompositionLocalProvider(
                LocalContentColor provides finalContentColor,
                LocalTextStyle provides MaterialTheme.typography.labelLarge
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    content = content
                )
            }
        }
    }
}
