package dev.gaddal.echojournal.core.domain.playback

import android.media.MediaPlayer
import java.io.File

class AndroidAudioPlayer : AudioPlayer {

    private var mediaPlayer: MediaPlayer? = null
    private var onPrepared: (() -> Unit)? = null
    private var onComplete: (() -> Unit)? = null

    // Track whether MediaPlayer has finished preparing
    private var isPrepared = false

    override val isPlaying: Boolean
        get() = mediaPlayer?.isPlaying == true

    override val currentPosition: Int
        get() {
            val mp = mediaPlayer ?: return 0
            if (!isPrepared) return 0
            return try {
                mp.currentPosition
            } catch (e: IllegalStateException) {
                0
            }
        }

    override val duration: Int
        get() {
            val mp = mediaPlayer ?: return 0
            if (!isPrepared) return 0
            return try {
                mp.duration
            } catch (e: IllegalStateException) {
                0
            }
        }

    override fun playFile(file: File) {
        stop() // Stop/cleanup any existing playback first

        isPrepared = false
        mediaPlayer = MediaPlayer().apply {
            setDataSource(file.absolutePath)
            setOnPreparedListener {
                isPrepared = true
                // Now it's safe to query currentPosition/duration
                start() // actually begin playback
                onPrepared?.invoke() // optional: let others know we're ready
            }
            setOnCompletionListener {
                // When MediaPlayer is truly done:
                onComplete?.invoke()
                // Optional: handle auto-stop or next track
            }
            prepareAsync()
        }
    }

    override fun pause() {
        mediaPlayer?.takeIf { it.isPlaying }?.pause()
    }

    override fun resume() {
        mediaPlayer?.takeIf { !it.isPlaying }?.start()
    }

    override fun stop() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
        isPrepared = false
    }

    override fun seekTo(positionMs: Int) {
        mediaPlayer?.seekTo(positionMs)
    }

    override fun setOnPreparedListener(listener: () -> Unit) {
        onPrepared = listener
    }

    override fun setOnPlaybackCompleteListener(listener: () -> Unit) {
        onComplete = listener
    }
}
