@file:OptIn(ExperimentalMaterial3Api::class)

package dev.gaddal.echojournal.journal.entry.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.gaddal.echojournal.R
import dev.gaddal.echojournal.core.domain.mood.Mood
import dev.gaddal.echojournal.core.presentation.designsystem.EchoJournalTheme
import dev.gaddal.echojournal.core.presentation.designsystem.colors.ButtonGradient
import dev.gaddal.echojournal.core.presentation.designsystem.colors.ButtonPressedGradient
import dev.gaddal.echojournal.core.presentation.designsystem.components.GradientButton
import dev.gaddal.echojournal.core.presentation.ui.LocalesPreview
import dev.gaddal.echojournal.journal.entry.EntryAction
import dev.gaddal.echojournal.journal.entry.EntryState

@Composable
fun MoodBottomSheet(
    showBottomSheet: Boolean,
    onDismiss: () -> Unit,
    onAction: (EntryAction) -> Unit,
    state: EntryState, // from ViewModel
    modifier: Modifier = Modifier
) {
    val sheetState = rememberModalBottomSheetState()

    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = {
                // if user dismiss, we treat it like "Cancel"
                onAction(EntryAction.OnCancelChooseMoodClick)
                onDismiss()
            },
            modifier = modifier,
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.surface,
        ) {
            // Header
            Text(
                text = stringResource(id = R.string.mood_question),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.headlineMedium,
            )

            // Moods
            var chosenMood by remember { mutableStateOf(state.mood) }
            Row(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 32.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                for (mood in state.allMoods) {
                    Column(
                        modifier = Modifier.clickable { chosenMood = mood },
                        verticalArrangement = Arrangement.spacedBy(
                            8.dp,
                            Alignment.CenterVertically
                        ),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            imageVector = when {
                                chosenMood == mood -> ImageVector.vectorResource(id = mood.iconRes)
                                else -> ImageVector.vectorResource(id = mood.iconResActiveOff)
                            },
                            contentDescription = mood.title.asString(),
                            modifier = Modifier.size(40.dp)
                        )
                        Text(
                            text = mood.title.asString(),
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodySmall,
                        )
                    }
                }
            }

            // Cancel & Confirm buttons
            Row(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(
                    onClick = {
                        onAction(EntryAction.OnCancelChooseMoodClick)
                        onDismiss()
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.inverseOnSurface,
                    )
                ) {
                    Text(
                        text = stringResource(id = R.string.cancel),
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.labelLarge,
                    )
                }

                GradientButton(
                    onClick = {
                        onAction(EntryAction.OnConfirmChooseMoodClick(chosenMood!!))
                        onDismiss()
                    },
                    modifier = Modifier.weight(2f),
                    enabled = chosenMood != null,
                    enabledBrush = Brush.linearGradient(
                        colors = ButtonGradient,
                    ),
                    pressedBrush = Brush.linearGradient(
                        colors = ButtonPressedGradient,
                    ),
                    disabledBrush = Brush.linearGradient(
                        colors = listOf(Color(0xFFE1E2EC), Color(0xFFE1E2EC))
                    ),
                    enabledContentColor = MaterialTheme.colorScheme.onPrimary,
                    disabledContentColor = MaterialTheme.colorScheme.outline
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                    )
                    Spacer(
                        modifier = Modifier.width(6.dp)
                    )
                    Text(
                        text = stringResource(id = R.string.confirm),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.labelLarge,
                    )
                }
            }
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@LocalesPreview
@Composable
fun MoodBottomSheetPreview() {
    EchoJournalTheme {
        MoodBottomSheet(
            showBottomSheet = true,
            onDismiss = { },
            onAction = { },
            state = EntryState(
                mood = Mood.Neutral,
            )
        )
    }
}
