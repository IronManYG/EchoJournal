package dev.gaddal.echojournal.core.database.local_data_source

import android.database.sqlite.SQLiteFullException
import dev.gaddal.echojournal.core.database.dao.AudioLogTopicDao
import dev.gaddal.echojournal.core.database.mappers.toAudioLogTopicEntity
import dev.gaddal.echojournal.core.domain.logs.audio_log_topic.AudioLogId
import dev.gaddal.echojournal.core.domain.logs.audio_log_topic.AudioLogTopic
import dev.gaddal.echojournal.core.domain.logs.audio_log_topic.LocalAudioLogTopicDataSource
import dev.gaddal.echojournal.core.domain.logs.audio_log_topic.TopicId
import dev.gaddal.echojournal.core.domain.util.DataError
import dev.gaddal.echojournal.core.domain.util.Result

class RoomLocalAudioLogTopicDataSource(
    private val audioLogTopicDao: AudioLogTopicDao
) : LocalAudioLogTopicDataSource {
    override suspend fun upsertAudioLogTopic(
        audioLogTopic: AudioLogTopic
    ): Result<Pair<AudioLogId, TopicId>, DataError.Local> {
        return try {
            val entity = audioLogTopic.toAudioLogTopicEntity()
            audioLogTopicDao.upsertAudioLogTopic(entity)
            Result.Success(entity.audioLogId to entity.topicId)
        } catch (e: SQLiteFullException) {
            Result.Error(DataError.Local.DISK_FULL)
        }
    }

    override suspend fun deleteAudioLogTopic(audioLogId: Int, topicId: Int) {
        audioLogTopicDao.deleteAudioLogTopic(audioLogId, topicId)
    }
}
