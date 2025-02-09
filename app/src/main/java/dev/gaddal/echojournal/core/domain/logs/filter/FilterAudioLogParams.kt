package dev.gaddal.echojournal.core.domain.logs.filter

import dev.gaddal.echojournal.core.domain.logs.audio_log.AudioLogWithTopics
import dev.gaddal.echojournal.core.domain.mood.Mood
import dev.gaddal.echojournal.journal.entries.EntriesState

data class FilterAudioLogParams(
    /**
     * The list we want to filter, each item has the `AudioLog` plus its `topics`.
     */
    val currentLogs: List<AudioLogWithTopics>,

    val selectedMoods: List<Mood> = emptyList(),
    val selectedTopicIds: List<Int> = emptyList(),  // which topic IDs are chosen
    val query: String = "",                        // free-text search
    val fromDateMillis: Long? = null,              // optional date range start
    val toDateMillis: Long? = null,                // optional date range end

    val sortOrder: AudioLogSort = AudioLogSort.DateAscending
)

fun EntriesState.toFilterParams(): FilterAudioLogParams {
    return FilterAudioLogParams(
        currentLogs = entriesWithTopics,
        selectedMoods = selectedMoods,
        selectedTopicIds = selectedTopics.map { it.id },
        query = "",
        fromDateMillis = null,
        toDateMillis = null,
        sortOrder = AudioLogSort.DateAscending
    )
}