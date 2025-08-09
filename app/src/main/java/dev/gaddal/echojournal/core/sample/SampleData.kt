package dev.gaddal.echojournal.core.sample

import dev.gaddal.echojournal.core.domain.logs.audio_log.AudioLog
import dev.gaddal.echojournal.core.domain.logs.audio_log.AudioLogWithTopics
import dev.gaddal.echojournal.core.domain.logs.topic.Topic
import dev.gaddal.echojournal.core.sample.SampleData.sampleTopics

/**
 * An object holding sample or mock data for demonstration or testing.
 * Storing sample data here can be useful for UI previews or
 * quick prototypes without relying on a backend or database.
 */
object SampleData {

    // For readability, let's define day & month approximations in milliseconds.
    private const val ONE_DAY_MS = 24L * 60L * 60L * 1000L
    private const val DAYS_PER_MONTH = 30L
    private const val FOUR_MONTHS_DAYS = 4 * DAYS_PER_MONTH // ~120 days

    // Current time for reference.
    private val now = System.currentTimeMillis()

    // We can store commonly used timestamps for convenience.
    private val createdToday = now
    private val created1DayAgo = now - 1 * ONE_DAY_MS
    private val created2DaysAgo = now - 2 * ONE_DAY_MS
    private val created3DaysAgo = now - 3 * ONE_DAY_MS
    private val created5DaysAgo = now - 5 * ONE_DAY_MS
    private val created10DaysAgo = now - 10 * ONE_DAY_MS
    private val created4MonthsAgo = now - FOUR_MONTHS_DAYS * ONE_DAY_MS

