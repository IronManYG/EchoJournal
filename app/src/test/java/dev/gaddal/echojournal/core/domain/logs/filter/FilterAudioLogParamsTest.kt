package dev.gaddal.echojournal.core.domain.logs.filter

import dev.gaddal.echojournal.core.domain.logs.audio_log.AudioLog
import dev.gaddal.echojournal.core.domain.logs.audio_log.AudioLogWithTopics
import dev.gaddal.echojournal.core.domain.logs.topic.Topic
import dev.gaddal.echojournal.core.domain.mood.Mood
import dev.gaddal.echojournal.journal.entries.EntriesState
import org.junit.Assert.assertEquals
import org.junit.Test

class FilterAudioLogParamsTest {

    @Test
    fun toFilterParams_mapsAllFields() {
        val topics = listOf(
            Topic(id = 1, name = "Work", colorHex = null),
            Topic(id = 2, name = "Life", colorHex = null)
        )

        val logs = listOf(
            AudioLogWithTopics(
                audioLog = AudioLog(
                    id = 1,
                    title = "A title",
                    mood = "neutral",
                    audioFilePath = "C:/tmp/a.mp4",
                    createdAt = 1_000L,
                    updatedAt = null,
                    description = "desc",
                    transcription = "hello world",
                    durationMs = 123L,
                    archived = false
                ),
                topics = topics
            )
        )

        val state = EntriesState(
            entriesWithTopics = logs,
            selectedMoods = listOf(Mood.Neutral, Mood.Excited),
            selectedTopics = listOf(topics[0]),
            query = "hello",
            fromDateMillis = 500L,
            toDateMillis = 1_500L,
            sortOrder = AudioLogSort.DateDescending
        )

        val params = state.toFilterParams()

        assertEquals(logs, params.currentLogs)
        assertEquals(listOf(Mood.Neutral, Mood.Excited), params.selectedMoods)
        assertEquals(listOf(1), params.selectedTopicIds)
        assertEquals("hello", params.query)
        assertEquals(500L, params.fromDateMillis)
        assertEquals(1_500L, params.toDateMillis)
        assertEquals(AudioLogSort.DateDescending, params.sortOrder)
    }
}
