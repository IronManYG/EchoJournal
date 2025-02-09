package dev.gaddal.echojournal.core.data.logs.filter

import dev.gaddal.echojournal.core.domain.logs.audio_log.AudioLog
import dev.gaddal.echojournal.core.domain.logs.audio_log.AudioLogWithTopics
import dev.gaddal.echojournal.core.domain.logs.audio_log.toMoodOrDefault
import dev.gaddal.echojournal.core.domain.logs.filter.AudioLogSort
import dev.gaddal.echojournal.core.domain.logs.filter.FilterAudioLog
import dev.gaddal.echojournal.core.domain.logs.filter.FilterAudioLogParams
import dev.gaddal.echojournal.core.domain.logs.topic.Topic
import dev.gaddal.echojournal.core.domain.mood.Mood

class FilterAudioLogImpl : FilterAudioLog {

    override fun execute(params: FilterAudioLogParams): List<AudioLogWithTopics> {
        val filteredList = params.currentLogs.filter { item ->
            val audioLog = item.audioLog

            // 1) Mood filter
            val moodMatches = matchesMoodFilter(
                audioLog.mood.toMoodOrDefault(),
                params.selectedMoods
            )

            // 2) Topic filter
            val topicMatches = matchesTopicFilter(
                item.topics, // The list of actual Topic domain objects
                params.selectedTopicIds
            )

            // 3) Query filter
            val queryMatches = matchesQueryFilter(
                audioLog,
                params.query
            )

            // 4) Date range filter
            val dateRangeMatches = matchesDateRange(
                createdAt = audioLog.createdAt,
                fromDateMillis = params.fromDateMillis,
                toDateMillis = params.toDateMillis
            )

            moodMatches && topicMatches && queryMatches && dateRangeMatches
        }

        // Now apply the chosen sorting
        return filteredList.sortedWith(getSortComparator(params.sortOrder))
    }

    // --- Filter Checks ---

    private fun matchesMoodFilter(
        logMood: Mood,
        selectedMoods: List<Mood>
    ): Boolean {
        if (selectedMoods.isEmpty()) return true
        return selectedMoods.contains(logMood)
    }

    private fun matchesTopicFilter(
        topics: List<Topic>,
        selectedTopicIds: List<Int>
    ): Boolean {
        if (selectedTopicIds.isEmpty()) return true
        // ANY-match approach: if the log has any of the chosen topic IDs, it passes
        val logTopicIds = topics.map { it.id }
        return logTopicIds.any { it in selectedTopicIds }
    }

    private fun matchesQueryFilter(
        log: AudioLog,
        query: String
    ): Boolean {
        if (query.isBlank()) return true
        val lowerQ = query.lowercase()

        val titleMatch = log.title.lowercase().contains(lowerQ)
        val descMatch = log.description?.lowercase()?.contains(lowerQ) == true
        val transcriptionMatch = log.transcription?.lowercase()?.contains(lowerQ) == true

        // If the user typed "xyz", it passes if it’s in any textual field
        return (titleMatch || descMatch || transcriptionMatch)
    }

    private fun matchesDateRange(
        createdAt: Long,
        fromDateMillis: Long?,
        toDateMillis: Long?
    ): Boolean {
        // If user didn't choose a range, skip
        val from = fromDateMillis ?: Long.MIN_VALUE
        val to   = toDateMillis   ?: Long.MAX_VALUE
        return (createdAt in from..to)
    }

    // --- Sorting ---
    private fun getSortComparator(order: AudioLogSort): Comparator<AudioLogWithTopics> {
        return when (order) {
            AudioLogSort.DateAscending ->
                compareBy { it.audioLog.createdAt }
            AudioLogSort.DateDescending ->
                compareByDescending { it.audioLog.createdAt }
            AudioLogSort.TitleAscending ->
                compareBy { it.audioLog.title.lowercase() }
            AudioLogSort.TitleDescending ->
                compareByDescending { it.audioLog.title.lowercase() }
        }
    }
}

