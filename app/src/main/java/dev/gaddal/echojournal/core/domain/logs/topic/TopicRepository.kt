package dev.gaddal.echojournal.core.domain.logs.topic

import dev.gaddal.echojournal.core.domain.util.DataError
import dev.gaddal.echojournal.core.domain.util.EmptyResult
import kotlinx.coroutines.flow.Flow

interface TopicRepository {

    fun getTopics(): Flow<List<Topic>>

    fun getTopicById(id: topicId): Flow<Topic?>

    suspend fun upsertTopic(topic: Topic): EmptyResult<DataError>

    suspend fun deleteTopicById(id: topicId)
}