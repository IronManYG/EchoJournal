package dev.gaddal.echojournal.core.domain.record

import android.content.Context
import android.media.MediaRecorder
import android.os.Build
import java.io.File
import java.io.FileOutputStream

class AndroidAudioRecorder(
    private val context: Context,
) : AudioRecorder {

    private var recorder: MediaRecorder? = null

    override fun start(outputFile: File, desiredQuality: RecordingQuality) {
        val newRecorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            MediaRecorder(context)
        } else {
            MediaRecorder()
        }

        newRecorder.apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)

            // Try to configure safely
            configureRecorderSafely(this, desiredQuality)

            // Output file
            setOutputFile(FileOutputStream(outputFile).fd)

            prepare()
            start()
        }

        recorder = newRecorder
    }

    override fun stop() {
        recorder?.stop()
        recorder?.reset()
        recorder = null
    }

    override fun pause() {
        recorder?.pause()
    }

    override fun resume() {
        recorder?.resume()
    }

    private fun configureRecorderSafely(
        recorder: MediaRecorder,
        desiredQuality: RecordingQuality
    ) {
        // 1) Start with the user-chosen quality
        var chosenBitRate = desiredQuality.bitRate
        var chosenSampleRate = desiredQuality.sampleRate
        var chosenChannels = 2 // If you want stereo by default

        // 2) Apply desired quality first
        try {
            recorder.setAudioEncodingBitRate(chosenBitRate)
            recorder.setAudioSamplingRate(chosenSampleRate)
            recorder.setAudioChannels(chosenChannels)
        } catch (e: Exception) {
            // Could not apply the highest requested settings; let's fallback.

            // Log or print the error for debugging
            e.printStackTrace()

            // 3) Fallback logic: Try stepping down the channels first
            try {
                chosenChannels = 1
                recorder.setAudioEncodingBitRate(chosenBitRate) // Try again
                recorder.setAudioSamplingRate(chosenSampleRate)
                recorder.setAudioChannels(chosenChannels)
            } catch (e2: Exception) {
                e2.printStackTrace()
                // 4) If it still fails, fallback to a known "safe" lower quality
                chosenBitRate = RecordingQuality.LOW.bitRate
                chosenSampleRate = RecordingQuality.LOW.sampleRate
                chosenChannels = 1

                // Last attempt with minimal settings
                try {
                    recorder.setAudioEncodingBitRate(chosenBitRate)
                    recorder.setAudioSamplingRate(chosenSampleRate)
                    recorder.setAudioChannels(chosenChannels)
                } catch (e3: Exception) {
                    // If we get here, we have a very restricted device.
                    e3.printStackTrace()
                    // You might choose to throw an error or keep going with
                    // default system settings only.
                }
            }
        }
    }
}
