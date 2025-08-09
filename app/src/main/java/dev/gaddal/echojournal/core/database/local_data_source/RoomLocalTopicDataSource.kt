package dev.gaddal.echojournal.core.database.local_data_source

import android.database.sqlite.SQLiteFullException
import dev.gaddal.echojournal.core.database.dao.TopicDao
import dev.gaddal.echojournal.core.database.mappers.toTopic
import dev.gaddal.echojournal.core.database.mappers.toTopicEntity
import dev.gaddal.echojournal.core.domain.logs.topic.LocalTopicDataSource
import dev.gaddal.echojournal.core.domain.logs.topic.Topic
import dev.gaddal.echojournal.core.domain.logs.topic.topicId
import dev.gaddal.echojournal.core.domain.util.DataError
import dev.gaddal.echojournal.core.domain.util.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RoomLocalTopicDataSource(
    private val topicDao: TopicDao
) : LocalTopicDataSource {
    override fun getTopics(): Flow<List<Topic>> {
        return topicDao.getTopics()
            .map { topicsEntities ->
                topicsEntities.map { it.toTopic() }
            }
    }

    override fun getTopicById(id: topicId): Flow<Topic?> {
        return topicDao.getTopicById(id).map { it.toTopic() }
    }

    override suspend fun upsertTopic(topic: Topic): Result<topicId, DataError.Local> {
        return try {
            val entity = topic.toTopicEntity()
            topicDao.upsertTopic(entity)
            Result.Success(entity.id)
        } catch (e: SQLiteFullException) {
            Result.Error(DataError.Local.DISK_FULL)
        }
    }

    override suspend fun deleteTopicById(id: topicId) {
        topicDao.deleteTopicById(id)
    }
}
