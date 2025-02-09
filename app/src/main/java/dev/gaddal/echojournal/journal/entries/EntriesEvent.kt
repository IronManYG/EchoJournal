package dev.gaddal.echojournal.journal.entries

import dev.gaddal.echojournal.core.presentation.ui.UiText

interface EntriesEvent {
    data class Error(val error: UiText) : EntriesEvent
}