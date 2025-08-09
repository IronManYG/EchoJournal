package dev.gaddal.echojournal.core.data.logs.topic

import dev.gaddal.echojournal.core.domain.logs.topic.LocalTopicDataSource
import dev.gaddal.echojournal.core.domain.logs.topic.Topic
import dev.gaddal.echojournal.core.domain.logs.topic.TopicRepository
import dev.gaddal.echojournal.core.domain.logs.topic.topicId
import dev.gaddal.echojournal.core.domain.util.DataError
import dev.gaddal.echojournal.core.domain.util.EmptyResult
import dev.gaddal.echojournal.core.domain.util.asEmptyDataResult
import kotlinx.coroutines.flow.Flow

class OfflineFirstTopicRepository(
    private val localTopicDataSource: LocalTopicDataSource,
) : TopicRepository {
    override fun getTopics(): Flow<List<Topic>> {
        return localTopicDataSource.getTopics()
    }

    override fun getTopicById(id: topicId): Flow<Topic?> {
        return localTopicDataSource.getTopicById(id)
    }

    override suspend fun upsertTopic(topic: Topic): EmptyResult<DataError> {
        return localTopicDataSource.upsertTopic(topic).asEmptyDataResult()
    }

    override suspend fun deleteTopicById(id: topicId) {
        localTopicDataSource.deleteTopicById(id)
    }
}
