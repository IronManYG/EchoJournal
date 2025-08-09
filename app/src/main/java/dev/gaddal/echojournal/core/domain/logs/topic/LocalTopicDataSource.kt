package dev.gaddal.echojournal.core.domain.logs.topic

import dev.gaddal.echojournal.core.domain.util.DataError
import dev.gaddal.echojournal.core.domain.util.Result
import kotlinx.coroutines.flow.Flow

interface LocalTopicDataSource {

    fun getTopics(): Flow<List<Topic>>

    fun getTopicById(id: topicId): Flow<Topic?>

    suspend fun upsertTopic(topic: Topic): Result<topicId, DataError.Local>

    suspend fun deleteTopicById(id: topicId)
}

typealias topicId = Int
