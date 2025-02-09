package dev.gaddal.echojournal.core.domain.logs.audio_log

import dev.gaddal.echojournal.core.domain.mood.Mood

/**
 * Data class representing an audio log entry.
 *
 * @property id Unique identifier for the log.
 * @property title Title or summary of the log.
 * @property mood The user's mood at the time of the log (string form).
 * @property audioFilePath File path where the audio is stored.
 * @property createdAt The timestamp (in epoch milliseconds) when this log was created.
 * @property updatedAt The timestamp (in epoch milliseconds) when this log was last updated (nullable).
 * @property description An optional text description of the audio log.
 * @property transcription A text transcription of the audio content.
 * @property durationMs The duration of the audio in milliseconds (nullable).
 * @property archived Boolean indicating if the log has been archived.
 */
data class AudioLog(
    val id: Int,
    val title: String,
    val mood: String,
    val audioFilePath: String,
    val createdAt: Long,
    val updatedAt: Long? = null,
    val description: String? = null,
    val transcription: String? = null,
    val durationMs: Long? = null,
    val archived: Boolean
)

/**
 * Converts a string representing mood into a [Mood] enum.
 *
 * If the provided string does not match any known mood, this defaults to [Mood.Neutral].
 */
fun String.toMoodOrDefault(): Mood {
    return when (this.lowercase()) {
        "sad" -> Mood.Sad
        "stressed" -> Mood.Stressed
        "neutral" -> Mood.Neutral
        "peaceful" -> Mood.Peaceful
        "excited" -> Mood.Excited
        else -> Mood.Neutral // fallback if unknown
    }
}