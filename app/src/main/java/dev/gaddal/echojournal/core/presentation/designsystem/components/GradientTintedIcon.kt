package dev.gaddal.echojournal.core.presentation.designsystem.components

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.gaddal.echojournal.core.presentation.designsystem.AiIcon

enum class GradientType {
    HORIZONTAL,
    VERTICAL,
    LINEAR,
    RADIAL,
    SWEEP
}

/**
 * A composable function that creates an Icon with either a solid color tint or a gradient tint.
 *
 * @param imageVector The [ImageVector] to be used for the icon.
 * @param contentDescription The content description for accessibility purposes.
 * @param modifier Modifier to be applied to the Icon.
 * @param gradientBrush Optional [Brush] to use for a custom gradient tint. If provided, [gradientType] and [colors] are ignored.
 * @param gradientType Optional [GradientType] to define the type of gradient tint. Used if [gradientBrush] is not provided. Defaults to [GradientType.LINEAR] if not specified.
 * @param colors List of [Color]s to be used for the gradient. Defaults to Red and Yellow if not provided.
 * @param solidColor Optional [Color] to use for a solid color tint. If provided, gradient tinting is disabled, and a standard tinted Icon is rendered.
 * @param blendMode The [BlendMode] to use for applying the gradient. Defaults to [BlendMode.SrcAtop].
 * @param isWithinScaffold Boolean flag to indicate if the icon is used within a Scaffold.
 */
@Composable
fun GradientTintedIcon(
    imageVector: ImageVector,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    gradientBrush: Brush? = null,
    gradientType: GradientType? = null,
    colors: List<Color> = listOf(Color.Red, Color.Yellow), // Default gradient colors
    solidColor: Color? = null, // Parameter for solid color tint
    blendMode: BlendMode = BlendMode.SrcAtop, // Default blendMode
    isWithinScaffold: Boolean = false // **NEW: isWithinScaffold parameter**
) {
    // Determine BlendMode based on isWithinScaffold
    val actualBlendMode = if (isWithinScaffold) {
        BlendMode.Lighten // Or BlendMode.Screen or BlendMode.Plus, whichever you prefer visually
    } else {
        blendMode // Use the default blendMode (SrcAtop) when not in Scaffold
    }

    if (solidColor != null) {
        // If solidColor is provided, render a standard Icon with solid color tint
        Icon(
            imageVector = imageVector,
            contentDescription = contentDescription,
            modifier = modifier,
            tint = solidColor
        )
    } else {
        // If solidColor is null, apply gradient tint logic
        // Determine the brush to use for the gradient
        val brushToUse = gradientBrush ?: when (gradientType) {
            GradientType.HORIZONTAL -> Brush.horizontalGradient(colors = colors)
            GradientType.VERTICAL -> Brush.verticalGradient(colors = colors)
            GradientType.SWEEP -> Brush.sweepGradient(colors = colors)
            GradientType.RADIAL -> Brush.radialGradient(
                colors = colors,
                center = Offset.Zero,
                radius = Float.POSITIVE_INFINITY,
                tileMode = TileMode.Clamp // Clamp tile mode to extend edge colors
            )

            GradientType.LINEAR, null -> Brush.linearGradient(
                colors = colors,
                start = Offset.Zero,
                end = Offset.Zero // Default linear gradient
            )
        }

        Icon(
            imageVector = imageVector,
            contentDescription = contentDescription,
            modifier = modifier
                .drawWithCache {
                    // Special case for linear gradient to make it diagonal for icon tint
                    val actualBrush = if (gradientType == GradientType.LINEAR) {
                        Brush.linearGradient(
                            colors = colors,
                            start = Offset.Zero,
                            end = Offset(size.width, size.height) // Diagonal gradient
                        )
                    } else {
                        brushToUse // Use the determined brush for other gradient types
                    }
                    onDrawWithContent {
                        drawContent() // Draw the original icon content
                        drawRect(
                            brush = actualBrush, // Apply the gradient brush
                            topLeft = Offset.Zero,
                            size = size,
                            blendMode = actualBlendMode, // **Use the dynamically selected blendMode**
                            style = Fill // Fill the rectangle with the gradient
                        )
                    }
                },
            tint = Color.Unspecified // Important: Set tint to Unspecified to enable gradient drawing
        )
    }
}

@Preview
@Composable
fun SolidColorIconExample() {
    GradientTintedIcon(
        imageVector = AiIcon,
        contentDescription = "Favorite Icon with Solid Color",
        modifier = Modifier.size(100.dp),
        solidColor = Color.Green // Solid green color tint
    )
}

@Preview
@Composable
fun HorizontalGradientIconExample() {
    GradientTintedIcon(
        imageVector = AiIcon,
        contentDescription = "Favorite Icon with Horizontal Gradient",
        modifier = Modifier.size(100.dp),
        gradientType = GradientType.HORIZONTAL,
        colors = listOf(Color(0xFF64B5F6), Color(0xFF0D47A1)) // Blue horizontal gradient
    )
}

@Preview
@Composable
fun VerticalGradientIconExample() {
    GradientTintedIcon(
        imageVector = AiIcon,
        contentDescription = "Favorite Icon with Vertical Gradient",
        modifier = Modifier.size(100.dp),
        gradientType = GradientType.VERTICAL,
        colors = listOf(Color.Yellow, Color.Red) // Yellow to red vertical gradient
    )
}

@Preview
@Composable
fun LinearGradientIconExample() {
    GradientTintedIcon(
        imageVector = AiIcon,
        contentDescription = "Favorite Icon with Linear Gradient",
        gradientType = GradientType.LINEAR,
        modifier = Modifier.size(100.dp),
        colors = listOf(Color.Green, Color.Blue) // Green to blue linear gradient
    )
}

@Preview
@Composable
fun RadialGradientIconExample() {
    GradientTintedIcon(
        imageVector = AiIcon,
        contentDescription = "Favorite Icon with Radial Gradient",
        modifier = Modifier.size(100.dp),
        gradientType = GradientType.RADIAL,
        colors = listOf(Color.Cyan, Color.Magenta) // Cyan to magenta radial gradient
    )
}

@Preview
@Composable
fun SweepGradientIconExample() {
    GradientTintedIcon(
        imageVector = AiIcon,
        contentDescription = "Favorite Icon with Sweep Gradient",
        modifier = Modifier.size(100.dp),
        gradientType = GradientType.SWEEP,
        colors = listOf(Color.Magenta, Color.Green, Color.Yellow) // Multi-color sweep gradient
    )
}

@Preview
@Composable
fun CustomBrushGradientIconExample() {
    val customBrush = Brush.radialGradient(
        colors = listOf(Color.White, Color.Black),
        center = Offset(50f, 50f), // Center of the radial gradient
        radius = 70f, // Radius of the radial gradient
        tileMode = TileMode.Mirror // Mirror tile mode for repeating gradient
    )
    GradientTintedIcon(
        imageVector = AiIcon,
        contentDescription = "Favorite Icon with Custom Brush Gradient",
        modifier = Modifier.size(100.dp),
        gradientBrush = customBrush // Passing a custom Brush
    )
}

@Preview
@Composable
fun CustomColorsGradientIconExample() {
    GradientTintedIcon(
        imageVector = AiIcon,
        contentDescription = "Favorite Icon with Custom Colors Horizontal Gradient",
        modifier = Modifier.size(100.dp),
        gradientType = GradientType.HORIZONTAL,
        colors = listOf(Color(0xFF9C27B0), Color(0xFFE040FB)) // Custom purple color palette
    )
}
