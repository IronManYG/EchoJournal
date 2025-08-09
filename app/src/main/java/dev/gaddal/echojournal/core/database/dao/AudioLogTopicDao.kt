package dev.gaddal.echojournal.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import dev.gaddal.echojournal.core.database.entity.AudioLogTopicEntity

@Dao
interface AudioLogTopicDao {

    @Upsert
    suspend fun upsertAudioLogTopic(audioLogTopic: AudioLogTopicEntity)

    @Query("DELETE FROM audio_log_topic WHERE audioLogId = :audioLogId AND topicId = :topicId")
    suspend fun deleteAudioLogTopic(audioLogId: Int, topicId: Int)
}
