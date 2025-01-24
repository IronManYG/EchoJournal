package dev.gaddal.echojournal.core.database.mappers

import dev.gaddal.echojournal.core.database.entity.AudioLogEntity
import dev.gaddal.echojournal.core.domain.logs.audio_log.AudioLog

fun AudioLogEntity.toAudioLog(): AudioLog {
    return AudioLog(
        id = id,
        title = title,
        mood = mood,
        audioFilePath = audioFilePath,
        createdAt = createdAt,
        updatedAt = updatedAt,
        description = description,
        transcription = transcription,
        durationMs = durationMs,
        archived = archived
    )
}

fun AudioLog.toAudioLogEntity(): AudioLogEntity {
    return AudioLogEntity(
        id = id,
        title = title,
        mood = mood,
        audioFilePath = audioFilePath,
        createdAt = createdAt,
        updatedAt = updatedAt,
        description = description,
        transcription = transcription,
        durationMs = durationMs,
        archived = archived
    )
}