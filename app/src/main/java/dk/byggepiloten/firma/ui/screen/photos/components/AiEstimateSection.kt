// Fil: app/src/main/java/dk/byggepiloten/firma/ui/screen/photos/components/AiEstimateSection.kt
// UÆNDRET FRA GITHUB – men bekræftet at den understøtter warningNote (valgfri)
// Total lines: 92 (med ny warningNote-parameter)

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
    warningNote: String? = null, // Orange advarsel ved fallback
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
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "Baseret på din opgave – endeligt tilbud kan variere",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    warningNote?.let {
                        Spacer(Modifier.height(12.dp))
                        Text(
                            text = it,
                            color = Color(0xFFFF8A65),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}