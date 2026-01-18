// Fil: app/src/main/java/dk/byggepiloten/firma/ui/screen/new_task/components/common/MeasurementRow.kt
// FULD RETTET – Tilføjet manglende import: kotlinx.coroutines.flow.snapshotFlow
// Dette løser "Unresolved reference 'snapshotFlow'"
// Ingen andre ændringer – "Tilføj væg"-knap beholdt med hvid baggrund/blå tekst for kontrast
// TotalArea og delete-logik uændret

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
import dk.byggepiloten.firma.data.model.task.WallMeasurement
import dk.byggepiloten.firma.ui.theme.ByggePilotenBlue
import androidx.compose.runtime.snapshotFlow // NY IMPORT – løser compile-fejlen
import kotlinx.coroutines.flow.collectLatest

@Composable
fun MeasurementRow(
    measurements: List<WallMeasurement>,
    onMeasurementsChange: (List<WallMeasurement>) -> Unit,
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
        localMeasurements.add(WallMeasurement())
    }

    val totalArea: Double by derivedStateOf {
        localMeasurements.fold(0.0) { acc, m ->
            acc + ((m.length ?: 0f) * (m.height ?: 0f)).toDouble()
        }
    }

    Column(modifier = modifier.fillMaxWidth()) {
        localMeasurements.forEachIndexed { index, measurement ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 12.dp)
            ) {
                StyledTextField(
                    value = measurement.length?.toString() ?: "",
                    onValueChange = { newValue ->
                        val cleaned = newValue.replace(',', '.')
                        val parsed = cleaned.toFloatOrNull()
                        if (parsed != null || cleaned.isEmpty()) {
                            localMeasurements[index] = measurement.copy(length = parsed)
                        }
                    },
                    label = "Længde (m)",
                    keyboardType = KeyboardType.Decimal,
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
                Spacer(Modifier.width(16.dp))
                StyledTextField(
                    value = measurement.height?.toString() ?: "",
                    onValueChange = { newValue ->
                        val cleaned = newValue.replace(',', '.')
                        val parsed = cleaned.toFloatOrNull()
                        if (parsed != null || cleaned.isEmpty()) {
                            localMeasurements[index] = measurement.copy(height = parsed)
                        }
                    },
                    label = "Højde (m)",
                    keyboardType = KeyboardType.Decimal,
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
                IconButton(
                    onClick = { if (localMeasurements.size > 1) localMeasurements.removeAt(index) },
                    enabled = localMeasurements.size > 1
                ) {
                    Icon(Icons.Filled.Delete, contentDescription = "Fjern væg", tint = Color.Red)
                }
            }
        }

        Text(
            text = "Samlet areal: ${"%.2f".format(totalArea)} m²",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 24.dp)
        )

        FilledTonalButton(
            onClick = { localMeasurements.add(WallMeasurement()) },
            colors = ButtonDefaults.filledTonalButtonColors(
                containerColor = Color.White,
                contentColor = ByggePilotenBlue
            ),
            modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 24.dp)
        ) {
            Icon(Icons.Filled.Add, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("Tilføj væg", fontWeight = FontWeight.Medium)
        }
    }
}