package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = ElectricLime,
    onPrimary = PitchBlack,
    primaryContainer = StadiumForest,
    onPrimaryContainer = ElectricLimeSoft,
    secondary = ChampionshipGold,
    onSecondary = PitchBlack,
    secondaryContainer = StadiumSurfaceElevated,
    onSecondaryContainer = ChampionshipGold,
    tertiary = MatchLiveCoral,
    onTertiary = Color.White,
    background = PitchBlack,
    onBackground = TextCream,
    surface = PitchDarkEmerald,
    onSurface = TextCream,
    surfaceVariant = StadiumSurface,
    onSurfaceVariant = TextMuted,
    outline = StadiumBorder,
    outlineVariant = StadiumBorderSubtle
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Keep the signature for compatibility, but preserve our tournament branding
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
