package dev.gaddal.echojournal.core.domain.logs.audio_log

import dev.gaddal.echojournal.core.domain.logs.topic.Topic

/**
 * Our domain model combining an AudioLog plus its list of Topics.
 */
data class AudioLogWithTopics(
    val audioLog: AudioLog,
    val topics: List<Topic>
)
