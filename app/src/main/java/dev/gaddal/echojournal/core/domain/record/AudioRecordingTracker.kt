@file:OptIn(ExperimentalCoroutinesApi::class)

package dev.gaddal.echojournal.core.domain.record

import dev.gaddal.echojournal.core.domain.util.Timer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import java.io.File
import kotlin.time.Duration

class AudioRecordingTracker(
    private val audioRecorder: AudioRecorder,
    private val applicationScope: CoroutineScope
) {
    // The raw states
    private val _isRecording = MutableStateFlow(false)
    val isRecording = _isRecording.asStateFlow()

    private val _isPaused = MutableStateFlow(false)
    val isPaused = _isPaused.asStateFlow()

    private val _elapsedTime = MutableStateFlow(Duration.ZERO)
    val elapsedTime = _elapsedTime.asStateFlow()

    init {
        // Combine isRecording + isPaused => only emit `true` when recording && !paused
        val isRecordingAndNotPaused = combine(_isRecording, _isPaused) { rec, paused ->
            rec && !paused
        }.distinctUntilChanged()

        // If isRecordingAndNotPaused == true, collect Timer.timeAndEmit(); else no emissions
        isRecordingAndNotPaused
            .flatMapLatest { active ->
                if (active) Timer.timeAndEmit() else flowOf()
            }
            .onEach { delta ->
                // Add the new delta to the current elapsed time
                _elapsedTime.update { it + delta }
            }
            .launchIn(applicationScope)
    }

    /** Start recording a new session (resets elapsed time if desired). */
    fun startRecording(outputFile: File, resetTime: Boolean = true) {
        if (_isRecording.value) return // Already recording
        if (resetTime) {
            _elapsedTime.value = Duration.ZERO
        }
        audioRecorder.start(outputFile = outputFile)
        _isRecording.value = true
        _isPaused.value = false
    }

    /** Pause the recorder if currently recording. (API >= 24 for official pause) */
    fun pauseRecording() {
        if (!_isRecording.value || _isPaused.value) return
        audioRecorder.pause()
        _isPaused.value = true
    }

    /** Resume from paused state. */
    fun resumeRecording() {
        if (!_isRecording.value || !_isPaused.value) return
        audioRecorder.resume()
        _isPaused.value = false
    }

    /** Stop recording entirely, optionally reset time to 0. */
    fun stopRecording(resetTime: Boolean = true) {
        if (!_isRecording.value) return
        audioRecorder.stop()
        _isRecording.value = false
        _isPaused.value = false
        if (resetTime) {
            _elapsedTime.value = Duration.ZERO
        }
    }

}
