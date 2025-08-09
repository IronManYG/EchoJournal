package dev.gaddal.echojournal.journal.entries.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.gaddal.echojournal.core.domain.logs.audio_log.AudioLog
import dev.gaddal.echojournal.core.domain.logs.audio_log.toMoodOrDefault
import dev.gaddal.echojournal.core.domain.logs.topic.Topic
import dev.gaddal.echojournal.core.domain.mood.Mood
import dev.gaddal.echojournal.core.extensions.to12HourTimeString
import dev.gaddal.echojournal.core.extensions.to24HourTimeString
import dev.gaddal.echojournal.core.presentation.designsystem.EchoJournalTheme
import dev.gaddal.echojournal.core.presentation.designsystem.colors.NeutralVariant90
import dev.gaddal.echojournal.core.presentation.designsystem.components.EchoJournalTopic
import dev.gaddal.echojournal.core.presentation.designsystem.components.ExpandableText
import dev.gaddal.echojournal.core.presentation.ui.LocalesPreview
import dev.gaddal.echojournal.core.sample.SampleData.getLocalizedSampleLogs
import dev.gaddal.echojournal.core.sample.SampleData.getLocalizedSampleTopics
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun EntryCard(
    audioLog: AudioLog,
    as24Hour: Boolean = false,
    topics: List<Topic> = listOf(),
    modifier: Modifier = Modifier,
    showLineAbove: Boolean = false,
    showLineBelow: Boolean = false,
    gapBetweenEntries: Dp = 0.dp,
    // Additional params to control playback
    isPlaying: Boolean,
    isPaused: Boolean,
    currentPosition: Duration,
    duration: Duration,
    onPlay: (id: Int) -> Unit,
    onPause: () -> Unit,
    onResume: () -> Unit,
    onStop: () -> Unit,
    onSeek: (ms: Int) -> Unit
) {
    // Convert the string mood to our sealed class
    val mood: Mood = audioLog.mood.toMoodOrDefault()

    // 1) Calculate surface height to draw timeline
    val surfaceHeightPx = remember { mutableIntStateOf(0) }
    val surfaceHeightDp = with(LocalDensity.current) { surfaceHeightPx.intValue.toDp() }

    Row(modifier = modifier.fillMaxWidth()) {
        // 2) Timeline Column (icon + optional vertical lines)
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Conditionally show the line above the icon
            val firstDividerHeight = 8.dp
            if (showLineAbove) {
                VerticalDivider(
                    modifier = Modifier.height(firstDividerHeight),
                    color = NeutralVariant90
                )
            } else {
                Spacer(modifier = Modifier.height(firstDividerHeight))
            }

            // Use the mood's icon here
            val imageSize = 32.dp
            Image(
                imageVector = ImageVector.vectorResource(id = mood.iconRes),
                contentDescription = null,
                modifier = Modifier.size(imageSize)
            )

            // Conditionally show the line below the icon
            if (showLineBelow) {
                VerticalDivider(
                    modifier = Modifier.height(
                        maxOf(
                            0.dp,
                            surfaceHeightDp + gapBetweenEntries - (firstDividerHeight + imageSize)
                        )
                    ),
                    color = NeutralVariant90
                )
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        // 3) Surface for the card content
        Surface(
            shape = RoundedCornerShape(10.dp),
            shadowElevation = 12.dp,
            modifier = Modifier.onGloballyPositioned { layoutCoordinates ->
                surfaceHeightPx.intValue = layoutCoordinates.size.height
            }
        ) {
            Column(
                modifier = Modifier.padding(start = 14.dp, top = 12.dp, end = 14.dp, bottom = 14.dp)
            ) {
                // Your row with title and time
                Row(
                    modifier = Modifier.padding(bottom = 2.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = audioLog.title,
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Text(
                        text = if (as24Hour) audioLog.createdAt.to24HourTimeString()
                            .asString() else audioLog.createdAt.to12HourTimeString().asString(),
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                AudioPlayer(
                    isPlaying = isPlaying,
                    isPaused = isPaused,
                    currentPosition = currentPosition,
                    duration = duration,
                    onPlay = { onPlay(audioLog.id) },
                    onPause = onPause,
                    onResume = onResume,
                    onStop = onStop,
                    onSeek = onSeek,
                    mood = mood
                )

                if (!audioLog.description.isNullOrBlank()) {
                    ExpandableText(
                        text = audioLog.description,
                        modifier = Modifier.padding(vertical = 8.dp),
                        style = MaterialTheme.typography.bodyMedium,
                        fontSize = 12.sp,
                    )
                }

                if (topics.isNotEmpty()) {
                    LazyRow(
                        modifier = Modifier.padding(top = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        items(topics) {
                            EchoJournalTopic(topic = it)
                        }
                    }
                }
            }
        }
    }
}

@LocalesPreview
@Composable
fun EntryCardPreview() {
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
        val (pos, dur) = randomPositionDuration()
        EntryCard(
            audioLog = getLocalizedSampleLogs().first(),
            topics = getLocalizedSampleTopics(),
            isPlaying = false,
            isPaused = false,
            currentPosition = pos,
            duration = dur,
            onPlay = {},
            onPause = {},
            onResume = {},
            onStop = {},
            onSeek = {}
        )
    }
}
