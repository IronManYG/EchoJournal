package dev.gaddal.echojournal.journal.entry.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import dev.gaddal.echojournal.core.domain.logs.topic.Topic
import dev.gaddal.echojournal.core.presentation.designsystem.EchoJournalTheme
import dev.gaddal.echojournal.core.presentation.ui.LocalesPreview
import dev.gaddal.echojournal.core.sample.SampleData.getLocalizedSampleTopics
import dev.gaddal.echojournal.journal.entries.components.TopicDropdownMenuItem

/**
 * A Custom DropdownMenu for displaying and selecting topics, built using Material 3 DropdownMenu as a container.
 * Designed for scenarios requiring precise positioning control, especially within complex layouts like FlowRow.
 * Also suitable for dropdowns triggered by text field input or other dynamic conditions.
 */
@Composable
fun CreatableTopicDropdown(
    expanded: Boolean,
    allTopics: List<Topic>,
    selectedTopics: List<Topic>,
    onTopicClick: (Topic) -> Unit,
    searchQuery: String,
    onCreateClick: () -> Unit,
    modifier: Modifier = Modifier,
    startOffset: IntOffset = IntOffset.Zero
) {
    val focusManager = LocalFocusManager.current

    if (expanded) {
        Box(
            modifier = modifier
                .offset { startOffset }
                .fillMaxSize()
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { focusManager.clearFocus() }
        ) {
            Surface(
                modifier = Modifier.heightIn(max = 200.dp),
                shape = RoundedCornerShape(10.dp),
                shadowElevation = 8.dp
            ) {
                LazyColumn(
                    modifier = Modifier.padding(4.dp)
                ) {
                    items(items = allTopics) { topic ->
                        val isSelected = topic in selectedTopics
                        TopicDropdownMenuItem(
                            topic = topic,
                            isSelected = isSelected,
                            onTopicSelected = {
                                onTopicClick(topic)
                                focusManager.clearFocus()
                            },
                        )
                    }
                    item {
                        CreatableTopicItem(
                            searchQuery = searchQuery,
                            onCreateClick = {
                                onCreateClick()
                                focusManager.clearFocus()
                            },
                            enabled = when {
                                searchQuery.isEmpty() -> false
                                allTopics.any {
                                    it.name.equals(
                                        searchQuery,
                                        ignoreCase = true
                                    )
                                } -> false

                                else -> true
                            }
                        )
                    }
                }
            }
        }
    }
}

@LocalesPreview
@Composable
fun CreatableTopicDropdownPreview() {
    val sampleTopics = getLocalizedSampleTopics()

    val selectedTopics = listOf(sampleTopics[0])

    EchoJournalTheme {
        Box(modifier = Modifier.padding(40.dp)) {
            CreatableTopicDropdown(
                expanded = true,
                allTopics = sampleTopics,
                selectedTopics = selectedTopics,
                onTopicClick = { },
                searchQuery = sampleTopics.random().name,
                onCreateClick = { },
                modifier = Modifier
            )
        }
    }
}