    /**
     * A list of [AudioLog] instances demonstrating
     * various timestamps, moods, and descriptions.
     */
    val sampleAudioLogs = listOf(
        AudioLog(
            id = 1,
            title = "Morning Reflection",
            mood = "neutral",
            audioFilePath = "/path/to/audio1.mp3",
            createdAt = createdToday, // "Today"
            updatedAt = null,
            description = """
                Lorem Ipsum is simply dummy text of the printing and typesetting industry.
                It has survived not only five centuries, but also the leap into 
                electronic typesetting, remaining essentially unchanged.
            """.trimIndent(),
            transcription = null,
            durationMs = null,
            archived = false
        ),
        AudioLog(
            id = 2,
            title = "Quick Thought",
            mood = "sad",
            audioFilePath = "/path/to/audio2.mp3",
            createdAt = created1DayAgo, // 1 day ago
            updatedAt = null,
            description = "Short reflection from yesterday.",
            transcription = null,
            durationMs = null,
            archived = false
        ),
        AudioLog(
            id = 3,
            title = "Day's Summary",
            mood = "stressed",
            audioFilePath = "/path/to/audio3.mp3",
            createdAt = created2DaysAgo,
            updatedAt = null,
            description = "Brief summary from 2 days ago.",
            transcription = null,
            durationMs = null,
            archived = false
        ),
        AudioLog(
            id = 4,
            title = "Quick Thought",
            mood = "excited",
            audioFilePath = "/path/to/audio5.mp3",
            createdAt = created3DaysAgo,
            updatedAt = null,
            description = """
                Medium-length text describing reflections or ideas 
                captured around 3 days ago.
            """.trimIndent(),
            transcription = null,
            durationMs = null,
            archived = false
        ),
        AudioLog(
            id = 5,
            title = "Morning Reflection",
            mood = "peaceful",
            audioFilePath = "/path/to/audio4.mp3",
            createdAt = created5DaysAgo,
            updatedAt = null,
            description = "",
            transcription = null,
            durationMs = null,
            archived = false
        ),
        // Additional logs with older timestamps
        AudioLog(
            id = 6,
            title = "10-Day Retrospective",
            mood = "neutral",
            audioFilePath = "/path/to/audio6.mp3",
            createdAt = created10DaysAgo, // ~10 days ago
            updatedAt = null,
            description = """
                Reflections from about 10 days in the past. 
                This log describes changes or events over the last week and a half.
            """.trimIndent(),
            transcription = null,
            durationMs = null,
            archived = false
        ),
        AudioLog(
            id = 7,
            title = "Four-Month Check-In",
            mood = "stressed",
            audioFilePath = "/path/to/audio7.mp3",
            createdAt = created4MonthsAgo, // ~4 months ago
            updatedAt = null,
            description = """
                A more extended period reflection, describing events, progress, or 
                setbacks over the last few months.
            """.trimIndent(),
            transcription = null,
            durationMs = null,
            archived = false
        ),

        // --------------------------------------------------------------------------------------
        // 6 more AudioLogs that randomly reuse the createdAt values above.
        // --------------------------------------------------------------------------------------

        AudioLog(
            id = 8,
            title = "Another 3-Day Thought",
            mood = "sad",
            audioFilePath = "/path/to/audio8.mp3",
            createdAt = created3DaysAgo, // Same as id=4
            updatedAt = null,
            description = "Another log recorded about 3 days ago.",
            transcription = null,
            durationMs = null,
            archived = false
        ),
        AudioLog(
            id = 9,
            title = "Second 10-Day Reflection",
            mood = "neutral",
            audioFilePath = "/path/to/audio9.mp3",
            createdAt = created10DaysAgo, // Same as id=6
            updatedAt = null,
            description = "Revisiting thoughts from 10 days ago.",
            transcription = null,
            durationMs = null,
            archived = false
        ),
        AudioLog(
            id = 10,
            title = "Alternative Yesterday's Thought",
            mood = "excited",
            audioFilePath = "/path/to/audio10.mp3",
            createdAt = created1DayAgo, // Same as id=2
            updatedAt = null,
            description = "A second quick thought from yesterday.",
            transcription = null,
            durationMs = null,
            archived = false
        ),
        AudioLog(
            id = 11,
            title = "Another Day's Summary",
            mood = "sad",
            audioFilePath = "/path/to/audio11.mp3",
            createdAt = created2DaysAgo, // Same as id=3
            updatedAt = null,
            description = "A second log from 2 days ago with a different mood.",
            transcription = null,
            durationMs = null,
            archived = false
        ),
        AudioLog(
            id = 12,
            title = "Fresh Morning Check-In",
            mood = "peaceful",
            audioFilePath = "/path/to/audio12.mp3",
            createdAt = createdToday, // Same as id=1
            updatedAt = null,
            description = """
                A new reflection from the same day as id=1, 
                focusing on different topics or insights.
            """.trimIndent(),
            transcription = null,
            durationMs = null,
            archived = false
        ),
        AudioLog(
            id = 13,
            title = "5-Day Revisit",
            mood = "neutral",
            audioFilePath = "/path/to/audio13.mp3",
            createdAt = created5DaysAgo, // Same as id=5
            updatedAt = null,
            description = "Another log from 5 days ago.",
            transcription = null,
            durationMs = null,
            archived = false
        ),
    )

    /**
     * A list of [Topic] instances showing a few basic topics
     * that might be associated with an AudioLog.
     */
    val sampleTopics = listOf(
        Topic(
            id = 1,
            name = "Work",
            colorHex = null
        ),
        Topic(
            id = 2,
            name = "Family",
            colorHex = null
        ),
        Topic(
            id = 3,
            name = "Love",
            colorHex = null
        ),
        Topic(
            id = 4,
            name = "Conundrums",
            colorHex = null
        ),
        Topic(
            id = 5,
            name = "Health",
            colorHex = null
        ),
        // Additional sample topics
        Topic(
            id = 6,
            name = "Travel",
            colorHex = null
        ),
        Topic(
            id = 7,
            name = "Food",
            colorHex = null
        ),
        Topic(
            id = 8,
            name = "Hobby",
            colorHex = null
        )
    )

    /**
     * Returns a list of [AudioLogWithTopics] by pairing each [AudioLog] with
     * a random subset of topics from [sampleTopics].
     *
     * Every call to this property will shuffle [sampleTopics] and pick a
     * random number of them (between 1 and the total size).
     *
     * Example usage:
     * ```
     * val logsWithTopics = SampleData.sampleAudioLogsWithTopics
     * ```
     */
    val sampleAudioLogsWithTopics: List<AudioLogWithTopics>
        get() = sampleAudioLogs.map { audioLog ->
            AudioLogWithTopics(
                audioLog = audioLog,
                topics = sampleTopics.shuffled()
                    .take((1..sampleTopics.size).random())
            )
        }
}
