// Fil: app/src/main/java/dk/byggepiloten/firma/ui/screen/new_task/categories/opmuring/OpmuringDimensionsStep.kt
// FULD RETTET – Rows kun ved individuelt, fuldt editable, "Tilføj væg"
// Linjer: 142

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
import dk.byggepiloten.firma.data.model.task.WallMeasurement
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

        ChoiceBoxRow(
            options = modeOptions,
            selectedOption = data.wallMode,
            onOptionSelected = {
                val cleared = if (it == "Samlet areal") emptyList() else data.wallMeasurements
                onDataChange(data.copy(wallMode = it, wallMeasurements = cleared, wallTotalAreaM2 = if (it == "Samlet areal") data.wallTotalAreaM2 else null))
            },
            modifier = Modifier.padding(bottom = 24.dp)
        )

        if (data.wallMode == "Samlet areal") {
            StyledTextField(
                value = data.wallTotalAreaM2?.toString() ?: "",
                onValueChange = { if (it.isEmpty() || it.toFloatOrNull() != null) onDataChange(data.copy(wallTotalAreaM2 = it.toFloatOrNull())) },
                label = "Samlet areal (m²)",
                keyboardType = KeyboardType.Decimal
            )
        } else if (data.wallMode == "Individuelle vægge") {
            var localMeasurements by remember { mutableStateOf(data.wallMeasurements.toMutableList()) }

            LaunchedEffect(localMeasurements) {
                onDataChange(data.copy(wallMeasurements = localMeasurements.toList()))
            }

            if (localMeasurements.isEmpty()) {
                localMeasurements = mutableListOf(WallMeasurement())
            }

            MeasurementRow(measurements = localMeasurements)
        }
    }
}