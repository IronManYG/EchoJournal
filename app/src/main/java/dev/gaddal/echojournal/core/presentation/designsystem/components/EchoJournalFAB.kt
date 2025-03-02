package dev.gaddal.echojournal.core.presentation.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.gaddal.echojournal.core.presentation.designsystem.EchoJournalTheme
import dev.gaddal.echojournal.core.presentation.designsystem.colors.ButtonGradient
import dev.gaddal.echojournal.core.presentation.designsystem.colors.Primary90
import dev.gaddal.echojournal.core.presentation.designsystem.colors.Primary95
import dev.gaddal.echojournal.core.presentation.designsystem.components.util.applyIf
import dev.gaddal.echojournal.journal.entries.components.BreathingCircle

@Composable
fun EchoJournalFAB(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    isLargeVariant: Boolean = false,
    rippleEnabled: Boolean = false,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        if (rippleEnabled) {
            BreathingCircle(
                color = Primary95,
                circleSize = 128.dp,
                durationMillis = 3000,
            )

            BreathingCircle(
                color = Primary90,
                circleSize = 108.dp,
                durationMillis = 3000,
            )
        }
        Surface(
            onClick = onClick,
            shape = CircleShape,
        ) {
            Box(
                modifier = Modifier
                    .applyIf(isLargeVariant) {
                        defaultMinSize(
                            minWidth = 72.dp,
                            minHeight = 72.dp,
                        )
                    }
                    .defaultMinSize(
                        minWidth = 64.dp,
                        minHeight = 64.dp,
                    )
                    .background(
                        brush = Brush.linearGradient(
                            colors = ButtonGradient
                        )
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EchoJournalFABPreview() {
    EchoJournalTheme {
        EchoJournalFAB(
            icon = Icons.Default.Add,
            isLargeVariant = false,
            rippleEnabled = true,
            onClick = { }
        )
    }
}