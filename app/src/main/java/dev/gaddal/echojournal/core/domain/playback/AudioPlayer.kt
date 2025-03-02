package dev.gaddal.echojournal.core.domain.playback

import java.io.File

interface AudioPlayer {
    /** Begin playing the given file from start. */
    fun playFile(file: File)

    /** Pause playback, if currently playing. */
    fun pause()

    /** Resume playback, if paused. */
    fun resume()

    /** Stop playback entirely (release resources if needed). */
    fun stop()

    /** Seek to the given position in milliseconds. */
    fun seekTo(positionMs: Int)

    fun setOnPreparedListener(listener: () -> Unit)

    fun setOnPlaybackCompleteListener(listener: () -> Unit)

    /** Current playback position in milliseconds. */
    val currentPosition: Int

    /** Total duration of the audio in milliseconds. */
    val duration: Int

    /** Is the player currently playing or actively outputting audio. */
    val isPlaying: Boolean
}
