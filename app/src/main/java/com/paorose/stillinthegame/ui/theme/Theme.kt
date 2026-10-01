package com.paorose.stillinthegame.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val Scheme = darkColorScheme(
    primary = CourtOrange,
    onPrimary = Midnight,
    secondary = ElectricBlue,
    onSecondary = Midnight,
    tertiary = RallyYellow,
    onTertiary = Midnight,
    background = Midnight,
    onBackground = Chalk,
    surface = MidnightRaised,
    onSurface = Chalk
)

@Composable
fun StillTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = Scheme, typography = StillTypography, content = content)
}
