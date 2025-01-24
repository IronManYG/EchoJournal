package dev.gaddal.echojournal.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "audio_logs")
data class AudioLogEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val title: String,
    val mood: String,
    val audioFilePath: String,

    /**
     * The timestamp (in millis) when this log was created
     */
    val createdAt: Long,

    /**
     * The timestamp (in millis) when this log was last updated
     * (if the user edited the log's title, mood, or description).
     */
    val updatedAt: Long? = null,

    /**
     * Optional text description for additional notes or thoughts.
     */
    val description: String? = null,

    /**
     * Optional transcription if AI or manual transcription is used.
     */
    val transcription: String? = null,

    /**
     * Duration of the audio in milliseconds (if you want to store it).
     */
    val durationMs: Long? = null,

    /**
     * Indicates whether the log is archived or hidden from the main list.
     */
    val archived: Boolean = false
)
