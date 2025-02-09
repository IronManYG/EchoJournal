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
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.gaddal.echojournal.core.domain.logs.audio_log.AudioLogWithTopics
import dev.gaddal.echojournal.core.extensions.formatDisplay
import dev.gaddal.echojournal.core.extensions.toLocalDate
import dev.gaddal.echojournal.core.presentation.designsystem.EchoJournalTheme
import dev.gaddal.echojournal.core.sample.SampleData.sampleAudioLogsWithTopics

@Composable
fun EntryList(
    modifier: Modifier = Modifier,
    entries: List<AudioLogWithTopics>,
) {
    // 1) Group logs by their creation date (descending)
    val groupedLogs = entries.groupBy { it.audioLog.toLocalDate() }
    val sortedGroups = groupedLogs
        .toSortedMap(compareByDescending { it })  // sort by date descending
        .toList()                                 // => List<Pair<LocalDate, List<AudioLogWithTopics>>>

    // 2) Define your desired spacing between items
    val gapBetweenEntries = 16.dp

    LazyColumn(
        modifier = modifier.padding(horizontal = 16.dp),
        contentPadding = PaddingValues(vertical = gapBetweenEntries)
    ) {
        sortedGroups.forEachIndexed { groupIndex, (date, dayLogs) ->
            stickyHeader {
                // e.g., "Today", "Yesterday", or date string
                val label = date.formatDisplay()
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
                    modifier = Modifier
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EntryListPreview() {
    EchoJournalTheme {
        EntryList(entries = sampleAudioLogsWithTopics)
    }
}


