package dev.gaddal.echojournal.journal.di

import dev.gaddal.echojournal.core.domain.playback.AndroidAudioPlayer
import dev.gaddal.echojournal.core.domain.playback.AudioPlaybackTracker
import dev.gaddal.echojournal.core.domain.playback.AudioPlayer
import dev.gaddal.echojournal.core.domain.record.AndroidAudioRecorder
import dev.gaddal.echojournal.core.domain.record.AudioRecorder
import dev.gaddal.echojournal.core.domain.record.AudioRecordingTracker
import dev.gaddal.echojournal.core.domain.record.DefaultFileNameProvider
import dev.gaddal.echojournal.core.domain.record.FileNameProvider
import dev.gaddal.echojournal.core.presentation.ui.StoragePathProvider
import dev.gaddal.echojournal.journal.entries.EntriesViewModel
import dev.gaddal.echojournal.journal.entry.EntryViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val journalPresentationModule = module {
    singleOf(::AndroidAudioRecorder).bind<AudioRecorder>()
    single { AudioRecordingTracker(get(), get()) }

    singleOf(::AndroidAudioPlayer).bind<AudioPlayer>()
    single { AudioPlaybackTracker(get(), get()) }

    single { StoragePathProvider(androidContext()) }
    singleOf(::DefaultFileNameProvider).bind<FileNameProvider>()

    viewModelOf(::EntriesViewModel)
    viewModelOf(::EntryViewModel)
}

