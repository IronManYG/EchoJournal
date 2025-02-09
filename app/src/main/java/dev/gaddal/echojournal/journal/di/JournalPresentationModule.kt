package dev.gaddal.echojournal.journal.di

import dev.gaddal.echojournal.journal.entries.EntriesViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val journalPresentationModule = module {
    viewModelOf(::EntriesViewModel)
}
