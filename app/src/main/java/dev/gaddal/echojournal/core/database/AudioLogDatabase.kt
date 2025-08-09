package dev.gaddal.echojournal.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import dev.gaddal.echojournal.core.database.dao.AudioLogDao
import dev.gaddal.echojournal.core.database.dao.AudioLogTopicDao
import dev.gaddal.echojournal.core.database.dao.TopicDao
import dev.gaddal.echojournal.core.database.entity.AudioLogEntity
import dev.gaddal.echojournal.core.database.entity.AudioLogTopicEntity
import dev.gaddal.echojournal.core.database.entity.TopicEntity

@Database(
    entities = [
        AudioLogEntity::class,
        TopicEntity::class,
        AudioLogTopicEntity::class
    ],
    version = 1
)
abstract class AudioLogDatabase : RoomDatabase() {

    abstract val audioLogDao: AudioLogDao
    abstract val topicDao: TopicDao
    abstract val audioLogTopicDao: AudioLogTopicDao
}
