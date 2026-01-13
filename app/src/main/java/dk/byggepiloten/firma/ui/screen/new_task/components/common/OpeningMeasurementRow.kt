// Fil: app/src/main/java/dk/byggepiloten/firma/ui/screen/new_task/components/common/OpeningMeasurementRow.kt
// FULD FIX – tilføjet import androidx.compose.ui.Alignment (for .align)
// Linjer: 160

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import dk.byggepiloten.firma.data.model.task.OpeningMeasurement
import dk.byggepiloten.firma.ui.theme.ByggePilotenBlue
import kotlinx.coroutines.flow.collectLatest

@Composable
fun OpeningMeasurementRow(
    measurements: List<OpeningMeasurement>,
    onMeasurementsChange: (List<OpeningMeasurement>) -> Unit,
    modifier: Modifier = Modifier
) {
    val localMeasurements = remember { measurements.toMutableStateList() }

    LaunchedEffect(measurements) {
        if (measurements != localMeasurements.toList()) {
            localMeasurements.clear()
            localMeasurements.addAll(measurements)
        }
    }

    LaunchedEffect(localMeasurements) {
        snapshotFlow { localMeasurements.toList() }
            .collectLatest { onMeasurementsChange(it) }
    }

    if (localMeasurements.isEmpty()) {
        localMeasurements.add(OpeningMeasurement())
    }

    val totalOpeningArea: Double by derivedStateOf {
        localMeasurements.fold(0.0) { acc, m ->
            acc + ((m.widthCm ?: 0f) * (m.heightCm ?: 0f) / 10000f).toDouble()
        }
    }

    Column(modifier = modifier.fillMaxWidth()) {
        localMeasurements.forEachIndexed { index, measurement ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                StyledTextField(
                    value = measurement.widthCm?.toString() ?: "",
                    onValueChange = { newValue ->
                        val cleaned = newValue.replace(',', '.')
                        val parsed = cleaned.toFloatOrNull()
                        if (parsed != null || cleaned.isEmpty()) {
                            localMeasurements[index] = measurement.copy(widthCm = parsed)
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
                        val cleaned = newValue.replace(',', '.')
                        val parsed = cleaned.toFloatOrNull()
                        if (parsed != null || cleaned.isEmpty()) {
                            localMeasurements[index] = measurement.copy(heightCm = parsed)
                        }
                    },
                    label = "Højde (cm)",
                    keyboardType = KeyboardType.Decimal,
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
                IconButton(
                    onClick = { if (localMeasurements.size > 1) localMeasurements.removeAt(index) },
                    enabled = localMeasurements.size > 1
                ) {
                    Icon(Icons.Filled.Delete, contentDescription = "Fjern", tint = Color.Red)
                }
            }
        }

        Text(
            text = "Samlet areal af åbninger: ${"%.2f".format(totalOpeningArea)} m²",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 16.dp)
        )

        Button(
            onClick = { localMeasurements.add(OpeningMeasurement()) },
            colors = ButtonDefaults.buttonColors(containerColor = ByggePilotenBlue),
            modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 16.dp)
        ) {
            Icon(Icons.Filled.Add, contentDescription = null, tint = Color.White)
            Spacer(Modifier.width(8.dp))
            Text("Tilføj åbning", color = Color.White)
        }
    }
}