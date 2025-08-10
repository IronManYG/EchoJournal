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
import dev.gaddal.echojournal.core.domain.record.FileNameProvider
import dev.gaddal.echojournal.core.domain.util.Result
import dev.gaddal.echojournal.core.presentation.ui.StorageLocation
import dev.gaddal.echojournal.core.presentation.ui.StoragePathProvider
import dev.gaddal.echojournal.core.presentation.ui.asUiText
import dev.gaddal.echojournal.core.presentation.ui.events.UiEventChannel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File
import kotlin.time.Duration

class EntriesViewModel(
    private val audioLogRepository: AudioLogRepository,
    private val topicRepository: TopicRepository,
    private val entriesFilter: FilterAudioLog,
    private val audioRecordingTracker: AudioRecordingTracker,
    private val audioPlaybackTracker: AudioPlaybackTracker,
    private val fileNameProvider: FileNameProvider,
    private val storagePathProvider: StoragePathProvider,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val _state = MutableStateFlow(EntriesState())
    val state = _state.asStateFlow()

    private val uiEvents = UiEventChannel.buffered<EntriesEvent>()
    val events = uiEvents.flow

    init {
        // Mirror the tracker's flows into the state
        observeRecordingTracker()
        observePlaybackTracker()

        // Load audio logs (with topics) from repository.
        audioLogRepository.getAudioLogsWithTopics()
            .onEach { audioLogsWithTopics ->
                _state.update {
                    it.copy(
                        entriesWithTopics = audioLogsWithTopics,
                        isLoading = false,
                        error = null
                    )
                }
                filterEntries() // Re-filter after loading
            }
            .launchIn(viewModelScope)

        // Load topics from repository.
        topicRepository.getTopics()
            .onEach { topics ->
                _state.update {
                    it.copy(
                        allTopics = topics,
                        isLoading = false,
                        error = null
                    )
                }
                filterEntries() // Re-filter after loading
            }
            .launchIn(viewModelScope)
    }

    fun onAction(action: EntriesAction) {
        when (action) {
            is EntriesAction.OnCreateTopicClick -> {
                viewModelScope.launch {
                    when (val result = topicRepository.upsertTopic(action.topic)) {
                        is Result.Success -> {
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

                        is Result.Error -> {
                            uiEvents.emit(
                                EntriesEvent.Error(result.error.asUiText())
                            )
                        }
                    }
                }
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

                // Mutual exclusivity: stop playback if currently playing
                if (_state.value.isPlayingAudio) {
                    Timber.tag("EntriesViewModel")
                        .d("Auto-stopping playback before starting recording")
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

                // Create a file in the cache directory using FileNameProvider
                val file = createTempAudioFile()
                audioRecordingTracker.startRecording(file, resetTime = true)
                Timber.tag("EntriesViewModel").d("Started recording to $file")
            }

            // 2) Pause
            EntriesAction.OnPauseRecordingClick -> {
                if (!_state.value.isRecording) return
                Timber.tag("EntriesViewModel").d("Pausing recording")
                audioRecordingTracker.pauseRecording()
            }

            // 3) Resume
            EntriesAction.OnResumeRecordingClick -> {
                if (!_state.value.isPaused) return
                Timber.tag("EntriesViewModel").d("Resuming recording")
                audioRecordingTracker.resumeRecording()
            }

            // 4) Finish
            EntriesAction.OnFinishRecordingClick -> {
                Timber.tag("EntriesViewModel").d("Finishing recording")
                audioRecordingTracker.stopRecording(resetTime = true)
                // Possibly do something with the final file
                // e.g. do saving logic, etc.
                // Possibly emit an event or save the final audio log
                // Emit an event that the UI can observe
                // e.g., to navigate, show a toast, etc.
                // viewModelScope.launch { _events.emit(RecordEvent.RecordingSaved) }
            }

            EntriesAction.OnCancelRecordingClick -> {
                Timber.tag("EntriesViewModel").d("Cancelling recording")
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
                // Mutual exclusivity: stop recording if currently recording or paused
                if (_state.value.isRecording || _state.value.isPaused) {
                    Timber.tag("EntriesViewModel")
                        .d("Auto-stopping recording before starting playback")
                    audioRecordingTracker.stopRecording(resetTime = true)
                }

                val entryFilePath =
                    state.value.entriesWithTopics.find { it.audioLog.id == action.logId }?.audioLog?.audioFilePath

                if (entryFilePath == null) {
                    // Fallback demo file path using FileNameProvider (no hard-coded name)
                    val path = storagePathProvider.getPath(StorageLocation.CACHE)
                    val fileName = fileNameProvider.newAudioFileName()
                    val file = File(path, fileName)
                    Timber.tag("EntriesViewModel").d("Playing fallback file: $file")
                    audioPlaybackTracker.playFile(file)
                } else {
                    Timber.tag("EntriesViewModel").d("Playing file: $entryFilePath")
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
                Timber.tag("EntriesViewModel").d("Pausing playback")
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
        val fileName = fileNameProvider.newAudioFileName()
        Timber.tag("EntriesViewModel").d("Generated audio file name: $fileName at $path")
        return File(path, fileName)
    }

    /**
     * Applies the filter logic to the current [EntriesState.entriesWithTopics]
     * and updates the [EntriesState.filterEntriesWithTopics].
     */
    private fun filterEntries() {
        val params = _state.value.toFilterParams()
        Timber.tag("EntriesViewModel").d(
            "Filtering: query='${'$'}{params.query}', moods=${'$'}{params.selectedMoods.size}, topics=${'$'}{params.selectedTopicIds.size}, date=[${'$'}{params.fromDateMillis}..${'$'}{params.toDateMillis}], sort=${'$'}{params.sortOrder}, current=${'$'}{params.currentLogs.size}"
        )
        val filtered = entriesFilter.execute(params)
        _state.update {
            it.copy(filterEntriesWithTopics = filtered)
        }
        Timber.tag("EntriesViewModel").d("Filtering complete: result=${'$'}{filtered.size}")
    }
}
