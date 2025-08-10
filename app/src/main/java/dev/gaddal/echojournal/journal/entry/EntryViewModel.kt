package dev.gaddal.echojournal.journal.entry

import android.media.MediaMetadataRetriever
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.gaddal.echojournal.core.domain.logs.audio_log.AudioLog
import dev.gaddal.echojournal.core.domain.logs.audio_log.AudioLogRepository
import dev.gaddal.echojournal.core.domain.logs.audio_log_topic.AudioLogTopic
import dev.gaddal.echojournal.core.domain.logs.audio_log_topic.AudioLogTopicRepository
import dev.gaddal.echojournal.core.domain.logs.topic.Topic
import dev.gaddal.echojournal.core.domain.logs.topic.TopicRepository
import dev.gaddal.echojournal.core.domain.mood.Mood
import dev.gaddal.echojournal.core.domain.playback.AudioPlaybackTracker
import dev.gaddal.echojournal.core.domain.util.DataError
import dev.gaddal.echojournal.core.domain.util.Result
import dev.gaddal.echojournal.core.presentation.ui.StorageLocation
import dev.gaddal.echojournal.core.presentation.ui.StoragePathProvider
import dev.gaddal.echojournal.core.presentation.ui.events.UiEventChannel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.File
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

class EntryViewModel(
    private val audioLogRepository: AudioLogRepository,
    private val topicRepository: TopicRepository,
    private val audioLogTopicRepository: AudioLogTopicRepository,
    private val audioPlaybackTracker: AudioPlaybackTracker,
    private val storagePathProvider: StoragePathProvider,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val _state = MutableStateFlow(EntryState())
    val state = _state.asStateFlow()

    private val uiEvents = UiEventChannel.buffered<EntryEvent>()
    val events = uiEvents.flow

    init {
        observePlaybackTracker()

        run {
            val temp = getTempAudioFile()
            val durationMs = if (temp.exists()) {
                getAudioDurationInMs(temp)
            } else {
                Timber.tag("EntryViewModel").w("Temp audio file not found at ${temp.absolutePath}")
                0L
            }
            _state.update {
                it.copy(
                    audioDuration = durationMs.milliseconds,
                )
            }
        }

        // Load topics from repository.
        // If the result is empty, default to sample data.
        topicRepository.getTopics()
            .onEach { topics ->
                _state.update {
                    it.copy(
                        allTopics = topics,
                        filteredTopics = topics
                    )
                }
            }
            .launchIn(viewModelScope)

        // Start collecting snapshotFlow for topicQuery.text
        // to filter allTopics
        viewModelScope.launch {
            snapshotFlow { _state.value.topicQuery.text }
                .collectLatest { query ->
                    val allTopics = _state.value.allTopics
                    val filtered = if (query.isEmpty()) {
                        // If empty, show all
                        allTopics
                    } else {
                        allTopics.filter { topic ->
                            topic.name.contains(query, ignoreCase = true)
                        }
                    }
                    _state.update { it.copy(filteredTopics = filtered) }
                }
        }
    }

    fun onAction(action: EntryAction) {
        when (action) {
            EntryAction.OnBackClick -> {} // No action needed
            EntryAction.OnSaveNewEntryClick -> handleSaveNewEntryClick()
            EntryAction.OnCancelNewEntryClick -> {} // No action needed
            is EntryAction.OnConfirmChooseMoodClick -> handleConfirmChooseMoodClick(action)
            EntryAction.OnCancelChooseMoodClick -> {} // No action needed
            is EntryAction.PlayAudio -> handlePlayAudio()
            EntryAction.PauseAudio -> handlePauseAudio()
            EntryAction.ResumeAudio -> handleResumeAudio()
            EntryAction.StopAudio -> handleStopAudio()
            is EntryAction.SeekTo -> handleSeekTo(action)
            is EntryAction.OnTopicFieldFocusChange -> handleTopicFieldFocusChange(action)
            is EntryAction.OnCreateTopic -> handleCreateTopic()
            is EntryAction.OnTopicSelected -> handleTopicSelected(action)
            is EntryAction.OnClearTopicClick -> handleClearTopicClick(action)
        }
    }

    private fun handleSaveNewEntryClick() {
        viewModelScope.launch(Dispatchers.IO) {
            var tempFile: File? = null
            var copiedFile: File? = null
            var audioLogIdResult: Result<Int, DataError>? =
                null // To track the audioLog upsert result

            try {
                tempFile = getTempAudioFile()
                copiedFile = copyAudioFileToInternal(tempFile)
                val filePath = copiedFile.absolutePath
                val durationMs = getAudioDurationInMs(copiedFile)

                val audioLog = AudioLog(
                    id = -1,
                    title = _state.value.title.text.toString(),
                    // Store canonical mood code (locale-independent) for robust localization
                    mood = when (_state.value.mood) {
                        Mood.Sad -> "sad"
                        Mood.Stressed -> "stressed"
                        Mood.Neutral -> "neutral"
                        Mood.Peaceful -> "peaceful"
                        Mood.Excited -> "excited"
                        null -> "" // Handle null case
                    },
                    audioFilePath = filePath,
                    createdAt = System.currentTimeMillis(),
                    updatedAt = System.currentTimeMillis(),
                    description = _state.value.description.text.toString(),
                    transcription = _state.value.transcription,
                    durationMs = durationMs,
                    archived = false
                )

                audioLogIdResult =
                    audioLogRepository.upsertAudioLog(audioLog) // Capture the result

                withContext(Dispatchers.Main) {
                    when (val result = audioLogIdResult) { // Use val result for smart cast
                        is Result.Success -> {
                            deleteFileByPath(tempFile.absolutePath)
                            Timber.tag("EntryViewModel").d("AudioLog ID: ${result.data}")

                            try {
                                _state.value.selectedTopics.forEach { topic ->
                                    val audioLogTopicResult =
                                        audioLogTopicRepository.upsertAudioLogTopic( // Capture result
                                            AudioLogTopic(
                                                audioLogId = result.data,
                                                topicId = topic.id
                                            )
                                        )
                                    if (audioLogTopicResult is Result.Error) { // Check for errors in topic upsert
                                        Timber.tag("EntryViewModel").e(
//                                                    audioLogTopicResult.exceptionOrNull(),
                                            "Error upserting AudioLogTopic for topic ${topic.name}"
                                        )
                                        // Decide how to handle topic upsert failure:
                                        // - Rollback transaction (if you have transactions)
                                        // - Flag error, but continue (potentially inconsistent data)
                                        // - Stop and report error
                                    }
                                }
                                uiEvents.emit(EntryEvent.SaveEntrySuccess)
                            } catch (topicUpsertException: Exception) { // More specific catch
                                Timber.tag("EntryViewModel").e(
                                    topicUpsertException,
                                    "Exception during AudioLogTopic upsert"
                                )
                                // Handle topic upsert exception -  consider rolling back AudioLog save?
//                                        withContext(Dispatchers.Main) {
//                                            eventChannel.send(EntryEvent.SaveEntryError("Error saving topics.")) // Send error event to UI
//                                        }
                            }
                        }

                        is Result.Error -> {
                            Timber.tag("EntryViewModel")
                                .e(
//                                            result.exceptionOrNull(),
                                    "Error saving AudioLog"
                                ) // Log error
                            deleteFileByPath(tempFile.absolutePath)
                            deleteFileByPath(copiedFile.absolutePath)
//                                    withContext(Dispatchers.Main) {
//                                        eventChannel.send(EntryEvent.SaveEntryError("Error saving audio log.")) // Send error event to UI
//                                    }
                        }

                        null -> { // Handle case where audioLogIdResult is unexpectedly null (though unlikely with your current code, good to be defensive)
                            Timber.tag("EntryViewModel")
                                .e("Unexpected null result from audioLogRepository.upsertAudioLog()")
                            deleteFileByPath(tempFile.absolutePath)
                            deleteFileByPath(copiedFile.absolutePath)
//                                    withContext(Dispatchers.Main) {
//                                        eventChannel.send(EntryEvent.SaveEntryError("Unexpected error saving audio log."))
//                                    }
                        }
                    }
                }
            } catch (generalException: Exception) { // More specific catch for general exceptions
                Timber.tag("EntryViewModel")
                    .e(
                        generalException,
                        "General exception during save process"
                    ) // Log general exception
                tempFile?.absolutePath?.let { deleteFileByPath(it) } // Ensure cleanup in finally block
                copiedFile?.absolutePath?.let { deleteFileByPath(it) } // Ensure cleanup in finally block
//                        withContext(Dispatchers.Main) {
//                            eventChannel.send(EntryEvent.SaveEntryError("General error during save.")) // Send error event to UI
//                        }
            } finally {
                // Ensure cleanup even if exceptions occur *before* repository call (less critical here, but good practice)
                // Consider if you REALLY need cleanup here - files should be deleted in specific error cases already.
                // If you remove deletion from catch/error blocks, then cleanup in finally becomes more crucial.
            }
        }
    }

    private fun handleConfirmChooseMoodClick(action: EntryAction.OnConfirmChooseMoodClick) {
        _state.update {
            it.copy(
                mood = action.chosenMood,
            )
        }
    }

    private fun handlePlayAudio() {
        val temp = getTempAudioFile()
        if (!temp.exists()) {
            Timber.tag("EntryViewModel").w("Attempted to play temp audio, but file missing at ${temp.absolutePath}")
            return
        }
        audioPlaybackTracker.playFile(temp)

        // Update global state so UI knows *which* entry is playing
        _state.update { oldState ->
            oldState.copy(
                isPlayingAudio = true,
                isPausedAudio = false,
                // Possibly reset position/duration if you want
                audioPosition = Duration.ZERO,
                audioDuration = Duration.ZERO
            )
        }
    }

    private fun handlePauseAudio() {
        audioPlaybackTracker.pause()
    }

    private fun handleResumeAudio() {
        audioPlaybackTracker.resume()
    }

    private fun handleStopAudio() {
        audioPlaybackTracker.stop()
        _state.update { oldState ->
            oldState.copy(
                isPlayingAudio = false,
                isPausedAudio = false,
                audioPosition = Duration.ZERO,
                audioDuration = Duration.ZERO
            )
        }
    }

    private fun handleSeekTo(action: EntryAction.SeekTo) {
        audioPlaybackTracker.seekTo(action.ms)
    }

    private fun handleTopicFieldFocusChange(action: EntryAction.OnTopicFieldFocusChange) {
        // Currently empty, you can add logic here if needed
    }

    private fun handleCreateTopic() {
        var isSuccess = false
        val newTopic = Topic(
            id = 0, // or -1 if your Topic ID is autogenerated
            name = _state.value.topicQuery.text.toString(),
            colorHex = null
        )
        viewModelScope.launch {
            when (topicRepository.upsertTopic(newTopic)) {
                is Result.Success -> {
                    isSuccess = true
                }

                is Result.Error -> {}
            }
            delay(200) // delay to make sure state is updated
            if (isSuccess) {
                _state.update {
                    val newTopicWithId = it.allTopics.find { topic -> topic.name == newTopic.name }
                    val topicExists =
                        it.selectedTopics.find { topic -> newTopicWithId == topic } == null
                    if (newTopicWithId != null && topicExists) {
                        Timber.tag("EntryViewModel").d("Topic with id added")
                        it.copy(
                            selectedTopics = it.selectedTopics + newTopicWithId
                        )
                    } else {
                        Timber.tag("EntryViewModel").d("Topic with id not found")
                        it
                    }
                }
            }
        }
    }

    private fun handleTopicSelected(action: EntryAction.OnTopicSelected) {
        _state.update {
            val updated = if (it.selectedTopics.contains(action.topic)) {
                it.selectedTopics - action.topic
            } else {
                it.selectedTopics + action.topic
            }
            it.copy(selectedTopics = updated)
        }
    }

    private fun handleClearTopicClick(action: EntryAction.OnClearTopicClick) {
        _state.update {
            it.copy(selectedTopics = it.selectedTopics - action.topic)
        }
    }

    private fun observePlaybackTracker() {
        audioPlaybackTracker.isPlaying
            .onEach { playing ->
                _state.update { it.copy(isPlayingAudio = playing) }
            }
            .launchIn(viewModelScope)

        audioPlaybackTracker.isPaused
            .onEach { paused ->
                _state.update { it.copy(isPausedAudio = paused) }
            }
            .launchIn(viewModelScope)

        audioPlaybackTracker.currentPosition
            .onEach { currentPos ->
                _state.update { it.copy(audioPosition = currentPos) }
            }
            .launchIn(viewModelScope)

        audioPlaybackTracker.duration
            .onEach { dur ->
                _state.update { it.copy(audioDuration = dur) }
            }
            .launchIn(viewModelScope)
    }

    /**
     * Copies [sourceFile] into the INTERNAL storage location with the specified [newFileName].
     * Returns the newly created File object in INTERNAL storage.
     */
    private fun copyAudioFileToInternal(
        sourceFile: File,
        newFileName: String = "my_recording_${System.currentTimeMillis()}.mp4"
    ): File {
        // Get the internal storage path
        val internalPath = storagePathProvider.getPath(StorageLocation.INTERNAL)

        // Create a new File reference with the desired name
        val destinationFile = File(internalPath, newFileName)

        // Copy contents from the source file to the destination
        sourceFile.copyTo(destinationFile, overwrite = true)

        // Return the destination file
        return destinationFile
    }

    private fun getTempAudioFile(): File {
        val path = storagePathProvider.getPath(StorageLocation.CACHE)
        val cacheDir = File(path)

        // Find the most recent MP4 file in cache (aligns with EntriesViewModel dynamic naming)
        val latest = cacheDir.listFiles()
            ?.asSequence()
            ?.filter { it.isFile && it.extension.equals("mp4", ignoreCase = true) }
            ?.maxByOrNull { it.lastModified() }

        if (latest != null) {
            Timber.tag("EntryViewModel").d("Resolved temp audio file: ${latest.absolutePath}")
            return latest
        }

        // Fallback to the old hard-coded name if present
        val fallback = File(path, "my_recording.mp4")
        Timber.tag("EntryViewModel").w("No dynamic temp audio found. Falling back to: ${fallback.absolutePath}")
        return fallback
    }

    private fun deleteFileByPath(filePath: String?): Boolean {
        // Return false immediately if filePath is null or blank
        if (filePath.isNullOrBlank()) return false

        val file = File(filePath)
        return if (file.exists()) {
            file.delete()
        } else {
            false
        }
    }

    private fun getAudioDurationInMs(audioFile: File): Long {
        if (!audioFile.exists()) {
            Timber.tag("EntryViewModel").w("getAudioDurationInMs: file does not exist at ${audioFile.absolutePath}")
            return 0L
        }
        val retriever = MediaMetadataRetriever()
        return try {
            retriever.setDataSource(audioFile.absolutePath)
            val durationString = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
            durationString?.toLongOrNull() ?: 0L
        } catch (e: IllegalArgumentException) {
            Timber.tag("EntryViewModel").e(e, "Failed to retrieve duration for ${audioFile.absolutePath}")
            0L
        } catch (e: RuntimeException) {
            Timber.tag("EntryViewModel").e(e, "Unexpected error retrieving duration for ${audioFile.absolutePath}")
            0L
        } finally {
            retriever.release()
        }
    }
}
