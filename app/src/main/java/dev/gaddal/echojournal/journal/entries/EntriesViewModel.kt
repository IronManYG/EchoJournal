package dev.gaddal.echojournal.journal.entries

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.gaddal.echojournal.core.domain.logs.audio_log.AudioLogRepository
import dev.gaddal.echojournal.core.domain.logs.filter.FilterAudioLog
import dev.gaddal.echojournal.core.domain.logs.filter.toFilterParams
import dev.gaddal.echojournal.core.domain.logs.topic.TopicRepository
import dev.gaddal.echojournal.core.domain.playback.AudioPlaybackTracker
import dev.gaddal.echojournal.core.domain.record.AudioRecordingTracker
import dev.gaddal.echojournal.core.presentation.ui.StorageLocation
import dev.gaddal.echojournal.core.presentation.ui.StoragePathProvider
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import kotlin.time.Duration

class EntriesViewModel(
    private val audioLogRepository: AudioLogRepository,
    private val topicRepository: TopicRepository,
    private val entriesFilter: FilterAudioLog,
    private val audioRecordingTracker: AudioRecordingTracker,
    private val audioPlaybackTracker: AudioPlaybackTracker,
    private val storagePathProvider: StoragePathProvider,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val _state = MutableStateFlow(EntriesState())
    val state = _state.asStateFlow()

    private val eventChannel = Channel<EntriesEvent>()
    val events = eventChannel.receiveAsFlow()

    init {
        // Mirror the tracker's flows into the state
        observeRecordingTracker()
        observePlaybackTracker()

        // Load audio logs (with topics) from repository.
        audioLogRepository.getAudioLogsWithTopics()
            .onEach { audioLogsWithTopics ->
                _state.update {
                    it.copy(entriesWithTopics = audioLogsWithTopics)
                }
                filterEntries() // Re-filter after loading
            }
            .launchIn(viewModelScope)

        // Load topics from repository.
        topicRepository.getTopics()
            .onEach { topics ->
                _state.update {
                    it.copy(allTopics = topics)
                }
                filterEntries() // Re-filter after loading
            }
            .launchIn(viewModelScope)
    }

    fun onAction(action: EntriesAction) {
        when (action) {
            is EntriesAction.OnCreateTopicClick -> {
                viewModelScope.launch {
                    topicRepository.upsertTopic(action.topic)
                }
                _state.update {
                    it.copy(
                        // Add it to the master list
                        // In a real app, you'd store it in a DB or something
                        // Then also add it to selected so that user sees it
                        // For demonstration only:
                        // allTopics = allTopics + newTopic // If you keep it in a var
                        selectedTopics = it.selectedTopics.toMutableList()
                            .apply { add(action.topic) },
                        allTopics = it.allTopics.toMutableList()
                            .apply { add(action.topic) },
                    )
                }
                filterEntries()
            }

            is EntriesAction.OnMoodSelected -> {
                _state.update {
                    // Add or remove the mood from the list of selected moods
                    val updated = if (it.selectedMoods.contains(action.mood)) {
                        it.selectedMoods - action.mood
                    } else {
                        it.selectedMoods + action.mood
                    }
                    it.copy(selectedMoods = updated)
                }
                filterEntries()
            }

            is EntriesAction.OnTopicSelected -> {
                _state.update {
                    val updated = if (it.selectedTopics.contains(action.topic)) {
                        it.selectedTopics - action.topic
                    } else {
                        it.selectedTopics + action.topic
                    }
                    it.copy(selectedTopics = updated)
                }
                filterEntries()
            }

            EntriesAction.OnClearMoodFilter -> {
                _state.update {
                    it.copy(selectedMoods = emptyList())
                }
                filterEntries()
            }

            EntriesAction.OnClearTopicFilter -> {
                _state.update {
                    it.copy(selectedTopics = emptyList())
                }
                filterEntries()
            }

            is EntriesAction.OnQueryChanged -> {
                _state.update { it.copy(query = action.query) }
                filterEntries()
            }

            is EntriesAction.OnDateRangeChanged -> {
                val from = action.fromDateMillis
                val to = action.toDateMillis
                val normFrom = if (from != null && to != null && from > to) to else from
                val normTo = if (from != null && to != null && from > to) from else to
                _state.update { it.copy(fromDateMillis = normFrom, toDateMillis = normTo) }
                filterEntries()
            }

            is EntriesAction.OnSortOrderChanged -> {
                _state.update { it.copy(sortOrder = action.sortOrder) }
                filterEntries()
            }

            EntriesAction.OnSettingsClick -> {}

            EntriesAction.OnCreateNewEntryTrigger -> {}

            is EntriesAction.OnEntryClick -> {}

            // 1) Start
            EntriesAction.OnStartRecordingClick -> {
                if (!_state.value.hasRecordPermission) return
                // For example: create a file in the cache directory
                val file = createTempAudioFile()
                audioRecordingTracker.startRecording(file, resetTime = true)

                // We don't need to set isRecording, etc.
                // Because observeTracker() will mirror changes automatically.
            }

            // 2) Pause
            EntriesAction.OnPauseRecordingClick -> {
                if (!_state.value.isRecording) return
                audioRecordingTracker.pauseRecording()
            }

            // 3) Resume
            EntriesAction.OnResumeRecordingClick -> {
                if (!_state.value.isPaused) return
                audioRecordingTracker.resumeRecording()
            }

            // 4) Finish
            EntriesAction.OnFinishRecordingClick -> {
                audioRecordingTracker.stopRecording(resetTime = true)
                // Possibly do something with the final file
                // e.g. do saving logic, etc.
                // Possibly emit an event or save the final audio log
                // Emit an event that the UI can observe
                // e.g., to navigate, show a toast, etc.
                // viewModelScope.launch { _events.emit(RecordEvent.RecordingSaved) }
            }

            EntriesAction.OnCancelRecordingClick -> {
                audioRecordingTracker.stopRecording(resetTime = true)
                // Discard or remove the file, etc.
                // e.g. do deleting logic, etc.
            }

            // Handle microphone permission results
            is EntriesAction.SubmitAudioPermissionInfo -> {
                _state.update {
                    it.copy(
                        hasRecordPermission = action.acceptedAudioPermission,
                        showRecordRationale = action.showAudioRationale
                    )
                }
            }

            EntriesAction.OnDismissRationaleDialog -> {
                _state.update { it.copy(showRecordRationale = false) }
            }

            is EntriesAction.PlayAudio -> {
                val entryFilePath =
                    state.value.entriesWithTopics.find { it.audioLog.id == action.logId }?.audioLog?.audioFilePath

                if (entryFilePath == null) {
                    // Let’s assume you look up the correct file from the log, etc.
                    val path = storagePathProvider.getPath(StorageLocation.CACHE)
                    val fileName = "my_recording.mp4"
                    val file = File(path, fileName) // only for demonstration and testing
                    audioPlaybackTracker.playFile(file)
                } else {
                    audioPlaybackTracker.playFile(File(entryFilePath))
                }

                // Update global state so UI knows *which* entry is playing
                _state.update { oldState ->
                    oldState.copy(
                        nowPlayingLogId = action.logId,
                        isPlayingAudio = true,
                        isPausedAudio = false,
                        // Possibly reset position/duration if you want
                        audioPosition = Duration.ZERO,
                        audioDuration = Duration.ZERO
                    )
                }
            }

            EntriesAction.PauseAudio -> {
                audioPlaybackTracker.pause()
            }

            EntriesAction.ResumeAudio -> {
                audioPlaybackTracker.resume()
            }

            EntriesAction.StopAudio -> {
                audioPlaybackTracker.stop()
                _state.update { oldState ->
                    oldState.copy(
                        nowPlayingLogId = null,
                        isPlayingAudio = false,
                        isPausedAudio = false,
                        audioPosition = Duration.ZERO,
                        audioDuration = Duration.ZERO
                    )
                }
            }

            is EntriesAction.SeekTo -> {
                audioPlaybackTracker.seekTo(action.ms)
            }
        }
    }

    private fun observeRecordingTracker() {
        audioRecordingTracker.isRecording
            .onEach { isRec ->
                _state.update { it.copy(isRecording = isRec) }
            }
            .launchIn(viewModelScope)

        audioRecordingTracker.isPaused
            .onEach { paused ->
                _state.update { it.copy(isPaused = paused) }
            }
            .launchIn(viewModelScope)

        audioRecordingTracker.elapsedTime
            .onEach { dur ->
                _state.update { it.copy(elapsedTime = dur) }
            }
            .launchIn(viewModelScope)
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


    private fun createTempAudioFile(
        storageLocation: StorageLocation = StorageLocation.CACHE,
    ): File {
        val path = storagePathProvider.getPath(storageLocation)
        val fileName = "my_recording.mp4"
        return File(path, fileName)
    }

    /**
     * Applies the filter logic to the current [EntriesState.entriesWithTopics]
     * and updates the [EntriesState.filterEntriesWithTopics].
     */
    private fun filterEntries() {
        val filtered = entriesFilter.execute(_state.value.toFilterParams())
        _state.update {
            it.copy(filterEntriesWithTopics = filtered)
        }
    }
}
