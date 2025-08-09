package dev.gaddal.echojournal.journal.entries

import dev.gaddal.echojournal.core.domain.logs.audio_log.AudioLogWithTopics
import dev.gaddal.echojournal.core.domain.logs.filter.AudioLogSort
import dev.gaddal.echojournal.core.domain.logs.topic.Topic
import dev.gaddal.echojournal.core.domain.mood.Mood
import kotlin.time.Duration

 data class EntriesState(
    val entriesWithTopics: List<AudioLogWithTopics> = emptyList(),
    val filterEntriesWithTopics: List<AudioLogWithTopics> = emptyList(),
    val selectedMoods: List<Mood> = emptyList(),
    val selectedTopics: List<Topic> = emptyList(),
    val allMoods: List<Mood> = Mood.all,
    val allTopics: List<Topic> = emptyList(),

    // Filter params in UI state
    val query: String = "",
    val fromDateMillis: Long? = null,
    val toDateMillis: Long? = null,
    val sortOrder: AudioLogSort = AudioLogSort.DateAscending,

    // Permission flags
    val hasRecordPermission: Boolean = false,
    val showRecordRationale: Boolean = false,

    // Recording flags
    val elapsedTime: Duration = Duration.ZERO,
    val isRecording: Boolean = false,   // true if actually collecting time
    val isPaused: Boolean = false,      // true if user explicitly paused

    // Playback flags (global audio player)
    val isPlayingAudio: Boolean = false,
    val isPausedAudio: Boolean = false,
    val audioPosition: Duration = Duration.ZERO,
    val audioDuration: Duration = Duration.ZERO,

    // NEW: Which AudioLog ID is currently playing? null if none
    val nowPlayingLogId: Int? = null
)