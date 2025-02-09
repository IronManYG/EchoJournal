package dev.gaddal.echojournal.core.domain.logs.filter

import dev.gaddal.echojournal.core.domain.logs.audio_log.AudioLogWithTopics

interface FilterAudioLog {
    fun execute(params: FilterAudioLogParams): List<AudioLogWithTopics>
}
