// Fil: app/src/main/java/dk/byggepiloten/firma/ui/screen/new_task/categories/opmuring/OpmuringDimensionsStep.kt
// FIX: Fjernet LazyColumn (årsag til crash: nested scrollable i verticalScroll Column).
// Erstattet med Column + for-loop over wallMeasurements (dynamisk add/remove).
// Valg af mode beholdt med FlowRow (wrap'er pænt), men resten i Column for bedre plads.
// Validering uændret.

package dk.byggepiloten.firma.ui.screen.new_task.categories.opmuring

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dk.byggepiloten.firma.data.model.task.WallData
import dk.byggepiloten.firma.data.model.task.WallMeasurement
import dk.byggepiloten.firma.ui.theme.ByggePilotenBlue

@Composable
fun OpmuringDimensionsStep(
    data: WallData,
    onDataChange: (WallData) -> Unit
) {
    Text("Dimensioner på væggene", color = Color.White, fontWeight = FontWeight.Medium, fontSize = 20.sp)
    Spacer(Modifier.height(24.dp))

    // Valg af mode – beholdt FlowRow (2 valg, wrap'er pænt)
    val modes = listOf("Samlet areal" to "samlet", "Individuelle mål" to "individuel")

    FlowRow(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
        modes.forEach { (text, mode) ->
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clickable {
                        onDataChange(
                            data.copy(
                                wallMode = mode,
                                wallMeasurements = if (mode == "samlet") emptyList() else data.wallMeasurements
                            )
                        )
                    }
                    .background(if (data.wallMode == mode) ByggePilotenBlue else Color.White, RoundedCornerShape(8.dp))
                    .padding(vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(text, color = if (data.wallMode == mode) Color.White else Color.Black)
            }
        }
    }

    Spacer(Modifier.height(24.dp))

    if (data.wallMode == "samlet") {
        OutlinedTextField(
            value = data.wallTotalAreaM2?.toString() ?: "",
            onValueChange = { if (it.isEmpty() || it.toFloatOrNull() != null) onDataChange(data.copy(wallTotalAreaM2 = it.toFloatOrNull())) },
            label = { Text("Samlet areal (m²)") },
            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                cursorColor = ByggePilotenBlue
            )
        )
    } else {
        Text("Antal vægge: ${data.wallMeasurements.size}", color = Color.White, fontSize = 16.sp)
        Spacer(Modifier.height(16.dp))

        Column {
            data.wallMeasurements.forEachIndexed { index, measurement ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    OutlinedTextField(
                        value = measurement.length?.toString() ?: "",
                        onValueChange = { new ->
                            if (new.isEmpty() || new.toFloatOrNull() != null) {
                                val updated = data.wallMeasurements.toMutableList().apply {
                                    this[index] = this[index].copy(length = new.toFloatOrNull())
                                }
                                onDataChange(data.copy(wallMeasurements = updated))
                            }
                        },
                        label = { Text("Længde (m)") },
                        modifier = Modifier.weight(1f),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black,
                            cursorColor = ByggePilotenBlue
                        )
                    )
                    Spacer(Modifier.width(8.dp))
                    OutlinedTextField(
                        value = measurement.height?.toString() ?: "",
                        onValueChange = { new ->
                            if (new.isEmpty() || new.toFloatOrNull() != null) {
                                val updated = data.wallMeasurements.toMutableList().apply {
                                    this[index] = this[index].copy(height = new.toFloatOrNull())
                                }
                                onDataChange(data.copy(wallMeasurements = updated))
                            }
                        },
                        label = { Text("Højde (m)") },
                        modifier = Modifier.weight(1f),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black,
                            cursorColor = ByggePilotenBlue
                        )
                    )
                    IconButton(onClick = {
                        val updated = data.wallMeasurements.toMutableList().apply { removeAt(index) }
                        onDataChange(data.copy(wallMeasurements = updated))
                    }) {
                        Icon(Icons.Filled.Delete, tint = Color.White, contentDescription = "Slet")
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
            Button(
                onClick = { onDataChange(data.copy(wallMeasurements = data.wallMeasurements + WallMeasurement())) },
                colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = ByggePilotenBlue),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Icon(Icons.Filled.Add, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Tilføj væg")
            }
        }
    }
}