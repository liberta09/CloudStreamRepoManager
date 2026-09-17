package com.kaan.cloudstreamrepomanager.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val CyberColorScheme = darkColorScheme(
    primary = CyberYellow,
    onPrimary = CyberBgDark,
    primaryContainer = CyberSurfaceDark,
    onPrimaryContainer = CyberYellow,
    secondary = CyberCyan,
    onSecondary = CyberBgDark,
    secondaryContainer = CyberSurfaceDark,
    onSecondaryContainer = CyberCyan,
    tertiary = CyberPink,
    onTertiary = CyberBgDark,
    background = CyberBgDark,
    onBackground = CyberTextPrimary,
    surface = CyberSurfaceDark,
    onSurface = CyberTextPrimary,
    surfaceVariant = CyberCardDark,
    onSurfaceVariant = CyberTextSecondary,
    outline = CyberCyan
)

@Composable
fun CloudStreamRepoManagerTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = CyberColorScheme,
        typography = Typography,
        content = content
    )
}
