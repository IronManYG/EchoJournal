@file:OptIn(ExperimentalFoundationApi::class)

package dev.gaddal.echojournal.journal.entries.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.gaddal.echojournal.core.domain.logs.audio_log.AudioLogWithTopics
import dev.gaddal.echojournal.core.extensions.formatDisplay
import dev.gaddal.echojournal.core.extensions.toLocalDate
import dev.gaddal.echojournal.core.presentation.designsystem.EchoJournalTheme
import dev.gaddal.echojournal.core.presentation.ui.LocalesPreview
import dev.gaddal.echojournal.core.sample.SampleData.getLocalizedSampleLogsWithTopics
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun EntryList(
    modifier: Modifier = Modifier,
    entries: List<AudioLogWithTopics>,
    nowPlayingLogId: Int?,
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
    // 1) Group logs by their creation date (descending)
    val groupedLogs = entries.groupBy { it.audioLog.toLocalDate() }
    val sortedGroups = groupedLogs
        .toSortedMap(compareByDescending { it }) // sort by date descending
        .toList() // => List<Pair<LocalDate, List<AudioLogWithTopics>>>

    // 2) Define your desired spacing between items
    val gapBetweenEntries = 16.dp

    LazyColumn(
        modifier = modifier.padding(horizontal = 16.dp),
        contentPadding = PaddingValues(vertical = gapBetweenEntries)
    ) {
        sortedGroups.forEachIndexed { groupIndex, (date, dayLogs) ->
            stickyHeader {
                // e.g., "Today", "Yesterday", or date string
                val label = date.formatDisplay().asString()
                if (groupIndex > 0) {
                    Spacer(modifier = Modifier.height(gapBetweenEntries))
                }
                DateHeader(label)
                Spacer(modifier = Modifier.height(gapBetweenEntries))
            }

            itemsIndexed(dayLogs, key = { _, item -> item.audioLog.id }) { index, item ->
                val isSingleItem = (dayLogs.size == 1)
                // - first item => no top line
                // - last item => no bottom line
                val showLineAbove = !isSingleItem && index != 0
                val showLineBelow = !isSingleItem && index != dayLogs.lastIndex

                // The card now receives the real topics from item.topics
                EntryCard(
                    audioLog = item.audioLog,
                    topics = item.topics,
                    showLineAbove = showLineAbove,
                    showLineBelow = showLineBelow,
                    gapBetweenEntries = gapBetweenEntries,
                    modifier = Modifier,
                    isPlaying = if (item.audioLog.id == nowPlayingLogId) isPlaying else false,
                    isPaused = if (item.audioLog.id == nowPlayingLogId) isPaused else false,
                    currentPosition = if (item.audioLog.id == nowPlayingLogId) currentPosition else Duration.ZERO,
                    duration = if (item.audioLog.durationMs == null) duration else item.audioLog.durationMs.milliseconds,
                    onPlay = { id -> onPlay(id) },
                    onPause = onPause,
                    onResume = onResume,
                    onStop = onStop,
                    onSeek = { ms -> onSeek(ms) },
                )
            }
        }
    }
}

@LocalesPreview
@Composable
fun EntryListPreview() {
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
        EntryList(
            entries = getLocalizedSampleLogsWithTopics(),
            nowPlayingLogId = null,
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


@LocalesPreview
@Composable
fun EntryListEmptyPreview() {
    EchoJournalTheme {
        EntryList(
            entries = emptyList(),
            nowPlayingLogId = null,
            isPlaying = false,
            isPaused = false,
            currentPosition = Duration.ZERO,
            duration = 0.milliseconds,
            onPlay = {},
            onPause = {},
            onResume = {},
            onStop = {},
            onSeek = {}
        )
    }
}

@LocalesPreview
@Composable
fun EntryListPlayingPreview() {
    EchoJournalTheme {
        val entries = getLocalizedSampleLogsWithTopics()
        val nowPlayingId = entries.firstOrNull()?.audioLog?.id
        EntryList(
            entries = entries,
            nowPlayingLogId = nowPlayingId,
            isPlaying = true,
            isPaused = false,
            currentPosition = 30_000.milliseconds,
            duration = 120_000.milliseconds,
            onPlay = {},
            onPause = {},
            onResume = {},
            onStop = {},
            onSeek = {}
        )
    }
}

@LocalesPreview
@Composable
fun EntryListPausedPreview() {
    EchoJournalTheme {
        val entries = getLocalizedSampleLogsWithTopics()
        val nowPlayingId = entries.firstOrNull()?.audioLog?.id
        EntryList(
            entries = entries,
            nowPlayingLogId = nowPlayingId,
            isPlaying = false,
            isPaused = true,
            currentPosition = 45_000.milliseconds,
            duration = 120_000.milliseconds,
            onPlay = {},
            onPause = {},
            onResume = {},
            onStop = {},
            onSeek = {}
        )
    }
}
