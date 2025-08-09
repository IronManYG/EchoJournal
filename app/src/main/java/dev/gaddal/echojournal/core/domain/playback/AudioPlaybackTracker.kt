package dev.gaddal.echojournal.core.domain.playback

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import timber.log.Timber

class AudioPlaybackTracker(
    private val audioPlayer: AudioPlayer,
    private val applicationScope: CoroutineScope
) {
    private val _isPlaying = MutableStateFlow(false)
    val isPlaying = _isPlaying.asStateFlow()

    private val _isPaused = MutableStateFlow(false)
    val isPaused = _isPaused.asStateFlow()

    private val _currentPosition = MutableStateFlow(Duration.ZERO)
    val currentPosition = _currentPosition.asStateFlow()

    private val _duration = MutableStateFlow(Duration.ZERO)
    val duration = _duration.asStateFlow()

    private var progressJob: Job? = null

    fun playFile(file: File) {
        Timber.tag("AudioPlaybackTracker").d("playFile: ${file.absolutePath}")
        // Stop current playback (if any), so we don't overlap
        stop()

        // 1) Set up a completion callback so we know when the track ends
        audioPlayer.setOnPlaybackCompleteListener {
            Timber.tag("AudioPlaybackTracker").d("onPlaybackComplete")
            // This runs *only* when MediaPlayer has finished playing
            stop() // resets _isPlaying, etc.
        }

        // Instead of setting _isPlaying = true right away, do it once the MediaPlayer is prepared:
        audioPlayer.setOnPreparedListener {
            _isPlaying.value = true
            _isPaused.value = false

            // At this point, duration is guaranteed valid:
            _duration.value = audioPlayer.duration.milliseconds
            Timber.tag("AudioPlaybackTracker").d("onPrepared: duration=${'$'}{_duration.value}")

            // Now launch the progress polling
            startPolling()
        }

        audioPlayer.playFile(file)
    }

    private fun startPolling() {
        Timber.tag("AudioPlaybackTracker").d("startPolling")
        // Start polling the player's current position
        progressJob = applicationScope.launch {
            while (_isPlaying.value && !_isPaused.value) {
                val posMs = audioPlayer.currentPosition // safe now
                _currentPosition.value = posMs.milliseconds
                delay(200)
            }
        }
    }

    fun pause() {
        if (!_isPlaying.value || _isPaused.value) return
        Timber.tag("AudioPlaybackTracker").d("pause")
        audioPlayer.pause()
        _isPaused.value = true
        progressJob?.cancel() // Stop polling
    }

    fun resume() {
        if (!_isPlaying.value || !_isPaused.value) return
        Timber.tag("AudioPlaybackTracker").d("resume")
        audioPlayer.resume()
        _isPaused.value = false

        // Resume polling
        progressJob = applicationScope.launch {
            while (_isPlaying.value && !_isPaused.value) {
                val posMs = audioPlayer.currentPosition
                _currentPosition.value = posMs.milliseconds
                delay(200)
            }
        }
    }

    fun seekTo(positionMs: Int) {
        Timber.tag("AudioPlaybackTracker").d("seekTo: ${positionMs}ms")
        audioPlayer.seekTo(positionMs)
        _currentPosition.value = positionMs.milliseconds
    }

    fun stop() {
        if (!_isPlaying.value) return
        Timber.tag("AudioPlaybackTracker").d("stop")
        audioPlayer.stop() // releases mediaPlayer
        _isPlaying.value = false
        _isPaused.value = false
        progressJob?.cancel()
        progressJob = null
        _currentPosition.value = Duration.ZERO
//        _duration.value = Duration.ZERO
    }
}
