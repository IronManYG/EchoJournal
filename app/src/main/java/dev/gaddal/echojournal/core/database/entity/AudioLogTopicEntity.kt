package dev.gaddal.echojournal.core.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    tableName = "audio_log_topic",
    primaryKeys = ["audioLogId", "topicId"],
    foreignKeys = [
        ForeignKey(
            entity = AudioLogEntity::class,
            parentColumns = ["id"],
            childColumns = ["audioLogId"],
        ),
        ForeignKey(
            entity = TopicEntity::class,
            parentColumns = ["id"],
            childColumns = ["topicId"],
        )
    ]
)
data class AudioLogTopicEntity(
    val audioLogId: Int,
    val topicId: Int
)
