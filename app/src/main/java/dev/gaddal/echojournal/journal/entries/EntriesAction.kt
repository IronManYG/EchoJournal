package dev.gaddal.echojournal.journal.entries

import dev.gaddal.echojournal.core.domain.logs.topic.Topic
import dev.gaddal.echojournal.core.domain.mood.Mood

sealed interface EntriesAction {
    data class OnCreateTopicClick(val topic: Topic) : EntriesAction
    data class OnMoodSelected(val mood: Mood) : EntriesAction
    data class OnTopicSelected(val topic: Topic) : EntriesAction
    data object OnClearMoodFilter : EntriesAction
    data object OnClearTopicFilter : EntriesAction
    data object OnFabClick : EntriesAction
    data class OnEntryClick(val id: Int) : EntriesAction
}