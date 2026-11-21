package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val LightChakaiColorScheme = lightColorScheme(
    primary = ChakaiOrange,
    onPrimary = PureWhite,
    primaryContainer = ChakaiOrangeContainer,
    onPrimaryContainer = ChakaiOnOrangeContainer,
    secondary = KiranaGreen,
    onSecondary = PureWhite,
    secondaryContainer = KiranaGreenContainer,
    onSecondaryContainer = KiranaOnGreenContainer,
    tertiary = WarmGold,
    onTertiary = InkPrimary,
    tertiaryContainer = WarmGoldContainer,
    onTertiaryContainer = WarmGoldText,
    background = WarmBackground,
    onBackground = InkPrimary,
    surface = PureWhite,
    onSurface = InkPrimary,
    surfaceVariant = SoftSurfaceVariant,
    onSurfaceVariant = InkSecondary,
    outline = SubtleOutline,
    outlineVariant = Color(0xFFEDE7DE),
    error = StatusRed,
    onError = PureWhite,
    errorContainer = StatusRedContainer,
    onErrorContainer = Color(0xFF7F1D1D)
)

private val DarkChakaiColorScheme = darkColorScheme(
    primary = Color(0xFFFF8533),
    onPrimary = Color(0xFF331400),
    primaryContainer = Color(0xFF662900),
    onPrimaryContainer = ChakaiOrangeContainer,
    secondary = Color(0xFF4ADE80),
    onSecondary = Color(0xFF052E16),
    secondaryContainer = Color(0xFF14532D),
    onSecondaryContainer = KiranaGreenContainer,
    tertiary = WarmGold,
    onTertiary = InkPrimary,
    background = DarkBackground,
    onBackground = Color(0xFFF5F2EE),
    surface = DarkSurface,
    onSurface = Color(0xFFF5F2EE),
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = Color(0xFFC7C0B8),
    outline = DarkOutline,
    error = Color(0xFFF87171),
    onError = Color(0xFF450A0A)
)

val ChakaiShapes = Shapes(
    small = RoundedCornerShape(10.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(22.dp),
    extraLarge = RoundedCornerShape(28.dp)
)

@Composable
fun ChakaiGoTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkChakaiColorScheme else LightChakaiColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = ChakaiShapes,
        content = content
    )
}
