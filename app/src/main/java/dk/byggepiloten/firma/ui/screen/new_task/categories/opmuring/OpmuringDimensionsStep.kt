// Fil: app/src/main/java/dk/byggepiloten/firma/ui/screen/new_task/categories/opmuring/OpmuringDimensionsStep.kt
// FULD OPdatering – LaunchedEffect sætter default "Samlet areal" hvis wallMode == null
// UI viser altid korrekt pre-select (via ?: "Samlet areal")
// Live opdatering beholdt – areal/målinger registeres øjeblikkeligt
// Linjer: 132 (ca. +20 pga. LaunchedEffect + kommentarer)

package dk.byggepiloten.firma.ui.screen.new_task.categories.opmuring

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import dk.byggepiloten.firma.data.model.task.WallData
import dk.byggepiloten.firma.ui.screen.new_task.components.common.ChoiceBoxRow
import dk.byggepiloten.firma.ui.screen.new_task.components.common.MeasurementRow
import dk.byggepiloten.firma.ui.screen.new_task.components.common.StyledTextField

@Composable
fun OpmuringDimensionsStep(
    data: WallData,
    onDataChange: (WallData) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Hvad er dimensionerne på muren?",
            style = MaterialTheme.typography.titleMedium,
            color = Color.White,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        val modeOptions = listOf("Samlet areal", "Individuelle vægge")

        // Pre-select "Samlet areal" i UI selv hvis wallMode == null
        ChoiceBoxRow(
            options = modeOptions,
            selectedOption = data.wallMode ?: "Samlet areal",
            onOptionSelected = {
                val clearedMeasurements = if (it == "Samlet areal") emptyList() else data.wallMeasurements
                onDataChange(
                    data.copy(
                        wallMode = it,
                        wallMeasurements = clearedMeasurements,
                        wallTotalAreaM2 = if (it == "Samlet areal") data.wallTotalAreaM2 else null
                    )
                )
            },
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Auto-set default mode hvis wallMode == null (f.eks. ny task)
        // Kører kun én gang ved første load af steppet
        LaunchedEffect(Unit) {
            if (data.wallMode == null) {
                onDataChange(data.copy(wallMode = "Samlet areal"))
            }
        }

        if (data.wallMode == "Samlet areal") {
            StyledTextField(
                value = data.wallTotalAreaM2?.toString() ?: "",
                onValueChange = { newValue ->
                    val cleaned = newValue.replace(',', '.')
                    onDataChange(data.copy(wallTotalAreaM2 = cleaned.toFloatOrNull()))
                },
                label = "Samlet areal (m²)",
                keyboardType = KeyboardType.Decimal,
                singleLine = true
            )
        } else if (data.wallMode == "Individuelle vægge") {
            MeasurementRow(
                measurements = data.wallMeasurements,
                onMeasurementsChange = { onDataChange(data.copy(wallMeasurements = it)) }
            )
        }
    }
}
