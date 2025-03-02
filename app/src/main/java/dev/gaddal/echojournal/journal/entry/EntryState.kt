package dev.gaddal.echojournal.journal.entry

import androidx.compose.foundation.text.input.TextFieldState
import dev.gaddal.echojournal.core.domain.logs.topic.Topic
import dev.gaddal.echojournal.core.domain.mood.Mood
import kotlin.time.Duration

data class EntryState(
    val mood: Mood? = null,
    val allMoods: List<Mood> = Mood.all,
    val title: TextFieldState = TextFieldState(),
    val topic: Topic? = null,
    val allTopics: List<Topic> = emptyList(),
    val filteredTopics: List<Topic> = emptyList(),
    val selectedTopics : List<Topic> = emptyList(),
    val topicQuery: TextFieldState = TextFieldState(),
    val description: TextFieldState = TextFieldState(),
    val transcription: String = "",

    // Playback flags (global audio player)
    val isPlayingAudio: Boolean = false,
    val isPausedAudio: Boolean = false,
    val audioPosition: Duration = Duration.ZERO,
    val audioDuration: Duration = Duration.ZERO,
)
