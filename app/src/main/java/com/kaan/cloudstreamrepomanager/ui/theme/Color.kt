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

val md_theme_dark_background = Color(0xFF0A0B10) // Very dark navy/purple
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

// Semantic Accents
val successGreen = Color(0xFF4CAF50)
val errorRed = Color(0xFFF44336)
val warningYellow = Color(0xFFFFD54F)

var CyberYellow = warningYellow
var CyberCyan = Color(0xFF8B5CF6)
var CyberPink = errorRed
var CyberPurple = Color(0xFF8B5CF6)
var CyberBlue = Color(0xFF8B5CF6)
var CyberOrange = warningYellow
var CyberGreen = successGreen

var CyberBgDark = Color(0xFF1C1428)       
var CyberSurfaceDark = Color(0xFF1C1428)  
var CyberCardDark = Color(0xFF2A1F3D)     
var CyberBorder = Color(0x337C6B9E)       
var CyberBorderFocused = Color(0xFF8B5CF6) 
var CyberTextPrimary = Color(0xFFF0E8FF)
var CyberTextSecondary = Color(0xFF7C6B9E) 
var CyberAccent = Color(0xFF8B5CF6)       

// Theme Applicator
fun applyThemeColors(themeName: String) {
    when (themeName) {
        "Camel" -> {
            CyberBgDark = Color(0xFF2C2416)
            CyberSurfaceDark = Color(0xFF2C2416)
            CyberCardDark = Color(0xFF3A3020)
            CyberAccent = Color(0xFFC8A84B)
            CyberTextPrimary = Color(0xFFF5ECD7)
            CyberTextSecondary = Color(0xFF9E8E6A)
            CyberBorder = Color(0x339E8E6A)
            
            CyberCyan = CyberAccent
            CyberPurple = CyberAccent
            CyberBorderFocused = CyberAccent
        }
        "Indigo" -> {
            CyberBgDark = Color(0xFF1A1B2E)
            CyberSurfaceDark = Color(0xFF1A1B2E)
            CyberCardDark = Color(0xFF252640)
            CyberAccent = Color(0xFF6C7BFF)
            CyberTextPrimary = Color(0xFFE8E8FF)
            CyberTextSecondary = Color(0xFF7B7CA0)
            CyberBorder = Color(0x337B7CA0)
            
            CyberCyan = CyberAccent
            CyberPurple = CyberAccent
            CyberBorderFocused = CyberAccent
        }
        else -> { // Default "Darknes Purple"
            CyberBgDark = Color(0xFF1C1428)       
            CyberSurfaceDark = Color(0xFF1C1428)  
            CyberCardDark = Color(0xFF2A1F3D)     
            CyberAccent = Color(0xFF8B5CF6)       
            CyberTextPrimary = Color(0xFFF0E8FF)
            CyberTextSecondary = Color(0xFF7C6B9E) 
            CyberBorder = Color(0x337C6B9E)       
            
            CyberCyan = CyberAccent
            CyberPurple = CyberAccent
            CyberBorderFocused = CyberAccent
        }
    }
}
