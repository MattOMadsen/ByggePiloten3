// Fil: app/src/main/java/dk/byggepiloten/firma/ui/screen/new_task/categories/opmuring/OpmuringOpeningsStep.kt
// FIX: Fjernet LazyColumn (nested scroll → crash).
// Erstattet med Column + for-loop over openingMeasurements.
// Valg af mode beholdt med FlowRow (3 valg, wrap'er pænt).

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
import dk.byggepiloten.firma.data.model.task.OpeningMeasurement
import dk.byggepiloten.firma.data.model.task.WallData
import dk.byggepiloten.firma.ui.theme.ByggePilotenBlue

@Composable
fun OpmuringOpeningsStep(
    data: WallData,
    onDataChange: (WallData) -> Unit
) {
    Text("Åbninger i muren (døre/vinduer)", color = Color.White, fontWeight = FontWeight.Medium, fontSize = 20.sp)
    Spacer(Modifier.height(24.dp))

    val modes = listOf("Ingen åbninger" to null, "Samlet areal" to "samlet", "Individuelle mål" to "individuel")

    FlowRow(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
        modes.forEach { (text, mode) ->
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clickable {
                        if (mode == null) onDataChange(data.copy(openingMode = null, openingTotalAreaM2 = null, openingMeasurements = emptyList()))
                        else onDataChange(data.copy(openingMode = mode, openingMeasurements = if (mode == "samlet") emptyList() else data.openingMeasurements))
                    }
                    .background(if (data.openingMode == mode) ByggePilotenBlue else Color.White, RoundedCornerShape(8.dp))
                    .padding(vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(text, color = if (data.openingMode == mode) Color.White else Color.Black)
            }
        }
    }

    Spacer(Modifier.height(24.dp))

    if (data.openingMode == "samlet") {
        OutlinedTextField(
            value = data.openingTotalAreaM2?.toString() ?: "",
            onValueChange = { if (it.isEmpty() || it.toFloatOrNull() != null) onDataChange(data.copy(openingTotalAreaM2 = it.toFloatOrNull())) },
            label = { Text("Samlet areal af åbninger (m²)") },
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
    } else if (data.openingMode == "individuel") {
        Text("Antal åbninger: ${data.openingMeasurements.size}", color = Color.White, fontSize = 16.sp)
        Spacer(Modifier.height(16.dp))

        Column {
            data.openingMeasurements.forEachIndexed { index, measurement ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    OutlinedTextField(
                        value = measurement.widthCm?.toString() ?: "",
                        onValueChange = { new ->
                            if (new.isEmpty() || new.toFloatOrNull() != null) {
                                val updated = data.openingMeasurements.toMutableList().apply {
                                    this[index] = this[index].copy(widthCm = new.toFloatOrNull())
                                }
                                onDataChange(data.copy(openingMeasurements = updated))
                            }
                        },
                        label = { Text("Bredde (cm)") },
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
                        value = measurement.heightCm?.toString() ?: "",
                        onValueChange = { new ->
                            if (new.isEmpty() || new.toFloatOrNull() != null) {
                                val updated = data.openingMeasurements.toMutableList().apply {
                                    this[index] = this[index].copy(heightCm = new.toFloatOrNull())
                                }
                                onDataChange(data.copy(openingMeasurements = updated))
                            }
                        },
                        label = { Text("Højde (cm)") },
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
                        val updated = data.openingMeasurements.toMutableList().apply { removeAt(index) }
                        onDataChange(data.copy(openingMeasurements = updated))
                    }) {
                        Icon(Icons.Filled.Delete, tint = Color.White, contentDescription = "Slet")
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
            Button(
                onClick = { onDataChange(data.copy(openingMeasurements = data.openingMeasurements + OpeningMeasurement())) },
                colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = ByggePilotenBlue),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Icon(Icons.Filled.Add, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Tilføj åbning")
            }
        }
    }
}