package dev.gaddal.echojournal.journal.entries.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import dev.gaddal.echojournal.core.domain.logs.topic.Topic
import dev.gaddal.echojournal.core.presentation.designsystem.components.EchoJournalChip

@Composable
fun TopicsFilterSection(
    allTopics: List<Topic>,
    selectedTopics: List<Topic>,
    onClearFilter: () -> Unit,
    onTopicSelected: (Topic) -> Unit,
) {
    var isMenuOpen by remember { mutableStateOf(false) }

    EchoJournalChip(
        topics = selectedTopics.sortedBy { it.name },
        isDropdownOpen = isMenuOpen,
        selected = isMenuOpen,
        onChipClick = { isMenuOpen = !isMenuOpen },
        onClearFilter = onClearFilter

    )

    TopicsDropdownMenu(
        expanded = isMenuOpen,
        onDismissRequest = { isMenuOpen = false },
        allTopics = allTopics,
        selectedTopics = selectedTopics,
        onTopicClick = { onTopicSelected(it) },
    )
}
