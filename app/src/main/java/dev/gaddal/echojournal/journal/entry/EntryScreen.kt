@file:OptIn(ExperimentalMaterial3Api::class)

package dev.gaddal.echojournal.journal.entry

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.gaddal.echojournal.core.domain.mood.Mood
import dev.gaddal.echojournal.core.presentation.designsystem.AiIcon
import dev.gaddal.echojournal.core.presentation.designsystem.EchoJournalTheme
import dev.gaddal.echojournal.core.presentation.designsystem.InterFontFamily
import dev.gaddal.echojournal.core.presentation.designsystem.colors.ButtonGradient
import dev.gaddal.echojournal.core.presentation.designsystem.colors.ButtonPressedGradient
import dev.gaddal.echojournal.core.presentation.designsystem.colors.Primary50
import dev.gaddal.echojournal.core.presentation.designsystem.colors.Secondary70
import dev.gaddal.echojournal.core.presentation.designsystem.colors.Secondary95
import dev.gaddal.echojournal.core.presentation.designsystem.components.EchoJournalScaffold
import dev.gaddal.echojournal.core.presentation.designsystem.components.EchoJournalTopAppBar
import dev.gaddal.echojournal.core.presentation.designsystem.components.GradientButton
import dev.gaddal.echojournal.core.presentation.designsystem.components.GradientTintedIcon
import dev.gaddal.echojournal.core.presentation.designsystem.components.GradientType
import dev.gaddal.echojournal.core.presentation.ui.ObserveAsEvents
import dev.gaddal.echojournal.journal.entries.components.AudioPlayer
import dev.gaddal.echojournal.journal.entry.components.MoodBottomSheet
import dev.gaddal.echojournal.journal.entry.components.TopicSearcherEntry
import org.koin.androidx.compose.koinViewModel

@Composable
fun EntryScreenRoot(
    onBackClick: () -> Unit,
    viewModel: EntryViewModel = koinViewModel(),
) {
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current
    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            is EntryEvent.Error -> {
                keyboardController?.hide()
                Toast.makeText(
                    context,
                    event.error.asString(context),
                    Toast.LENGTH_LONG
                ).show()
            }

            is EntryEvent.SaveEntrySuccess -> {
                onBackClick()
            }
        }
    }

    val state by viewModel.state.collectAsStateWithLifecycle()

    EntryScreen(
        state = state,
        onAction = { action ->
            when (action) {
                is EntryAction.OnBackClick -> onBackClick()
                else -> Unit
            }
            viewModel.onAction(action)
        }
    )
}

