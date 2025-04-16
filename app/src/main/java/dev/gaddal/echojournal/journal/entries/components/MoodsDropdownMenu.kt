package dev.gaddal.echojournal.journal.entries.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.gaddal.echojournal.R
import dev.gaddal.echojournal.core.domain.mood.Mood

@Composable
fun MoodsDropdownMenu(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    allMoods: List<Mood>,
    selectedMoods: List<Mood>,
    onMoodSelected: (Mood) -> Unit
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismissRequest,
        modifier = Modifier.fillMaxWidth(0.9f),
        containerColor = MaterialTheme.colorScheme.surface,
    ) {
        allMoods.forEach { mood ->
            val isSelected = mood in selectedMoods
            DropdownMenuItem(
                modifier = Modifier
                    .padding(horizontal = 4.dp, vertical = 2.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        color = if (isSelected) MaterialTheme.colorScheme.surfaceTint.copy(alpha = 0.05f)
                        else Color.Unspecified
                    ),
                leadingIcon = {
                    Image(
                        painter = painterResource(id = mood.iconRes),
                        contentDescription = mood.title.asString(),
                        modifier = Modifier.size(24.dp)
                    )
                },
                text = {
                    Text(
                        text = mood.title.asString(),
                        color = MaterialTheme.colorScheme.secondary,
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.labelMedium
                    )
                },
                // DO NOT dismiss upon selection (multi-select)
                onClick = {
                    onMoodSelected(mood)
                },
                trailingIcon = {
                    if (isSelected) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = stringResource(R.string.selected),
                            modifier = Modifier.size(20.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            )
        }
    }
}