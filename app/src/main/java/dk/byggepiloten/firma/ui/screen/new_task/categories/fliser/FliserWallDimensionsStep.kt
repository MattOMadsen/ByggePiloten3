// Fil: app/src/main/java/dk/byggepiloten/firma/ui/screen/new_task/categories/fliser/FliserWallDimensionsStep.kt
// OPDATERET: Vertikalt layout som Opmuring
// - Labels over felterne (ingen intern label i TextField)
// - Mulighed for at bruge gulv-perimeter automatisk (hvis Gulv valgt)
// - Live vægareal-visning
// - onUpdate callback
// - Linjer: 142

package dk.byggepiloten.firma.ui.screen.new_task.categories.fliser

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.background
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dk.byggepiloten.firma.data.model.task.FliserData
import dk.byggepiloten.firma.ui.theme.ByggePilotenBlue
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.clickable

@Composable
fun FliserWallDimensionsStep(
    data: FliserData,
    onUpdate: (FliserData) -> Unit,
    wallArea: Float,
    floorPerimeterAvailable: Boolean
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Væg – dimensioner", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(32.dp))

        if (floorPerimeterAvailable) {
            Text("Brug gulvets omkreds til vægge?", color = Color.White, fontSize = 18.sp)
            Spacer(Modifier.height(16.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(32.dp)) {
                listOf(true to "Ja", false to "Nej").forEach { (value, label) ->
                    Box(
                        modifier = Modifier
                            .background(
                                if (data.useFloorPerimeterForWalls == value) ByggePilotenBlue else Color.White,
                                RoundedCornerShape(12.dp)
                            )
                            .clickable { onUpdate(data.copy(useFloorPerimeterForWalls = value)) }
                            .padding(horizontal = 24.dp, vertical = 16.dp)
                    ) {
                        Text(
                            label,
                            color = if (data.useFloorPerimeterForWalls == value) Color.White else Color.Black,
                            fontSize = 18.sp
                        )
                    }
                }
            }

            Spacer(Modifier.height(32.dp))
        }

        if (data.useFloorPerimeterForWalls == false || !floorPerimeterAvailable) {
            Text("Manuel væg-omkreds (meter)", color = Color.White, fontSize = 18.sp)
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = data.manualWallPerimeter?.toString() ?: "",
                onValueChange = { onUpdate(data.copy(manualWallPerimeter = it.toFloatOrNull())) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    cursorColor = ByggePilotenBlue
                ),
                modifier = Modifier.fillMaxWidth(0.8f)
            )

            Spacer(Modifier.height(24.dp))
        }

        Text("Væghøjde (meter)", color = Color.White, fontSize = 18.sp)
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = data.wallHeight?.toString() ?: "",
            onValueChange = { onUpdate(data.copy(wallHeight = it.toFloatOrNull())) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                cursorColor = ByggePilotenBlue
            ),
            modifier = Modifier.fillMaxWidth(0.8f)
        )

        if (wallArea > 0f) {
            Spacer(Modifier.height(32.dp))
            Box(
                modifier = Modifier
                    .background(ByggePilotenBlue.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                    .padding(16.dp)
            ) {
                Text(
                    "Vægareal: ${"%.2f".format(wallArea)} m²",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}