@Composable
fun EntryScreen(
    state: EntryState,
    onAction: (EntryAction) -> Unit,
) {
    var showBottomSheet by remember { mutableStateOf(false) }
    EchoJournalScaffold(
        modifier = Modifier,
        withGradient = false,
        topAppBar = {
            EchoJournalTopAppBar(
                title = "New Entry",
                modifier = Modifier.fillMaxWidth(),
                showBackButton = true,
                showSettingsButton = false,
                onBackClick = {
                    onAction(EntryAction.OnBackClick)
                },
                isTitleCentered = true
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(vertical = 8.dp, horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Mood & title
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                //
                var isFocused by remember {
                    mutableStateOf(true)
                }

                if (state.mood != null) {
                    Image(
                        imageVector = ImageVector.vectorResource(id = state.mood.iconRes),
                        contentDescription = null,
                        modifier = Modifier
                            .clickable {
                                showBottomSheet = true
                            }
                            .size(32.dp),
                    )
                } else {
                    Surface(
                        shape = CircleShape,
                        color = Secondary95,
                    ) {
                        IconButton(
                            onClick = {
                                showBottomSheet = true
                            },
                            modifier = Modifier.size(32.dp),
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp),
                                tint = Secondary70
                            )
                        }
                    }
                }
                BasicTextField(
                    state = state.title,
                    textStyle = TextStyle(
                        color = MaterialTheme.colorScheme.onBackground,
                        fontFamily = InterFontFamily,
                        fontWeight = FontWeight.Medium,
                        fontSize = 26.sp,
                        lineHeight = 32.sp
                    ),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text
                    ),
                    lineLimits = TextFieldLineLimits.SingleLine,
                    cursorBrush = SolidColor(MaterialTheme.colorScheme.onBackground),
                    modifier = Modifier
                        .onFocusChanged {
                            isFocused = it.isFocused
                        },
                    decorator = { innerBox ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                            ) {
                                if (state.title.text.isEmpty() && !isFocused) {
                                    Text(
                                        text = "Add Title...", // hint
                                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(
                                            alpha = 0.4f
                                        ),
                                        modifier = Modifier.fillMaxWidth(),
                                        style = MaterialTheme.typography.headlineLarge,
                                    )
                                }
                            }
                        }
                        innerBox()
                    }
                )
            }

            // Audio player & AI transcript button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AudioPlayer(
                    modifier = Modifier.weight(1f),
                    isPlaying = state.isPlayingAudio,
                    isPaused = state.isPausedAudio,
                    currentPosition = state.audioPosition,
                    duration = state.audioDuration,
                    onPlay = { onAction(EntryAction.PlayAudio) },
                    onResume = { onAction(EntryAction.ResumeAudio) },
                    onPause = { onAction(EntryAction.PauseAudio) },
                    onStop = { onAction(EntryAction.StopAudio) },
                    onSeek = { ms -> onAction(EntryAction.SeekTo(ms)) },
                    mood = state.mood
                )

                Surface(
                    shape = CircleShape,
                    shadowElevation = 12.dp,
                ) {
                    IconButton(
                        onClick = { },
                        modifier = Modifier.size(32.dp),
                    ) {
                        Icon(
                            imageVector = AiIcon,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp),
                            tint = if (state.mood != null) state.mood.iconTint else Primary50
                        )
                    }
                }
            }

            // Topic
            TopicSearcherEntry(
                state = state,
                onAction = onAction,
                allTopics = state.filteredTopics,
                selectedTopics = state.selectedTopics,
                onTopicClick = { topic ->
                    onAction(EntryAction.OnTopicSelected(topic))
                },
                onCreateClick = {
                    onAction(EntryAction.OnCreateTopic)
                },
                onClearTopicClick = { topic ->
                    onAction(EntryAction.OnTopicSelected(topic))
                },
            )

            // Description or transcription
            Row(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                //
                var isFocused by remember {
                    mutableStateOf(true)
                }
                GradientTintedIcon(
                    imageVector = if (state.transcription.isNotEmpty()) AiIcon else Icons.Default.Edit,
                    contentDescription = if (state.transcription.isNotEmpty()) "Transcription" else "Description",
                    modifier = Modifier.size(16.dp),
                    gradientType = GradientType.VERTICAL,
                    colors = state.mood?.gradientColors
                        ?: ButtonGradient, // Use mood gradient if available, otherwise use Button gradient
                    solidColor = if (!(state.mood != null && state.transcription.isNotEmpty())) MaterialTheme.colorScheme.outlineVariant else null, // Apply solid color otherwise
                    isWithinScaffold = true
                )
                if (state.transcription.isNotEmpty()) {
                    Text(
                        text = state.transcription,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                BasicTextField(
                    state = state.description,
                    textStyle = TextStyle(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontFamily = InterFontFamily,
                        fontWeight = FontWeight.Normal,
                        fontSize = 14.sp,
                        lineHeight = 20.sp
                    ),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text
                    ),
                    lineLimits = TextFieldLineLimits.MultiLine(),
                    cursorBrush = SolidColor(MaterialTheme.colorScheme.onSurfaceVariant),
                    modifier = Modifier
                        .onFocusChanged {
                            isFocused = it.isFocused
                        },
                    decorator = { innerBox ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                            ) {
                                if (state.description.text.isEmpty() && !isFocused) {
                                    Text(
                                        text = "Add Description...", // hint
                                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(
                                            alpha = 0.4f
                                        ),
                                        modifier = Modifier.fillMaxWidth(),
                                        style = MaterialTheme.typography.bodyMedium,
                                    )
                                }
                            }
                        }
                        innerBox()
                    }
                )
            }

            // Cancel & Save
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(
                    onClick = {
                        onAction(EntryAction.OnCancelChooseMoodClick)
                        onAction(EntryAction.OnBackClick)
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.inverseOnSurface,
                    )
                ) {
                    Text(
                        text = "Cancel",
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.labelLarge,
                    )
                }

                GradientButton(
                    onClick = {
                        onAction(EntryAction.OnSaveNewEntryClick)
                    },
                    modifier = Modifier.weight(2f),
                    enabled = state.mood != null && state.title.text.isNotEmpty(),
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
                    Text(
                        text = "Save",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.labelLarge,
                    )
                }
            }
        }
        MoodBottomSheet(
            showBottomSheet = showBottomSheet,
            onDismiss = { showBottomSheet = false },
            onAction = onAction, // the same callback your VM uses
            state = state // pass state from VM
        )
    }
}

@Preview
@Composable
fun EntryScreenPreview(modifier: Modifier = Modifier) {
    EchoJournalTheme {
        EntryScreen(
            state = EntryState(
                mood = Mood.Peaceful,
                title = rememberTextFieldState(initialText = "My Entry"),
                description = rememberTextFieldState(
                    initialText = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tit amet, consecterur adipiscing"
                ),
                transcription = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tit amet, consecterur adipiscing"
            ),
            onAction = {}
        )
    }
}
