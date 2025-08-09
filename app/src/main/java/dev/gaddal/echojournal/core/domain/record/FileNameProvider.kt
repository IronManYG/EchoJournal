package dev.gaddal.echojournal.core.domain.record

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

/**
 * Provides names for newly created audio files.
 * Strategy: ej_yyyyMMdd_HHmmss_<uuid>.mp4
 */
interface FileNameProvider {
    fun newAudioFileName(): String
}

class DefaultFileNameProvider : FileNameProvider {
    private val dateFormat = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US)

    override fun newAudioFileName(): String {
        val ts = dateFormat.format(Date())
        val uuid = UUID.randomUUID().toString().substring(0, 8)
        return "ej_${ts}_$uuid.mp4"
    }
}
