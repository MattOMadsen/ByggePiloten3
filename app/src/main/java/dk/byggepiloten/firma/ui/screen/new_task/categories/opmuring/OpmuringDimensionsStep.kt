// Fil: app/src/main/java/dk/byggepiloten/firma/ui/screen/new_task/categories/opmuring/OpmuringDimensionsStep.kt
// OPDATERET: Forbedret synlighed af netto-areal teaser-boks
// - Tekstfarve på netto areal → hvid + bold
// - Baggrunds-alpha fra 0.2f → 0.35f for bedre kontrast
// - Lidt mere padding og større font-weight på hovedlinjen
// - Ingen andre ændringer i layout eller farver i appen

package dk.byggepiloten.firma.ui.screen.new_task.categories.opmuring

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dk.byggepiloten.firma.ui.screen.new_task.components.common.ChoiceBoxRow
import dk.byggepiloten.firma.ui.screen.new_task.components.common.MeasurementRow
import dk.byggepiloten.firma.ui.screen.new_task.components.common.StyledTextField
import dk.byggepiloten.firma.ui.theme.ByggePilotenBlue
import dk.byggepiloten.firma.ui.viewmodel.task.OpmuringTaskViewModel

/**
 * Composable for dimensionstrinnet i opmuring-wizard.
 * Konsistent viewModel-stil.
 * Valg mellem "Samlet areal" og "Individuelle vægge".
 * Live teaser viser netto areal (væg - åbninger).
 */
@Composable
fun OpmuringDimensionsStep(
    viewModel: OpmuringTaskViewModel
) {
    val data by viewModel.wallData.collectAsStateWithLifecycle()

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Hvad er dimensionerne på muren?",
            style = MaterialTheme.typography.titleMedium,
            color = Color.White,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        val modeOptions = listOf("Samlet areal", "Individuelle vægge")

        ChoiceBoxRow(
            options = modeOptions,
            selectedOption = data.wallMode ?: "Samlet areal",
            onOptionSelected = { selected ->
                val clearedMeasurements = if (selected == "Samlet areal") emptyList() else data.wallMeasurements
                viewModel.updateWallData(
                    data.copy(
                        wallMode = selected,
                        wallMeasurements = clearedMeasurements,
                        wallTotalAreaM2 = if (selected == "Samlet areal") data.wallTotalAreaM2 else null
                    )
                )
            },
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Default til "Samlet areal" hvis wallMode == null (første load)
        LaunchedEffect(Unit) {
            if (data.wallMode == null) {
                viewModel.updateWallData(data.copy(wallMode = "Samlet areal"))
            }
        }

        if (data.wallMode == "Samlet areal") {
            StyledTextField(
                value = data.wallTotalAreaM2?.toString() ?: "",
                onValueChange = { newValue ->
                    val cleaned = newValue.replace(',', '.')
                    viewModel.updateWallData(data.copy(wallTotalAreaM2 = cleaned.toFloatOrNull()))
                },
                label = "Samlet areal (m²)",
                keyboardType = KeyboardType.Decimal,
                singleLine = true
            )
        } else if (data.wallMode == "Individuelle vægge") {
            MeasurementRow(
                measurements = data.wallMeasurements,
                onMeasurementsChange = { viewModel.updateWallData(data.copy(wallMeasurements = it)) }
            )
        }

        // Live netto-areal teaser (væg - åbninger)
        val wallArea = remember(data.wallMeasurements, data.wallTotalAreaM2) {
            if (data.wallMode == "Samlet areal") {
                data.wallTotalAreaM2 ?: 0f
            } else {
                data.wallMeasurements.sumOf { ((it.length ?: 0f) * (it.height ?: 0f)).toDouble() }.toFloat()
            }
        }

        val openingArea = remember(data.openingMeasurements) {
            data.openingMeasurements.sumOf { ((it.widthCm ?: 0f) / 100f * (it.heightCm ?: 0f) / 100f).toDouble() }.toFloat()
        }

        val nettoArea = (wallArea - openingArea).coerceAtLeast(0f)

        if (wallArea > 0f) {
            Spacer(Modifier.height(32.dp))

            Text(
                text = "Samlet vægareal: ${String.format("%.2f", wallArea)} m²",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = Color.White
            )

            if (openingArea > 0f) {
                Text(
                    text = "Fratrukket åbninger: ${String.format("%.2f", openingArea)} m²",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    color = Color.White,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            // Forbedret synlighed af netto areal boks
            Text(
                text = "Netto areal til opmuring: ${String.format("%.2f", nettoArea)} m²",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Black,           // ← gjort ekstra fed
                color = Color.White,                     // ← ændret fra blå til hvid
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp)                // lidt mere luft ovenfra
                    .background(
                        Color.White.copy(alpha = 0.35f), // ← stærkere baggrund (fra 0.2f)
                        MaterialTheme.shapes.medium
                    )
                    .padding(horizontal = 20.dp, vertical = 16.dp)  // lidt mere padding indeni
            )
        }
    }
}