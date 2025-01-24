package dev.gaddal.echojournal.core.domain.logs.audio_log

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
