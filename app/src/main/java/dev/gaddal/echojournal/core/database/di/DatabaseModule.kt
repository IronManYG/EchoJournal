package dev.gaddal.echojournal.core.database.di

import androidx.room.Room
import dev.gaddal.echojournal.core.database.AudioLogDatabase
import dev.gaddal.echojournal.core.database.local_data_source.RoomLocalAudioLogDataSource
import dev.gaddal.echojournal.core.database.local_data_source.RoomLocalAudioLogTopicDataSource
import dev.gaddal.echojournal.core.database.local_data_source.RoomLocalTopicDataSource
import dev.gaddal.echojournal.core.domain.logs.audio_log.LocalAudioLogDataSource
import dev.gaddal.echojournal.core.domain.logs.audio_log_topic.LocalAudioLogTopicDataSource
import dev.gaddal.echojournal.core.domain.logs.topic.LocalTopicDataSource
import org.koin.android.ext.koin.androidApplication
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val databaseModule = module {
    single {
        Room.databaseBuilder(
            androidApplication(),
            AudioLogDatabase::class.java,
            "library.db"
        ).build()
    }

    single { get<AudioLogDatabase>().audioLogDao }
    single { get<AudioLogDatabase>().topicDao }
    single { get<AudioLogDatabase>().audioLogTopicDao }

    singleOf(::RoomLocalAudioLogDataSource).bind<LocalAudioLogDataSource>()
    singleOf(::RoomLocalTopicDataSource).bind<LocalTopicDataSource>()
    singleOf(::RoomLocalAudioLogTopicDataSource).bind<LocalAudioLogTopicDataSource>()
}
