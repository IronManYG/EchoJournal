package dev.gaddal.echojournal.journal.entries.components

import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.gaddal.echojournal.core.presentation.designsystem.EchoJournalTheme
import dev.gaddal.echojournal.core.presentation.designsystem.colors.Primary90
import dev.gaddal.echojournal.core.presentation.designsystem.colors.Primary95
import dev.gaddal.echojournal.core.presentation.designsystem.components.util.applyIf

@Composable
fun BreathingCircle(
    modifier: Modifier = Modifier,
    color: Color = Color.Blue.copy(alpha = 0.5f),
    circleSize: Dp = 128.dp,
    initialScale: Float = 0.9f,
    targetScale: Float = 1.1f,
    durationMillis: Int = 1000,
    repeatMode: RepeatMode = RepeatMode.Reverse,
    easing: Easing = LinearOutSlowInEasing,
    isAnimated: Boolean = true,
    brush: Brush? = null
) {
    // Decide the current scale based on isAnimated
    val scale: Float
    if (isAnimated) {
        val infiniteTransition = rememberInfiniteTransition()
        val animatedScale by infiniteTransition.animateFloat(
            initialValue = initialScale,
            targetValue = targetScale,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = durationMillis, easing = easing),
                repeatMode = repeatMode
            )
        )
        scale = animatedScale
    } else {
        scale = initialScale
    }

    Box(
        modifier = modifier
            .size(circleSize)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            // If brush is null, apply the solid color background
            .applyIf(condition = (brush == null)) {
                background(
                    color = color,
                    shape = CircleShape
                )
            }
            // If brush is not null, apply the brush background
            .applyIf(condition = (brush != null)) {
                // Safely unwrap brush here with '!!' or requireNotNull(brush)
                background(
                    brush = requireNotNull(brush),
                    shape = CircleShape
                )
            }
    )
}

@Preview(showBackground = true)
@Composable
fun BreathingCirclePreview(modifier: Modifier = Modifier) {
    EchoJournalTheme {
        Box(
            modifier = modifier,
            contentAlignment = Alignment.Center
        ) {
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
    }
}
