// Fil: app/src/main/java/dk/byggepiloten/firma/ui/screen/new_task/components/common/OpeningMeasurementRow.kt
// FULD RETTET – "Tilføj åbning" + fuldt editable TextFields
// Linjer: 106

package dk.byggepiloten.firma.ui.screen.new_task.components.common

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import dk.byggepiloten.firma.data.model.task.OpeningMeasurement
import dk.byggepiloten.firma.ui.theme.ByggePilotenBlue

@Composable
fun OpeningMeasurementRow(
    measurements: MutableList<OpeningMeasurement>,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        measurements.forEachIndexed { index, measurement ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                StyledTextField(
                    value = measurement.widthCm?.toString() ?: "",
                    onValueChange = { newValue ->
                        if (newValue.isEmpty() || newValue.toFloatOrNull() != null) {
                            measurements[index] = measurement.copy(widthCm = newValue.toFloatOrNull())
                        }
                    },
                    label = "Bredde (cm)",
                    keyboardType = KeyboardType.Decimal,
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
                Spacer(Modifier.width(12.dp))
                StyledTextField(
                    value = measurement.heightCm?.toString() ?: "",
                    onValueChange = { newValue ->
                        if (newValue.isEmpty() || newValue.toFloatOrNull() != null) {
                            measurements[index] = measurement.copy(heightCm = newValue.toFloatOrNull())
                        }
                    },
                    label = "Højde (cm)",
                    keyboardType = KeyboardType.Decimal,
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
                IconButton(
                    onClick = { if (measurements.size > 1) measurements.removeAt(index) },
                    enabled = measurements.size > 1
                ) {
                    Icon(Icons.Filled.Delete, contentDescription = "Fjern", tint = Color.Red)
                }
            }
        }

        Button(
            onClick = { measurements.add(OpeningMeasurement()) },
            colors = ButtonDefaults.buttonColors(containerColor = ByggePilotenBlue),
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Icon(Icons.Filled.Add, contentDescription = null, tint = Color.White)
            Spacer(Modifier.width(8.dp))
            Text("Tilføj åbning", color = Color.White)
        }
    }
}