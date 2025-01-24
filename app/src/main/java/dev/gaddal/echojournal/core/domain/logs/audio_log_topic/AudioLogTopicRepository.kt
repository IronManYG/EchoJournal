package dev.gaddal.echojournal.core.domain.logs.audio_log_topic

import dev.gaddal.echojournal.core.domain.util.DataError
import dev.gaddal.echojournal.core.domain.util.EmptyResult

interface AudioLogTopicRepository {
    suspend fun upsertAudioLogTopic(audioLogTopic: AudioLogTopic): EmptyResult<DataError>

    suspend fun deleteAudioLogTopic(audioLogId: Int, topicId: Int)
}