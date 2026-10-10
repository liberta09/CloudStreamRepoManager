package com.kaan.cloudstreamrepomanager.ui.theme

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color

// Modern Material 3 Dark Theme Palette
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

val md_theme_dark_background = Color(0xFF0A0B10)
val md_theme_dark_onBackground = Color(0xFFE2E2E9)
val md_theme_dark_surface = Color(0xFF0A0B10)
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

// Semantic Accents & Status Colors
val successGreen = Color(0xFF10B981) // Neon Emerald
val errorRed = Color(0xFFEF4444)     // Neon Red
val warningYellow = Color(0xFFFBBF24) // Bright Yellow

// Reactive Theme Palette Variables (Tracked by Compose State Engine)
var CyberYellow by mutableStateOf(warningYellow)
var CyberCyan by mutableStateOf(Color(0xFF00E5FF))
var CyberPink by mutableStateOf(Color(0xFFA855F7))
var CyberPurple by mutableStateOf(Color(0xFF8B5CF6))
var CyberBlue by mutableStateOf(Color(0xFF0072FF))
var CyberOrange by mutableStateOf(warningYellow)
var CyberGreen by mutableStateOf(successGreen)

var CyberBgDark by mutableStateOf(Color(0xFF0C0E1A))       // Deep Cyber Navy
var CyberSurfaceDark by mutableStateOf(Color(0xFF131627))  // Cyber Glass Surface
var CyberCardDark by mutableStateOf(Color(0xFF181C30))     // Cyber Card Surface
var CyberBorder by mutableStateOf(Color(0x3300E5FF))       // Cyan Glass Border
var CyberBorderFocused by mutableStateOf(Color(0xFF00E5FF)) // Electric Cyan Focus
var CyberTextPrimary by mutableStateOf(Color(0xFFF1F5F9))   // Crisp White
var CyberTextSecondary by mutableStateOf(Color(0xFF94A3B8)) // Slate Grey
var CyberAccent by mutableStateOf(Color(0xFF8B5CF6))       // Vivid Purple Accent

// Theme Applicator
fun applyThemeColors(themeName: String) {
    when (themeName) {
        "Camel" -> {
            CyberBgDark = Color(0xFF1C1917)       // Deep Warm Stone
            CyberSurfaceDark = Color(0xFF292524)  // Warm Dark Glass
            CyberCardDark = Color(0xFF322D2B)     // Lighter Warm Card
            CyberAccent = Color(0xFFF59E0B)       // Golden Amber
            CyberTextPrimary = Color(0xFFFAFAF9)   // Off-white
            CyberTextSecondary = Color(0xFFA8A29E) // Muted warm grey
            CyberBorder = Color(0x33F59E0B)
            CyberBorderFocused = Color(0xFFF59E0B)
            
            CyberCyan = Color(0xFFF59E0B)
            CyberPurple = Color(0xFFD97706)
            CyberYellow = Color(0xFFFBBF24)
        }
        "Indigo" -> {
            CyberBgDark = Color(0xFF0B132B)       // Deep Navy Midnight
            CyberSurfaceDark = Color(0xFF1C2541)  // Dark Navy Surface
            CyberCardDark = Color(0xFF232D4F)     // Navy Glass Card
            CyberAccent = Color(0xFF48CAE4)       // Electric Cyan
            CyberTextPrimary = Color(0xFFF8FAFC)   // Crisp White
            CyberTextSecondary = Color(0xFF7B7CA0) // Muted blue-grey
            CyberBorder = Color(0x3348CAE4)
            CyberBorderFocused = Color(0xFF48CAE4)
            
            CyberCyan = Color(0xFF00B4D8)
            CyberPurple = Color(0xFF5390D9)
            CyberYellow = warningYellow
        }
        else -> { // Default "Darknes Purple / Cyber Neon"
            CyberBgDark = Color(0xFF0C0E1A)       // Deep Cyber Navy
            CyberSurfaceDark = Color(0xFF131627)  // Cyber Glass Surface
            CyberCardDark = Color(0xFF181C30)     // Cyber Card Surface
            CyberAccent = Color(0xFF8B5CF6)       // Vivid Purple/Neon
            CyberTextPrimary = Color(0xFFF1F5F9)   // Crisp White
            CyberTextSecondary = Color(0xFF94A3B8) // Slate Grey
            CyberBorder = Color(0x3300E5FF)       // Cyan Glass Border
            CyberBorderFocused = Color(0xFF00E5FF) // Electric Cyan Focus
            
            CyberCyan = Color(0xFF00E5FF)
            CyberPurple = Color(0xFF8B5CF6)
            CyberYellow = warningYellow
        }
    }
}
