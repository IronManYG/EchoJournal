package dev.gaddal.echojournal.journal.entry

import dev.gaddal.echojournal.core.presentation.ui.UiText

interface EntryEvent {
    data class Error(val error: UiText) : EntryEvent
    data object SaveEntrySuccess : EntryEvent
}
