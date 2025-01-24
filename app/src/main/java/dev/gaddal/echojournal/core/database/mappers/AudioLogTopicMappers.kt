package dev.gaddal.echojournal.core.database.mappers

import dev.gaddal.echojournal.core.database.entity.AudioLogTopicEntity
import dev.gaddal.echojournal.core.domain.logs.audio_log_topic.AudioLogTopic

fun AudioLogTopicEntity.toAudioLogTopic(): AudioLogTopic {
    return AudioLogTopic(
        audioLogId = audioLogId,
        topicId = topicId
    )
}

fun AudioLogTopic.toAudioLogTopicEntity(): AudioLogTopicEntity {
    return AudioLogTopicEntity(
        audioLogId = audioLogId,
        topicId = topicId
    )
}
