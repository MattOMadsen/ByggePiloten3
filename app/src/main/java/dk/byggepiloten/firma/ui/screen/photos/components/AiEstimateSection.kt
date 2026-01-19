// Fil: app/src/main/java/dk/byggepiloten/firma/ui/screen/photos/components/AiEstimateSection.kt
// FIX: Fjernet gul "Ingen AI-estimat endnu..." tekst helt fra denne reusable component
// - Den tekst håndteres nu kun i SummaryStep (vises kun ved 0 billeder)
// - Section viser nu KUN progress eller pris-card – intet andet hvis estimate == null
// - Rød fejlmeddelelse ("Kunne ikke generere...") håndteres separat (sandsynligvis via error-state i viewModel eller wizard)
// - Gjort component renere og mere reusable på tværs af steps
// Total lines: 68

package dk.byggepiloten.firma.ui.screen.photos.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dk.byggepiloten.firma.ui.theme.ByggePilotenBlue
import java.text.NumberFormat
import java.util.Locale

@Composable
fun AiEstimateSection(
    isGeneratingEstimate: Boolean,
    aiPriceEstimate: Long?,
    modifier: Modifier = Modifier
) {
    val priceFormatter = NumberFormat.getNumberInstance(Locale.forLanguageTag("da-DK"))

    Column(modifier = modifier.fillMaxWidth()) {
        if (isGeneratingEstimate) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.95f))
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(color = ByggePilotenBlue)
                    Spacer(Modifier.height(16.dp))
                    Text(
                        text = "Genererer AI-estimat...",
                        style = MaterialTheme.typography.titleMedium,
                        color = ByggePilotenBlue
                    )
                }
            }
        } else if (aiPriceEstimate != null) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.95f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "${priceFormatter.format(aiPriceEstimate)} kr inkl. moms",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "Baseret på din opgave – endeligt tilbud kan variere",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
        // Ingen fallback-tekst her – håndteres i SummaryStep eller separat error-banner
    }
}