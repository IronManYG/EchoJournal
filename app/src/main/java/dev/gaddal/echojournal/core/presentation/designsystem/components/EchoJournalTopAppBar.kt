@file:OptIn(ExperimentalMaterial3Api::class)

package dev.gaddal.echojournal.core.presentation.designsystem.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.gaddal.echojournal.R
import dev.gaddal.echojournal.core.presentation.designsystem.EchoJournalTheme
import dev.gaddal.echojournal.core.presentation.designsystem.components.util.DropDownItem

/**
 * A customizable top app bar component for EchoJournal applications.
 *
 * This composable provides a flexible app bar with various configuration options including:
 * - Centered or start-aligned titles
 * - Custom title text styling
 * - Optional navigation/back button
 * - Optional settings button
 * - Expandable dropdown menu
 * - Custom start content (e.g., logo)
 * - Scroll behavior integration
 *
 * @param title The text to display in the app bar's title area
 * @param modifier Modifier to be applied to the TopAppBar
 * @param showBackButton Whether to show the navigation/back button
 * @param showSettingsButton Whether to show the settings button when no menu items are present
 * @param menuItems List of items for the dropdown menu (creates a menu button when non-empty)
 * @param onMenuItemClick Callback for when a dropdown menu item is selected
 * @param onBackClick Callback for when the back button is clicked
 * @param onSettingsClick Callback for when the settings button is clicked
 * @param scrollBehavior Scroll behavior for the app bar
 * @param startContent Optional composable content to display before the title
 * @param isTitleCentered Whether to center the title horizontally in the app bar
 * @param titleTextStyle The text style to apply to the title
 */
@Composable
fun EchoJournalTopAppBar(
    title: String,
    modifier: Modifier = Modifier,
    showBackButton: Boolean,
    showSettingsButton: Boolean,
    menuItems: List<DropDownItem> = emptyList(),
    onMenuItemClick: (Int) -> Unit = {},
    onBackClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
    scrollBehavior: TopAppBarScrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(),
    startContent: (@Composable () -> Unit)? = null,
    isTitleCentered: Boolean = false,
    titleTextStyle: TextStyle = MaterialTheme.typography.headlineLarge,
) {
    // State management for dropdown menu visibility
    var isDropDownOpen by rememberSaveable { mutableStateOf(false) }

    TopAppBar(
        title = {
            // Title row with conditional centering logic
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = if (isTitleCentered) Modifier.fillMaxWidth() else Modifier,
                horizontalArrangement = if (isTitleCentered) Arrangement.Center else Arrangement.Start
            ) {
                // Optional start content with spacer
                startContent?.let {
                    it()
                    Spacer(modifier = Modifier.width(8.dp))
                }

                // Title text with custom styling
                Text(
                    text = title,
                    style = titleTextStyle,
                    overflow = TextOverflow.Ellipsis,
                    softWrap = true,
                    maxLines = 1,
                )
            }
        },
        modifier = modifier,
        navigationIcon = {
            // Back button with edge case handling
            if (showBackButton) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.Default.ArrowBackIosNew,
                        contentDescription = stringResource(id = R.string.go_back),
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        },
        actions = {
            // Dynamic actions section
            when {
                menuItems.isNotEmpty() -> {
                    // Dropdown menu implementation
                    Box {
                        DropdownMenu(
                            expanded = isDropDownOpen,
                            onDismissRequest = { isDropDownOpen = false }
                        ) {
                            menuItems.forEachIndexed { index, item ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .clickable {
                                            onMenuItemClick(index)
                                            isDropDownOpen = false
                                        }
                                        .fillMaxWidth()
                                        .padding(16.dp)
                                ) {
                                    Icon(
                                        imageVector = item.icon,
                                        contentDescription = item.title
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(text = item.title)
                                }
                            }
                        }
                        IconButton(onClick = { isDropDownOpen = true }) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = stringResource(id = R.string.open_menu),
                            )
                        }
                    }
                }

                showSettingsButton -> {
                    // Fallback to settings button
                    IconButton(onClick = onSettingsClick) {
                        Icon(
                            imageVector = Icons.Outlined.Settings,
                            contentDescription = stringResource(id = R.string.settings),
                        )
                    }
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent
        ),
        scrollBehavior = scrollBehavior,
    )
}

/**
 * Preview showing the centered title configuration with custom text style.
 * Demonstrates:
 * - Centered title alignment
 * - Modified headline text style
 * - Optional start content (commented out)
 */
@Preview(showBackground = true)
@Composable
private fun EchoJournalTopAppBarPreview() {
    EchoJournalTheme {
        EchoJournalTopAppBar(
            title = "Echo Journal",
            modifier = Modifier.fillMaxWidth(),
            showBackButton = false,
            showSettingsButton = true,
            isTitleCentered = false,
            titleTextStyle = MaterialTheme.typography.headlineLarge,
            startContent = {
                // Example of start content (logo icon):
                // Icon(
                //     imageVector = Icons.Default.Home,
                //     contentDescription = null,
                //     modifier = Modifier.size(35.dp)
                // )
            },
            menuItems = listOf(
                // Example of dropdown menu items:
//                DropDownItem(
//                    icon = Icons.Default.Person,
//                    title = "Profile"
//                )
            )
        )
    }
}
