package dev.gaddal.echojournal.core.presentation.designsystem.colors

import androidx.compose.ui.graphics.Color

// Colors/Palettes
val Primary10 = Color(0xFF001945)
val Primary30 = Color(0xFF004CB4)
val Primary40 = Color(0xFF0057CC)
val Primary50 = Color(0xFF1F70F5)
val Primary60 = Color(0xFF578CFF)
val Primary90 = Color(0xFFD9E2FF)
val Primary95 = Color(0xFFEEF0FF)
val Primary100 = Color(0xFFFFFFFF)

val Secondary30 = Color(0xFF3B4663)
val Secondary50 = Color(0xFF6B7796)
val Secondary70 = Color(0xFF9FABCD)
val Secondary80 = Color(0xFFBAC6E9)
val Secondary90 = Color(0xFFD9E2FF)
val Secondary95 = Color(0xFFEEF0FF)

val NeutralVariant10 = Color(0xFF191A20)
val NeutralVariant30 = Color(0xFF40434F)
val NeutralVariant50 = Color(0xFF6C7085)
val NeutralVariant80 = Color(0xFFC1C3CE)
val NeutralVariant90 = Color(0xFFE0E1E7)
val NeutralVariant99 = Color(0xFFFCFDFE)

val Error20 = Color(0xFF680014)
val Error95 = Color(0xFFFFEDEC)
val Error100 = Color(0xFFFFFFFF)

// Gradients
val BGGradient = listOf(Secondary90.copy(alpha = 0.4f), Secondary95.copy(alpha = 0.4f))
val BGSaturatedGradient = listOf(Secondary90, Secondary95)
val ButtonGradient = listOf(Primary60, Primary50)
val ButtonPressedGradient = listOf(Primary60, Primary40)

// Mood
val Sad25 = Color(0xFFEFF4F8)
val Sad35 = Color(0xFFC5D8E9)
val Sad80 = Color(0xFF3A8EDE)
val Sad95 = Color(0xFF004585)
val SadGradient = listOf(Color(0xFF7BBCFA), Color(0xFF2993F7))

val Stressed25 = Color(0xFFF8EFEF)
val Stressed35 = Color(0xFFE9C5C5)
val Stressed80 = Color(0xFFDE3A3A)
val Stressed95 = Color(0xFF6B0303)
val StressedGradient = listOf(Color(0xFFF69193), Color(0xFFED3A3A))

val Neutral25 = Color(0xFFEEF7F3)
val Neutral35 = Color(0xFFB9DDCB)
val Neutral80 = Color(0xFF41B278)
val Neutral95 = Color(0xFF0A5F33)
val NeutralGradient = listOf(Color(0xFFC4F3DB), Color(0xFF71EBAC))

val Peaceful25 = Color(0xFFF6F2F5)
val Peaceful35 = Color(0xFFE1CEDB)
val Peaceful80 = Color(0xFFBE3294)
val Peaceful95 = Color(0xFF6C044D)
val PeacefulGradient = listOf(Color(0xFFFCCDEE), Color(0xFFF991E0))

val Excited25 = Color(0xFFF5F3EF)
val Excited35 = Color(0xFFDDD2C8)
val Excited80 = Color(0xFFDB6C0B)
val Excited95 = Color(0xFF723602)
val ExcitedGradient = listOf(Color(0xFFF5CB6F), Color(0xFFF6B01A))
