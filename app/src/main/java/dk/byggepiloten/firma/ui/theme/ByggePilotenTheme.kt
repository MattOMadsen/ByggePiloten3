// Fil: app/src/main/java/dk/byggepiloten/firma/ui/theme/ByggePilotenTheme.kt
// OPDATERET PR. 11. DEC. 2025: Ensartet blå baggrund på ALLE screens (via md_theme_light_background = ByggePilotenBlue).
// - Beholdt ALLE originale features: Dynamic colors, edge-to-edge, WindowInsetsControllerCompat (ingen deprecation).
// - RETTET: background = ByggePilotenBlue (mørk blå #2196F3) – ikke lysere end velkomst.
// - Dark mode: ByggePilotenBlueDark for konsistens.
// - Anvendelse: I alle screens, brug Scaffold(containerColor = MaterialTheme.colorScheme.background) for blå look.
// - Fuldt: Edge-to-edge virker (transparent status/nav bar), luminance-check for ikoner.
// - Test: Byg → åbn enhver screen → blå baggrund overalt (ingen variation).

package dk.byggepiloten.firma.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat

private val LightColorScheme = lightColorScheme(
    primary = md_theme_light_primary,
    onPrimary = md_theme_light_onPrimary,
    primaryContainer = md_theme_light_primaryContainer,
    background = md_theme_light_background,  // OPDATERET: Nu ByggePilotenBlue (#2196F3) – ensartet blå
    surface = md_theme_light_surface
)

private val DarkColorScheme = darkColorScheme(
    primary = md_theme_dark_primary,
    onPrimary = md_theme_dark_onPrimary,
    primaryContainer = md_theme_dark_primaryContainer,
    background = md_theme_dark_background,  // OPDATERET: Nu ByggePilotenBlueDark – mørk blå
    surface = md_theme_dark_surface
)

@Composable
fun ByggePilotenTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalView.current.context
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            val decorView = window.decorView

            // 1. Gør statusbar og navigation bar helt transparente
            WindowCompat.setDecorFitsSystemWindows(window, false)
            window.statusBarColor = Color.Transparent.toArgb()
            window.navigationBarColor = Color.Transparent.toArgb()

            // 2. FJERNER DEN SORTE/GRÅ BAGGRUND BAG STATUSBAR
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                WindowCompat.getInsetsController(window, decorView).apply {
                    isAppearanceLightStatusBars = false
                    isAppearanceLightNavigationBars = false
                }
            }

            // 3. Automatiske ikoner (mørke på lys baggrund, lyse på mørk) – matcher ny blå
            val isLightBackground = colorScheme.background.luminance() > 0.5f
            WindowInsetsControllerCompat(window, decorView).apply {
                isAppearanceLightStatusBars = isLightBackground
                isAppearanceLightNavigationBars = isLightBackground
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}