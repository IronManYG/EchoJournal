package dev.gaddal.echojournal.journal.entry

import dev.gaddal.echojournal.core.domain.logs.topic.Topic
import dev.gaddal.echojournal.core.domain.mood.Mood

sealed interface EntryAction {
    data object OnBackClick : EntryAction
    data object OnSaveNewEntryClick : EntryAction
    data object OnCancelNewEntryClick : EntryAction
    data class OnConfirmChooseMoodClick(val chosenMood: Mood) : EntryAction
    data object OnCancelChooseMoodClick : EntryAction

    // PlayBack actions
    data object PlayAudio : EntryAction
    data object PauseAudio : EntryAction
    data object ResumeAudio : EntryAction
    data object StopAudio : EntryAction
    data class SeekTo(val ms: Int) : EntryAction

    data class OnTopicFieldFocusChange(val isFocused: Boolean) : EntryAction
    data class OnTopicSelected(val topic: Topic) : EntryAction
    data object OnCreateTopic : EntryAction
    data class OnClearTopicClick(val topic: Topic) : EntryAction
}