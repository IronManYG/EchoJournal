@file:OptIn(ExperimentalLayoutApi::class)

package dev.gaddal.echojournal.journal.entry.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.clearText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.gaddal.echojournal.R
import dev.gaddal.echojournal.core.domain.logs.topic.Topic
import dev.gaddal.echojournal.core.presentation.designsystem.InterFontFamily
import dev.gaddal.echojournal.core.presentation.designsystem.components.EchoJournalTopic
import dev.gaddal.echojournal.core.presentation.ui.LocalesPreview
import dev.gaddal.echojournal.core.sample.SampleData.getLocalizedSampleTopics
import dev.gaddal.echojournal.journal.entry.EntryAction
import dev.gaddal.echojournal.journal.entry.EntryState

@Composable
fun TopicSearcherEntry(
    state: EntryState,
    onAction: (EntryAction) -> Unit,
    allTopics: List<Topic>,
    selectedTopics: List<Topic>,
    onTopicClick: (Topic) -> Unit,
    onCreateClick: () -> Unit,
    onClearTopicClick: (Topic) -> Unit,
) {
    var isFocused by remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }

    var dropdownOffset by remember { mutableStateOf(IntOffset.Zero) }

    Box {
        FlowRow(
            modifier = Modifier
                .fillMaxWidth()
                .onGloballyPositioned { coordinates ->
                    dropdownOffset = IntOffset(
                        coordinates.positionInParent().x.toInt(),
                        coordinates.positionInParent().y.toInt() + coordinates.size.height + 8 * 3
                    )
                },
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            selectedTopics.forEach { topic ->
                EchoJournalTopic(
                    topic = topic,
                    onClearTopicClick = onClearTopicClick
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = Icons.Default.Tag,
                    contentDescription = stringResource(R.string.tag),
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.outlineVariant
                )
                BasicTextField(
                    state = state.topicQuery,
                    textStyle = TextStyle(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontFamily = InterFontFamily,
                        fontWeight = FontWeight.Normal,
                        fontSize = 14.sp,
                        lineHeight = 20.sp
                    ),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    lineLimits = TextFieldLineLimits.SingleLine,
                    cursorBrush = SolidColor(MaterialTheme.colorScheme.onSurfaceVariant),
                    modifier = Modifier
                        .onFocusChanged {
                            isFocused = it.isFocused
                            expanded = it.isFocused
                            onAction(EntryAction.OnTopicFieldFocusChange(it.isFocused))
                        },
                    decorator = { innerBox ->
                        Row(
                            modifier = Modifier,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box {
                                if (state.topicQuery.text.isEmpty() && !isFocused) {
                                    Text(
                                        text = stringResource(R.string.topic_hint),
                                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(
                                            alpha = 0.4f
                                        ),
                                        style = MaterialTheme.typography.bodyMedium,
                                    )
                                }
                            }
                        }
                        innerBox()
                    }
                )
            }
        }

        CreatableTopicDropdown(
            expanded = expanded,
            searchQuery = state.topicQuery.text.toString(),
            allTopics = allTopics,
            selectedTopics = selectedTopics,
            onTopicClick = { topic ->
                onTopicClick(topic)
            },
            onCreateClick = {
                onCreateClick()
                state.topicQuery.clearText()
            },
            startOffset = dropdownOffset
        )
    }
}

@LocalesPreview
@Composable
fun TopicSearcherEntryPreview() {
    val sampleState = remember {
        mutableStateOf(EntryState())
    }

    val topics = getLocalizedSampleTopics()

    val sampleTopics = remember {
        mutableStateListOf<Topic>().apply {
            addAll(topics)
        }
    }

    val sampleSelectedTopics = remember {
        mutableStateListOf<Topic>()
    }

    TopicSearcherEntry(
        state = sampleState.value,
        onAction = {},
        allTopics = sampleTopics,
        selectedTopics = sampleSelectedTopics,
        onTopicClick = { topic ->
            if (sampleSelectedTopics.contains(topic)) {
                sampleSelectedTopics.remove(topic)
            } else {
                sampleSelectedTopics.add(topic)
            }
        },
        onCreateClick = {
            val topicId = sampleTopics.size + 1
            val topic = Topic(topicId, "New Topic #$topicId", "#000000")
            sampleTopics.add(topic)
        },
        onClearTopicClick = { topic ->
            sampleSelectedTopics.remove(topic)
        },
    )
}
