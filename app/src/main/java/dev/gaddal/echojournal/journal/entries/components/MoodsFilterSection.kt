package dev.gaddal.echojournal.journal.entries.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import dev.gaddal.echojournal.core.domain.mood.Mood
import dev.gaddal.echojournal.core.presentation.designsystem.components.EchoJournalChip

@Composable
fun MoodsFilterSection(
    allMoods: List<Mood>,
    selectedMoods: List<Mood>,
    onMoodSelected: (Mood) -> Unit,
    onClearFilter: () -> Unit
) {
    var isMenuOpen by remember { mutableStateOf(false) }

    // The chip shows the current selected moods
    EchoJournalChip(
        moods = selectedMoods,
        isDropdownOpen = isMenuOpen,
        selected = isMenuOpen,
        onChipClick = { isMenuOpen = !isMenuOpen },
        onClearFilter = onClearFilter
    )

    // The dropdown is displayed separately below or in a Box over the chip:
    MoodsDropdownMenu(
        expanded = isMenuOpen,
        onDismissRequest = { isMenuOpen = false },
        allMoods = allMoods,
        selectedMoods = selectedMoods,
        onMoodSelected = { onMoodSelected(it) }
    )
}
