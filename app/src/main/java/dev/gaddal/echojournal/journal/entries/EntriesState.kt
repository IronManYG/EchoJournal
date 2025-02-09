package dev.gaddal.echojournal.journal.entries

import dev.gaddal.echojournal.core.domain.logs.audio_log.AudioLogWithTopics
import dev.gaddal.echojournal.core.domain.logs.topic.Topic
import dev.gaddal.echojournal.core.domain.mood.Mood

data class EntriesState(
    val entriesWithTopics: List<AudioLogWithTopics> = emptyList(),
    val filterEntriesWithTopics: List<AudioLogWithTopics> = emptyList(),
    val selectedMoods: List<Mood> = emptyList(),
    val selectedTopics: List<Topic> = emptyList(),
    val allMoods: List<Mood> = Mood.all,
    val allTopics: List<Topic> = emptyList(),
)