package dev.gaddal.echojournal.core.data.di

import dev.gaddal.echojournal.core.data.logs.audio_log.OfflineFirstAudioLogRepository
import dev.gaddal.echojournal.core.data.logs.audio_log_topic.OfflineFirstAudioLogTopicRepository
import dev.gaddal.echojournal.core.data.logs.filter.FilterAudioLogImpl
import dev.gaddal.echojournal.core.data.logs.topic.OfflineFirstTopicRepository
import dev.gaddal.echojournal.core.domain.logs.audio_log.AudioLogRepository
import dev.gaddal.echojournal.core.domain.logs.audio_log_topic.AudioLogTopicRepository
import dev.gaddal.echojournal.core.domain.logs.filter.FilterAudioLog
import dev.gaddal.echojournal.core.domain.logs.topic.TopicRepository
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val coreDataModule = module {
    singleOf(::OfflineFirstAudioLogRepository).bind(AudioLogRepository::class)
    singleOf(::OfflineFirstTopicRepository).bind(TopicRepository::class)
    singleOf(::OfflineFirstAudioLogTopicRepository).bind(AudioLogTopicRepository::class)

    singleOf(::FilterAudioLogImpl).bind<FilterAudioLog>()
}