package dev.gaddal.echojournal.core.database.entity

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

/**
 * Data class used by Room to load an AudioLog with its associated Topics.
 * Not an actual @Entity, just a POJO for the relationship.
 */
data class AudioLogWithTopicsRelation(
    @Embedded
    val audioLog: AudioLogEntity,

    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = AudioLogTopicEntity::class,
            parentColumn = "audioLogId",
            entityColumn = "topicId"
        )
    )
    val topics: List<TopicEntity>
)
