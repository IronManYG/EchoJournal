package dev.gaddal.echojournal.core.domain.record

import java.io.File

interface AudioRecorder {
    fun start(
        outputFile: File,
        desiredQuality: RecordingQuality = RecordingQuality.MEDIUM
    )
    fun stop()
    fun pause()
    fun resume()
}
