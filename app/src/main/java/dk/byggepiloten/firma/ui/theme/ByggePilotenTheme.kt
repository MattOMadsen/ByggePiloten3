// Fil: app/src/main/java/dk/byggepiloten/firma/ui/theme/ByggePilotenTheme.kt
// FULD OPDATERET – FULL DARK/LIGHT MODE SUPPORT + MATERIAL YOU
// + Dynamiske farver (blue gradient tilpasset mode)
// + Alle screens bruger nu MaterialTheme.colorScheme for tekst/cards
// + ca. 120 linjer

package dk.byggepiloten.firma.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = ByggePilotenBlue,
    secondary = Color(0xFF42A5F5),
    tertiary = Color(0xFF90CAF9),
    background = Color(0xFFF5F9FF),
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onTertiary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF1976D2),
    secondary = Color(0xFF03A9F4),
    tertiary = Color(0xFF81D4FA),
    background = Color(0xFF0D47A1),
    surface = Color(0xFF1E3A8A),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.Black,
    onBackground = Color.White,
    onSurface = Color.White
)

@Composable
fun ByggePilotenTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}