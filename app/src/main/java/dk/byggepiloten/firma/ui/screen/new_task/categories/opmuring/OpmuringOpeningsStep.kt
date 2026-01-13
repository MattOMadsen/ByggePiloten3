// Fil: app/src/main/java/dk/byggepiloten/firma/ui/screen/new_task/categories/opmuring/OpmuringOpeningsStep.kt
// FULD FIX – viser live samlet areal også i "Samlet areal"-mode
// Linjer: 172

package dk.byggepiloten.firma.ui.screen.new_task.categories.opmuring

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import dk.byggepiloten.firma.data.model.task.WallData
import dk.byggepiloten.firma.ui.screen.new_task.components.common.ChoiceBoxRow
import dk.byggepiloten.firma.ui.screen.new_task.components.common.OpeningMeasurementRow
import dk.byggepiloten.firma.ui.screen.new_task.components.common.StyledTextField

@Composable
fun OpmuringOpeningsStep(
    data: WallData,
    onDataChange: (WallData) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Åbninger i muren (døre/vinduer)",
            style = MaterialTheme.typography.titleMedium,
            color = Color.White,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        val modeOptions = listOf("Ingen åbninger", "Samlet areal", "Individuelle mål")

        ChoiceBoxRow(
            options = modeOptions,
            selectedOption = when {
                data.openingMode == null -> "Ingen åbninger"
                data.openingMode == "samlet" -> "Samlet areal"
                data.openingMode == "individuel" -> "Individuelle mål"
                else -> "Ingen åbninger"
            },
            onOptionSelected = { selected ->
                when (selected) {
                    "Ingen åbninger" -> onDataChange(data.copy(openingMode = null, openingTotalAreaM2 = null, openingMeasurements = emptyList()))
                    "Samlet areal" -> onDataChange(data.copy(openingMode = "samlet", openingMeasurements = emptyList()))
                    "Individuelle mål" -> onDataChange(data.copy(openingMode = "individuel"))
                }
            },
            modifier = Modifier.padding(bottom = 24.dp)
        )

        if (data.openingMode == "samlet") {
            StyledTextField(
                value = data.openingTotalAreaM2?.toString() ?: "",
                onValueChange = { newValue ->
                    val cleaned = newValue.replace(',', '.')
                    onDataChange(data.copy(openingTotalAreaM2 = cleaned.toFloatOrNull()))
                },
                label = "Samlet areal af åbninger (m²)",
                keyboardType = KeyboardType.Decimal,
                singleLine = true
            )

            // Live total i samlet mode
            val displayedTotal = data.openingTotalAreaM2?.let { "%.2f".format(it) } ?: "0.00"
            Text(
                text = "Samlet areal af åbninger: $displayedTotal m²",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 16.dp)
            )
        } else if (data.openingMode == "individuel") {
            OpeningMeasurementRow(
                measurements = data.openingMeasurements,
                onMeasurementsChange = { onDataChange(data.copy(openingMeasurements = it)) }
            )
        }

        // Advarsel (beholdt)
        val wallArea = if (data.wallMode == "Samlet areal") (data.wallTotalAreaM2 ?: 0f) else {
            data.wallMeasurements.fold(0f) { acc, m -> acc + (m.length ?: 0f) * (m.height ?: 0f) }
        }
        val openingArea = if (data.openingMode == "samlet") (data.openingTotalAreaM2 ?: 0f) else {
            data.openingMeasurements.fold(0f) { acc, m -> acc + (m.widthCm ?: 0f) * (m.heightCm ?: 0f) / 10000f }
        }

        if (openingArea > wallArea + 0.01f) {
            Text(
                text = "Advarsel: Åbninger er større end murareal – tjek målene!",
                color = Color.Red,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 16.dp).align(Alignment.CenterHorizontally)
            )
        }
    }
}