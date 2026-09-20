package com.kaan.cloudstreamrepomanager.ui.theme

import androidx.compose.ui.graphics.Color

// Modern Material 3 Dark Theme Palette (Professional & Clean)
val md_theme_dark_primary = Color(0xFFAEC6FF)
val md_theme_dark_onPrimary = Color(0xFF002E69)
val md_theme_dark_primaryContainer = Color(0xFF194483)
val md_theme_dark_onPrimaryContainer = Color(0xFFD7E2FF)

val md_theme_dark_secondary = Color(0xFFBEC6DC)
val md_theme_dark_onSecondary = Color(0xFF283141)
val md_theme_dark_secondaryContainer = Color(0xFF3E4759)
val md_theme_dark_onSecondaryContainer = Color(0xFFDAE2F9)

val md_theme_dark_tertiary = Color(0xFFDEBCDF)
val md_theme_dark_onTertiary = Color(0xFF402843)
val md_theme_dark_tertiaryContainer = Color(0xFF583E5B)
val md_theme_dark_onTertiaryContainer = Color(0xFFFBD7FC)

val md_theme_dark_error = Color(0xFFFFB4AB)
val md_theme_dark_errorContainer = Color(0xFF93000A)
val md_theme_dark_onError = Color(0xFF690005)
val md_theme_dark_onErrorContainer = Color(0xFFFFDAD6)

val md_theme_dark_background = Color(0xFF111318)
val md_theme_dark_onBackground = Color(0xFFE2E2E9)
val md_theme_dark_surface = Color(0xFF111318)
val md_theme_dark_onSurface = Color(0xFFE2E2E9)
val md_theme_dark_surfaceVariant = Color(0xFF44474E)
val md_theme_dark_onSurfaceVariant = Color(0xFFC4C6D0)
val md_theme_dark_outline = Color(0xFF8E9099)
val md_theme_dark_inverseOnSurface = Color(0xFF111318)
val md_theme_dark_inverseSurface = Color(0xFFE2E2E9)
val md_theme_dark_inversePrimary = Color(0xFF355CA8)
val md_theme_dark_surfaceTint = Color(0xFFAEC6FF)
val md_theme_dark_outlineVariant = Color(0xFF44474E)
val md_theme_dark_scrim = Color(0xFF000000)

// Semantic Accents
val successGreen = Color(0xFF81C784)
val warningYellow = Color(0xFFFFD54F)

// Legacy Fallbacks (To prevent breaking existing code during migration if any variable is missed)
val CyberYellow = warningYellow
val CyberCyan = md_theme_dark_primary
val CyberPink = md_theme_dark_error
val CyberOrange = warningYellow
val CyberGreen = successGreen
val CyberBgDark = md_theme_dark_background
val CyberSurfaceDark = md_theme_dark_surface
val CyberCardDark = Color(0xFF1A1C22)
val CyberBorder = md_theme_dark_outlineVariant
val CyberBorderFocused = md_theme_dark_primary
val CyberTextPrimary = md_theme_dark_onSurface
val CyberTextSecondary = md_theme_dark_onSurfaceVariant
