package dev.gaddal.echojournal.journal.entries

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.gaddal.echojournal.core.domain.logs.audio_log.AudioLogRepository
import dev.gaddal.echojournal.core.domain.logs.audio_log_topic.AudioLogTopicRepository
import dev.gaddal.echojournal.core.domain.logs.filter.FilterAudioLog
import dev.gaddal.echojournal.core.domain.logs.filter.toFilterParams
import dev.gaddal.echojournal.core.domain.logs.topic.TopicRepository
import dev.gaddal.echojournal.core.sample.SampleData.sampleAudioLogsWithTopics
import dev.gaddal.echojournal.core.sample.SampleData.sampleTopics
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class EntriesViewModel(
    private val audioLogRepository: AudioLogRepository,
    private val topicRepository: TopicRepository,
    private val audioLogTopicRepository: AudioLogTopicRepository,
    private val entriesFilter: FilterAudioLog,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val _state = MutableStateFlow(EntriesState())
    val state = _state.asStateFlow()

    private val eventChannel = Channel<EntriesEvent>()
    val events = eventChannel.receiveAsFlow()

    init {
        // Load audio logs (with topics) from repository.
        // If the result is empty, default to sample data.
        audioLogRepository.getAudioLogsWithTopics()
            .onEach { audioLogsWithTopics ->
                _state.update {
                    it.copy(entriesWithTopics = audioLogsWithTopics.ifEmpty { sampleAudioLogsWithTopics })
                }
                filterEntries() // Re-filter after loading
            }
            .launchIn(viewModelScope)

        // Load topics from repository.
        // If the result is empty, default to sample data.
        topicRepository.getTopics()
            .onEach { topics ->
                _state.update {
                    it.copy(allTopics = topics.ifEmpty { sampleTopics })
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

            is EntriesAction.OnEntryClick -> {}
            EntriesAction.OnFabClick -> {}
        }
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
