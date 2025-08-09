package dev.gaddal.echojournal.core.database.mappers

import dev.gaddal.echojournal.core.database.entity.TopicEntity
import dev.gaddal.echojournal.core.domain.logs.topic.Topic

fun TopicEntity.toTopic(): Topic {
    return Topic(
        id = id,
        name = name,
        colorHex = colorHex
    )
}

fun Topic.toTopicEntity(): TopicEntity {
    return TopicEntity(
        name = name,
        colorHex = colorHex
    )
}
