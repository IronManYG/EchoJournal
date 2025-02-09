package dev.gaddal.echojournal.journal.entries.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.gaddal.echojournal.core.domain.mood.Mood
import dev.gaddal.echojournal.core.presentation.designsystem.EchoJournalTheme


@Composable
fun AudioPlayer(
    modifier: Modifier = Modifier,
    mood: Mood? = null,
    // If mood is null, these become the fallback defaults:
    containerColor: Color = MaterialTheme.colorScheme.inverseOnSurface,
    iconTint: Color = MaterialTheme.colorScheme.primary,
    progressColor: Color = MaterialTheme.colorScheme.primary,
    progressTrackColor: Color = MaterialTheme.colorScheme.inversePrimary,
    textColor: Color = MaterialTheme.colorScheme.onSurface
) {
    // Determine final colors based on whether mood is provided
    val finalContainerColor = mood?.containerColor ?: containerColor
    val finalIconTint = mood?.iconTint ?: iconTint
    val finalProgressColor = mood?.progressColor ?: progressColor
    val finalProgressTrackColor = mood?.progressTrackColor ?: progressTrackColor

    Row(
        modifier = modifier
            .clip(CircleShape)
            .background(finalContainerColor)
            .padding(4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Surface(
            shape = CircleShape,
            shadowElevation = 12.dp,
        ) {
            IconButton(
                onClick = { },
                modifier = Modifier.size(32.dp),
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = finalIconTint
                )
            }
        }
        LinearProgressIndicator(
            progress = { 70f / 100.0f },
            modifier = Modifier.weight(1f),
            color = finalProgressColor,
            trackColor = finalProgressTrackColor,
            gapSize = 0.dp
        )
        Text(
            text = "00:00/12:30",
            modifier = Modifier.padding(end = 4.dp),
            style = MaterialTheme.typography.bodySmall,
            color = textColor,
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AudioPlayerPreview() {
    EchoJournalTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Default version
            AudioPlayer()

            // Moods
            AudioPlayer(mood = Mood.Sad)

            AudioPlayer(mood = Mood.Stressed)

            AudioPlayer(mood = Mood.Neutral)

            AudioPlayer(mood = Mood.Peaceful)

            AudioPlayer(mood = Mood.Excited)
        }
    }
}