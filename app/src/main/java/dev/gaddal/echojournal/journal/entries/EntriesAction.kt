package dev.gaddal.echojournal.journal.entries

import dev.gaddal.echojournal.core.domain.logs.filter.AudioLogSort
import dev.gaddal.echojournal.core.domain.logs.topic.Topic
import dev.gaddal.echojournal.core.domain.mood.Mood

sealed interface EntriesAction {
    data class OnCreateTopicClick(val topic: Topic) : EntriesAction
    data class OnMoodSelected(val mood: Mood) : EntriesAction
    data class OnTopicSelected(val topic: Topic) : EntriesAction
    data object OnClearMoodFilter : EntriesAction
    data object OnClearTopicFilter : EntriesAction
    data object OnSettingsClick : EntriesAction
    data object OnCreateNewEntryTrigger : EntriesAction
    data class OnEntryClick(val id: Int) : EntriesAction

    // Filtering parameters
    data class OnQueryChanged(val query: String) : EntriesAction
    data class OnDateRangeChanged(val fromDateMillis: Long?, val toDateMillis: Long?) : EntriesAction
    data class OnSortOrderChanged(val sortOrder: AudioLogSort) : EntriesAction

    // Recording actions
    data object OnStartRecordingClick : EntriesAction
    data object OnPauseRecordingClick : EntriesAction
    data object OnResumeRecordingClick : EntriesAction
    data object OnFinishRecordingClick : EntriesAction
    data object OnCancelRecordingClick : EntriesAction

    // Permission
    data class SubmitAudioPermissionInfo(
        val acceptedAudioPermission: Boolean,
        val showAudioRationale: Boolean
    ) : EntriesAction

    data object OnDismissRationaleDialog : EntriesAction

    // PlayBack actions
    data class PlayAudio(val filePath: String, val logId: Int) : EntriesAction
    data object PauseAudio : EntriesAction
    data object ResumeAudio : EntriesAction
    data object StopAudio : EntriesAction
    data class SeekTo(val ms: Int) : EntriesAction
}