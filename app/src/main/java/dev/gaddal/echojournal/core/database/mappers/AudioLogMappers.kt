package dev.gaddal.echojournal.core.database.mappers

import dev.gaddal.echojournal.core.database.entity.AudioLogEntity
import dev.gaddal.echojournal.core.database.entity.AudioLogWithTopicsRelation
import dev.gaddal.echojournal.core.domain.logs.audio_log.AudioLog
import dev.gaddal.echojournal.core.domain.logs.audio_log.AudioLogWithTopics

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

/** Helper to map from the DB relationship class to the domain class. */
fun AudioLogWithTopicsRelation.toAudioLogWithTopics(): AudioLogWithTopics {
    return AudioLogWithTopics(
        audioLog = audioLog.toAudioLog(),
        topics = topics.map { it.toTopic() }
    )
}