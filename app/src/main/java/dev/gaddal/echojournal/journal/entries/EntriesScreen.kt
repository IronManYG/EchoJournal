@file:OptIn(ExperimentalMaterial3Api::class)

package dev.gaddal.echojournal.journal.entries

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.gaddal.echojournal.R
import dev.gaddal.echojournal.core.presentation.designsystem.EchoJournalTheme
import dev.gaddal.echojournal.core.presentation.designsystem.components.EchoJournalFAB
import dev.gaddal.echojournal.core.presentation.designsystem.components.EchoJournalScaffold
import dev.gaddal.echojournal.core.presentation.designsystem.components.EchoJournalTopAppBar
import dev.gaddal.echojournal.core.presentation.ui.LocalesPreview
import dev.gaddal.echojournal.core.presentation.ui.ObserveAsEvents
import dev.gaddal.echojournal.journal.entries.components.EmptyList
import dev.gaddal.echojournal.journal.entries.components.EntryList
import dev.gaddal.echojournal.journal.entries.components.MoodsFilterSection
import dev.gaddal.echojournal.journal.entries.components.TopicsFilterSection
import dev.gaddal.echojournal.record.components.RecordBottomSheet
import org.koin.androidx.compose.koinViewModel

@Composable
fun EntriesScreenRoot(
    onSettingsClick: () -> Unit,
    onCreateNewEntryTrigger: () -> Unit,
    onEntryClick: (id: Int) -> Unit,
    viewModel: EntriesViewModel = koinViewModel(),
) {
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current
    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            is EntriesEvent.Error -> {
                keyboardController?.hide()
                Toast.makeText(
                    context,
                    event.error.asString(context),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    val state by viewModel.state.collectAsStateWithLifecycle()

    EntriesScreen(
        state = state,
        onAction = { action ->
            when (action) {
                is EntriesAction.OnSettingsClick -> onSettingsClick()
                is EntriesAction.OnCreateNewEntryTrigger -> onCreateNewEntryTrigger()
                is EntriesAction.OnEntryClick -> onEntryClick(action.id)
                else -> Unit
            }
            viewModel.onAction(action)
        }
    )
}

@Composable
fun EntriesScreen(
    state: EntriesState,
    onAction: (EntriesAction) -> Unit,
) {
    var showBottomSheet by remember { mutableStateOf(false) }
    EchoJournalScaffold(
        modifier = Modifier,
        topAppBar = {
            EchoJournalTopAppBar(
                title = stringResource(id = R.string.your_echo_journal),
                modifier = Modifier.fillMaxWidth(),
                showBackButton = false,
                showSettingsButton = true,
                onSettingsClick = { onAction(EntriesAction.OnSettingsClick) }
            )
        },
        floatingActionButton = {
            EchoJournalFAB(
                modifier = Modifier
                    .padding(16.dp),
                icon = Icons.Default.Add,
                contentDescription = "Add new entry",
                rippleEnabled = false,
                isLargeVariant = false,
                onClick = {
                    showBottomSheet = true
                }
            )
        },
    ) { innerPadding ->
        if (state.entriesWithTopics.isEmpty()) {
            EmptyList(modifier = Modifier.padding(innerPadding))
        } else {
            Column(
                modifier = Modifier
                    .padding(innerPadding)
            ) {
                // Filter Chips
                LazyRow(
                    modifier = Modifier.padding(horizontal = 16.dp),
                ) {
                    item {
                        MoodsFilterSection(
                            allMoods = state.allMoods,
                            selectedMoods = state.selectedMoods,
                            onMoodSelected = { mood -> onAction(EntriesAction.OnMoodSelected(mood)) },
                            onClearFilter = { onAction(EntriesAction.OnClearMoodFilter) },
                        )
                    }
                    item {
                        TopicsFilterSection(
                            allTopics = state.allTopics,
                            selectedTopics = state.selectedTopics,
                            onClearFilter = { onAction(EntriesAction.OnClearTopicFilter) },
                            onTopicSelected = { topic ->
                                onAction(
                                    EntriesAction.OnTopicSelected(
                                        topic
                                    )
                                )
                            }
                        )
                    }
                }

                EntryList(
                    entries = state.filterEntriesWithTopics,
                    nowPlayingLogId = state.nowPlayingLogId,
                    isPlaying = state.isPlayingAudio,
                    isPaused = state.isPausedAudio,
                    currentPosition = state.audioPosition,
                    duration = state.audioDuration,
                    onPlay = { id -> onAction(EntriesAction.PlayAudio(filePath = "", logId = id)) },
                    onPause = { onAction(EntriesAction.PauseAudio) },
                    onResume = { onAction(EntriesAction.ResumeAudio) },
                    onStop = { onAction(EntriesAction.StopAudio) },
                    onSeek = { ms -> onAction(EntriesAction.SeekTo(ms)) }
                )
            }
        }
        RecordBottomSheet(
            showBottomSheet = showBottomSheet,
            onDismiss = { showBottomSheet = false },
            onAction = onAction, // the same callback your VM uses
            state = state // pass state from VM
        )
    }
}

@LocalesPreview
@Composable
fun EntriesScreenPreview() {
    EchoJournalTheme {
        EntriesScreen(
            state = EntriesState(),
            onAction = {}
        )
    }
}
