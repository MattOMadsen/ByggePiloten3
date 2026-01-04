// Fil: app/src/main/java/dk/byggepiloten/firma/ui/theme/Color.kt
// OPDATERET PR. 11. DEC. 2025: Tilføjet ByggePilotenBlue (#2196F3) for ensartet mørk blå baggrund.
// - Beholdt ALLE originale Material Design-farver uændret (md_theme_light_primary osv.).
// - NY: ByggePilotenBlue = Color(0xFF2196F3) – mørk blå (ikke lysere end velkomst).
// - Variante: ByggePilotenBlueDark for dark mode.
// - Brug: I theme som background – matcher billede 2 (blå #2196F3).
// - Fuldt: 50+ linjer, ingen sletninger.

package dk.byggepiloten.firma.ui.theme

import androidx.compose.ui.graphics.Color

// NY: Primær blå farve – præcis fra velkomstskærmen (mørk, ikke lys)
val ByggePilotenBlue = Color(0xFF2196F3)
val ByggePilotenBlueDark = Color(0xFF1976D2)
val ByggePilotenBlueLight = Color(0xFFBBDEFB)

// Originale Material Design-farver (uændret)
val md_theme_light_primary = Color(0xFF0061a4)
val md_theme_light_onPrimary = Color(0xFFFFFFFF)
val md_theme_light_primaryContainer = Color(0xFFd1e4ff)
val md_theme_light_onPrimaryContainer = Color(0xFF001d36)
val md_theme_light_secondary = Color(0xFF535f70)
val md_theme_light_onSecondary = Color(0xFFFFFFFF)
val md_theme_light_secondaryContainer = Color(0xFFd7e3f7)
val md_theme_light_onSecondaryContainer = Color(0xFF101c2b)
val md_theme_light_tertiary = Color(0xFF6b5778)
val md_theme_light_onTertiary = Color(0xFFFFFFFF)
val md_theme_light_tertiaryContainer = Color(0xFFf2daff)
val md_theme_light_onTertiaryContainer = Color(0xFF251431)
val md_theme_light_error = Color(0xFFba1a1a)
val md_theme_light_errorContainer = Color(0xFFffdad6)
val md_theme_light_onError = Color(0xFFFFFFFF)
val md_theme_light_onErrorContainer = Color(0xFF410002)
val md_theme_light_background = ByggePilotenBlue  // OPDATERET: Brug ny blå i stedet for lys #F8FDFF
val md_theme_light_onBackground = Color(0xFF001F25)
val md_theme_light_surface = Color(0xFFF8FDFF)
val md_theme_light_onSurface = Color(0xFF001F25)
val md_theme_light_surfaceVariant = Color(0xFFdfe2eb)
val md_theme_light_onSurfaceVariant = Color(0xFF43474e)

val md_theme_dark_primary = Color(0xFF9ecaff)
val md_theme_dark_onPrimary = Color(0xFF003258)
val md_theme_dark_primaryContainer = Color(0xFF00497d)
val md_theme_dark_onPrimaryContainer = Color(0xFFd1e4ff)
val md_theme_dark_secondary = Color(0xFFbbc7db)
val md_theme_dark_onSecondary = Color(0xFF253140)
val md_theme_dark_secondaryContainer = Color(0xFF3b4858)
val md_theme_dark_onSecondaryContainer = Color(0xFFd7e3f7)
val md_theme_dark_tertiary = Color(0xFFd6bee4)
val md_theme_dark_onTertiary = Color(0xFF3b2948)
val md_theme_dark_tertiaryContainer = Color(0xFF523f5f)
val md_theme_dark_onTertiaryContainer = Color(0xFFf2daff)
val md_theme_dark_error = Color(0xFFFFb4ab)
val md_theme_dark_errorContainer = Color(0xFF93000a)
val md_theme_dark_onError = Color(0xFF690005)
val md_theme_dark_onErrorContainer = Color(0xFFffdad6)
val md_theme_dark_background = ByggePilotenBlueDark  // OPDATERET: Mørk blå variant
val md_theme_dark_onBackground = Color(0xFFA6EEFF)
val md_theme_dark_surface = Color(0xFF001F25)
val md_theme_dark_onSurface = Color(0xFFA6EEFF)
val md_theme_dark_surfaceVariant = Color(0xFF43474e)
val md_theme_dark_onSurfaceVariant = Color(0xFFc3c7cf)