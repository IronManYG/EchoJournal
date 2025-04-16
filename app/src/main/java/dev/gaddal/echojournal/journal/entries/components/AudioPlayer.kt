package dev.gaddal.echojournal.journal.entries.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.gaddal.echojournal.R
import dev.gaddal.echojournal.core.domain.mood.Mood
import dev.gaddal.echojournal.core.extensions.formatAsMmSs
import dev.gaddal.echojournal.core.presentation.designsystem.EchoJournalTheme
import dev.gaddal.echojournal.core.presentation.ui.LocalesPreview
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun AudioPlayer(
    modifier: Modifier = Modifier,
    isPlaying: Boolean,
    isPaused: Boolean,
    currentPosition: Duration,
    duration: Duration,
    onPlay: () -> Unit,
    onPause: () -> Unit,
    onResume: () -> Unit,
    onStop: () -> Unit,
    onSeek: (Int) -> Unit, // ms
    mood: Mood? = null,
    // If mood is null, these become the fallback defaults:
    containerColor: Color = MaterialTheme.colorScheme.inverseOnSurface,
    iconTint: Color = MaterialTheme.colorScheme.primary,
    progressColor: Color = MaterialTheme.colorScheme.primary,
    progressTrackColor: Color = MaterialTheme.colorScheme.inversePrimary,
    textColor: Color = MaterialTheme.colorScheme.onSurface
) {
    // 1) Determine final colors based on whether mood is provided
    val finalContainerColor = mood?.containerColor ?: containerColor
    val finalIconTint = mood?.iconTint ?: iconTint
    val finalProgressColor = mood?.progressColor ?: progressColor
    val finalProgressTrackColor = mood?.progressTrackColor ?: progressTrackColor

    // 2) Compute progress fraction
    val progressFraction = if (duration.inWholeMilliseconds == 0L) 0f
    else currentPosition.inWholeMilliseconds / duration.inWholeMilliseconds.toFloat()

    // 3) Determine which icon to show (and action to call) based on isPlaying/isPaused
    val icon = when {
        isPlaying && !isPaused -> Icons.Default.Pause
        else -> Icons.Default.PlayArrow // either paused or not playing
    }

    val onClickAction: () -> Unit = when {
        // If it’s paused, tapping the button should resume
        isPlaying && isPaused -> onResume
        // If it’s playing and not paused, tapping should pause
        isPlaying && !isPaused -> onPause
        // Otherwise, it’s not playing => onPlay
        else -> onPlay
    }

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
                onClick = { onClickAction() },
                modifier = Modifier.size(32.dp),
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier
                        .size(24.dp),
                    tint = finalIconTint
                )
            }
        }

        LinearProgressIndicator(
            progress = { progressFraction.coerceIn(0f, 1f) },
            modifier = Modifier.weight(1f),
            color = finalProgressColor,
            trackColor = finalProgressTrackColor,
            gapSize = 0.dp
        )

        Text(
            text = buildString {
                append(currentPosition.formatAsMmSs().asString())
                append(stringResource(R.string.audio_duration_separator))
                append(duration.formatAsMmSs().asString())
            },
            modifier = Modifier.padding(end = 4.dp),
            style = MaterialTheme.typography.bodySmall,
            color = textColor,
        )
    }
}

@LocalesPreview
@Composable
fun AudioPlayerPreview() {
    // For randomness
    val random = remember { kotlin.random.Random(System.currentTimeMillis()) }

    // Helper function that returns a random currentPosition/duration pair
    fun randomPositionDuration(): Pair<Duration, Duration> {
        // totalDuration between 1 min (60s) and 5 min (300s)
        val totalMs = random.nextInt(from = 60_000, until = 300_000)
        // currentPosition between 0 and totalDuration
        val currentMs = random.nextInt(until = totalMs)
        return currentMs.milliseconds to totalMs.milliseconds
    }

    EchoJournalTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1) Default version
            val (pos0, dur0) = randomPositionDuration()
            AudioPlayer(
                isPlaying = false,
                isPaused = false,
                currentPosition = pos0,
                duration = dur0,
                onPlay = {},
                onResume = {},
                onPause = {},
                onStop = {},
                onSeek = {}
            )

            // 2) Sad mood
            val (pos1, dur1) = randomPositionDuration()
            AudioPlayer(
                isPlaying = true,
                isPaused = false,
                currentPosition = pos1,
                duration = dur1,
                onPlay = {},
                onPause = {},
                onResume = {},
                onStop = {},
                onSeek = {},
                mood = Mood.Sad
            )

            // 3) Stressed
            val (pos2, dur2) = randomPositionDuration()
            AudioPlayer(
                isPlaying = true,
                isPaused = true,
                currentPosition = pos2,
                duration = dur2,
                onPlay = {},
                onPause = {},
                onResume = {},
                onStop = {},
                onSeek = {},
                mood = Mood.Stressed
            )

            // 4) Neutral
            val (pos3, dur3) = randomPositionDuration()
            AudioPlayer(
                isPlaying = false,
                isPaused = false,
                currentPosition = pos3,
                duration = dur3,
                onPlay = {},
                onPause = {},
                onResume = {},
                onStop = {},
                onSeek = {},
                mood = Mood.Neutral
            )

            // 5) Peaceful
            val (pos4, dur4) = randomPositionDuration()
            AudioPlayer(
                isPlaying = true,
                isPaused = false,
                currentPosition = pos4,
                duration = dur4,
                onPlay = {},
                onPause = {},
                onResume = {},
                onStop = {},
                onSeek = {},
                mood = Mood.Peaceful
            )

            // 6) Excited
            val (pos5, dur5) = randomPositionDuration()
            AudioPlayer(
                isPlaying = true,
                isPaused = true,
                currentPosition = pos5,
                duration = dur5,
                onPlay = {},
                onPause = {},
                onResume = {},
                onStop = {},
                onSeek = {},
                mood = Mood.Excited
            )
        }
    }
}
