package dev.gaddal.echojournal.core.data.logs.audio_log_topic

import dev.gaddal.echojournal.core.domain.logs.audio_log_topic.AudioLogTopic
import dev.gaddal.echojournal.core.domain.logs.audio_log_topic.AudioLogTopicRepository
import dev.gaddal.echojournal.core.domain.logs.audio_log_topic.LocalAudioLogTopicDataSource
import dev.gaddal.echojournal.core.domain.util.DataError
import dev.gaddal.echojournal.core.domain.util.EmptyResult
import dev.gaddal.echojournal.core.domain.util.asEmptyDataResult

class OfflineFirstAudioLogTopicRepository(
    private val localAudioLogTopicDataSource: LocalAudioLogTopicDataSource,
) : AudioLogTopicRepository {

    override suspend fun upsertAudioLogTopic(audioLogTopic: AudioLogTopic): EmptyResult<DataError> {
        return localAudioLogTopicDataSource.upsertAudioLogTopic(audioLogTopic).asEmptyDataResult()
    }

    override suspend fun deleteAudioLogTopic(audioLogId: Int, topicId: Int) {
        localAudioLogTopicDataSource.deleteAudioLogTopic(audioLogId, topicId)
    }
}