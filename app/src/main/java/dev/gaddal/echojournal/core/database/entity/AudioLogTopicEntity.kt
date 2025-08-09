package dev.gaddal.echojournal.core.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "audio_log_topic",
    primaryKeys = ["audioLogId", "topicId"],
    indices = [
        Index(value = ["audioLogId"]),
        Index(value = ["topicId"])
    ],
    foreignKeys = [
        ForeignKey(
            entity = AudioLogEntity::class,
            parentColumns = ["id"],
            childColumns = ["audioLogId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = TopicEntity::class,
            parentColumns = ["id"],
            childColumns = ["topicId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class AudioLogTopicEntity(
    val audioLogId: Int,
    val topicId: Int
)
