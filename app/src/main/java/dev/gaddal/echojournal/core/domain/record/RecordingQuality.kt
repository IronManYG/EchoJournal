package dev.gaddal.echojournal.core.domain.record

enum class RecordingQuality(
    val bitRate: Int,
    val sampleRate: Int
) {
    LOW(bitRate = 64_000, sampleRate = 16_000),
    MEDIUM(bitRate = 96_000, sampleRate = 32_000),
    HIGH(bitRate = 128_000, sampleRate = 44_100),
    VERY_HIGH(bitRate = 256_000, sampleRate = 48_000)
}
