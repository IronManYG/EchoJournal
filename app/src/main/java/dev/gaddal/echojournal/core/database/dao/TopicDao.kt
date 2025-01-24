package dev.gaddal.echojournal.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import dev.gaddal.echojournal.core.database.entity.TopicEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TopicDao {

    @Upsert
    suspend fun upsertTopic(topic: TopicEntity): Long

    @Query("SELECT * FROM topics")
    fun getTopics(): Flow<List<TopicEntity>>

    @Query("SELECT * FROM topics WHERE id = :id")
    fun getTopicById(id: Int): Flow<TopicEntity>

    @Query("DELETE FROM topics WHERE id = :id")
    suspend fun deleteTopicById(id: Int)
}