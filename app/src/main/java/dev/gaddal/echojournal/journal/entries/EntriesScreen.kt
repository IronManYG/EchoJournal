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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.gaddal.echojournal.core.presentation.designsystem.EchoJournalTheme
import dev.gaddal.echojournal.core.presentation.designsystem.components.EchoJournalFAB
import dev.gaddal.echojournal.core.presentation.designsystem.components.EchoJournalScaffold
import dev.gaddal.echojournal.core.presentation.designsystem.components.EchoJournalTopAppBar
import dev.gaddal.echojournal.core.presentation.ui.ObserveAsEvents
import dev.gaddal.echojournal.journal.entries.components.EmptyList
import dev.gaddal.echojournal.journal.entries.components.EntryList
import dev.gaddal.echojournal.journal.entries.components.MoodsFilterSection
import dev.gaddal.echojournal.journal.entries.components.TopicsFilterSection
import org.koin.androidx.compose.koinViewModel

@Composable
fun EntriesScreenRoot(
    onFabClick: () -> Unit,
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
                is EntriesAction.OnFabClick -> onFabClick()
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
    EchoJournalScaffold(
        modifier = Modifier,
        topAppBar = {
            EchoJournalTopAppBar(
                title = "Your EchoJournal",
                modifier = Modifier.fillMaxWidth(),
                showBackButton = false,
                showSettingsButton = true,
            )
        },
        floatingActionButton = {
            EchoJournalFAB(
                modifier = Modifier
                    .padding(16.dp),
                icon = Icons.Default.Add,
                rippleEnabled = false,
                isLargeVariant = false,
                onClick = {}
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
                            },
                            onCreateTopic = { newTopic ->
                                onAction(EntriesAction.OnCreateTopicClick(newTopic))
                            }
                        )
                    }
                }

                EntryList(
                    entries = state.filterEntriesWithTopics
                )
            }
        }
    }
}

@Preview
@Composable
fun EntriesScreenPreview() {
    EchoJournalTheme {
        EntriesScreen(
            state = EntriesState(),
            onAction = {}
        )
    }
}