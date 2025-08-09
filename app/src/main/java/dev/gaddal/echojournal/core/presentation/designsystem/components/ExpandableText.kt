package dev.gaddal.echojournal.core.presentation.designsystem.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withLink
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import dev.gaddal.echojournal.core.presentation.designsystem.EchoJournalTheme
import dev.gaddal.echojournal.core.presentation.designsystem.InterFontFamily

const val DEFAULT_MINIMUM_TEXT_LINE = 3

/**
 * An expandable text component that provides access to truncated text with a dynamic "... Show More"/"Show Less" button.
 *
 * Now only the "Show More" and "Show Less" portions are clickable, using [LinkAnnotation.Clickable].
 *
 * @param modifier Modifier for the Box containing the text.
 * @param style The TextStyle to apply to the text.
 * @param fontStyle The FontStyle to apply to the text.
 * @param text The text to be displayed.
 * @param collapsedMaxLine The maximum number of lines to display when collapsed.
 * @param showMoreText The text to display for "... Show More" button.
 * @param showMoreStyle The SpanStyle for "... Show More" button.
 * @param showLessText The text to display for "Show Less" button.
 * @param showLessStyle The SpanStyle for "Show Less" button.
 * @param textAlign The alignment of the text.
 * @param fontSize The font size of the text.
 */
@Composable
fun ExpandableText(
    modifier: Modifier = Modifier,
    style: TextStyle = LocalTextStyle.current,
    fontStyle: FontStyle? = null,
    text: String,
    collapsedMaxLine: Int = DEFAULT_MINIMUM_TEXT_LINE,
    showMoreText: String = "Show More",
    showMoreStyle: SpanStyle = SpanStyle(
        color = MaterialTheme.colorScheme.primary,
        fontFamily = InterFontFamily,
        fontWeight = FontWeight.W500,
        fontSize = 14.sp,
    ),
    showLessText: String = "Show Less",
    showLessStyle: SpanStyle = showMoreStyle,
    textAlign: TextAlign? = null,
    fontSize: TextUnit = TextUnit.Unspecified,
) {
    // Track expanded/collapsed state, whether truncation is needed, and where it happens
    var isExpanded by remember { mutableStateOf(false) }
    var clickable by remember { mutableStateOf(false) }
    var lastCharIndex by remember { mutableIntStateOf(0) }

    // Define link annotations for Show More and Show Less
    val showMoreLink = LinkAnnotation.Clickable(
        tag = "show_more",
        styles = TextLinkStyles(showMoreStyle)
    ) {
        isExpanded = true
    }

    val showLessLink = LinkAnnotation.Clickable(
        tag = "show_less",
        styles = TextLinkStyles(showLessStyle)
    ) {
        isExpanded = false
    }

    // Build the annotated string, only making the "Show More" / "Show Less" segment clickable
    val annotatedText = buildAnnotatedString {
        if (clickable) {
            if (isExpanded) {
                // Expanded: show all text + "Show Less"
                append(text)
                append(" ")
                withLink(showLessLink) {
                    append(showLessText)
                }
            } else {
                // Collapsed: show truncated text + "... Show More"
                val adjustedText = text
                    .substring(startIndex = 0, endIndex = lastCharIndex)
                    // Remove extra chars used to "reserve space" for the Show More text
                    .dropLast(showMoreText.length * 2)
                    // Remove trailing whitespace or periods
                    .dropLastWhile { it.isWhitespace() || it == '.' }

                // 1) Append truncated text
                append(adjustedText)
                // 2) Append ellipsis
                append("... ")
                // 3) Append “Show More” link
                withLink(showMoreLink) {
                    append(showMoreText)
                }
            }
        } else {
            // No click needed => no overflow => show entire text
            append(text)
        }
    }

    // Just wrap in a Box for styling if desired; no click modifier here
    Box(modifier = modifier) {
        Text(
            text = annotatedText,
            modifier = Modifier
                .fillMaxWidth()
                .animateContentSize(),
            maxLines = if (isExpanded) Int.MAX_VALUE else collapsedMaxLine,
            fontStyle = fontStyle,
            // Determine if the text overflows when not expanded
            onTextLayout = { textLayoutResult ->
                if (!isExpanded && textLayoutResult.hasVisualOverflow) {
                    clickable = true
                    // Record where we need to truncate
                    lastCharIndex = textLayoutResult.getLineEnd(collapsedMaxLine - 1)
                }
            },
            style = style,
            textAlign = textAlign,
            fontSize = fontSize
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ExpandableTextPreview() {
    EchoJournalTheme {
        val description = "Lorem ipsum dolor sit amet, " +
            "consectetur adipiscing elit. Sed do eiusmod tempor incididunt ut labore et dolore " +
            "magna aliqua. " +
            "Proin in nisl vitae justo viverra bibendum vitae vel nulla. "

        ExpandableText(
            style = MaterialTheme.typography.bodyMedium,
            text = description.repeat(3), // Just to force some overflow
        )
    }
}
