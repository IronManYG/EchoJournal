package dev.gaddal.echojournal.core.domain.logs.audio_log_topic

import dev.gaddal.echojournal.core.domain.util.DataError
import dev.gaddal.echojournal.core.domain.util.Result

typealias AudioLogId = Int
typealias TopicId = Int

interface LocalAudioLogTopicDataSource {

    suspend fun upsertAudioLogTopic(audioLogTopic: AudioLogTopic): Result<Pair<AudioLogId, TopicId>, DataError.Local>

    suspend fun deleteAudioLogTopic(audioLogId: Int, topicId: Int)
}
