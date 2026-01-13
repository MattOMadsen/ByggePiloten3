// Fil: app/src/main/java/dk/byggepiloten/firma/ui/screen/new_task/categories/fliser/FliserFloorDimensionsStep.kt
// FULD RETTET VERSION (tilføjet manglende imports)
// - Linjer: 112

package dk.byggepiloten.firma.ui.screen.new_task.categories.fliser

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.background
import androidx.compose.foundation.text.KeyboardOptions
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

@Composable
fun FliserFloorDimensionsStep(
    data: FliserData,
    onUpdate: (FliserData) -> Unit,
    floorArea: Float
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Gulv – dimensioner", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(32.dp))

        Text("Længde (meter)", color = Color.White, fontSize = 18.sp)
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = data.floorLength?.toString() ?: "",
            onValueChange = { onUpdate(data.copy(floorLength = it.toFloatOrNull())) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                cursorColor = ByggePilotenBlue
            ),
            modifier = Modifier.fillMaxWidth(0.8f)
        )

        Spacer(Modifier.height(24.dp))
        Text("Bredde (meter)", color = Color.White, fontSize = 18.sp)
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = data.floorWidth?.toString() ?: "",
            onValueChange = { onUpdate(data.copy(floorWidth = it.toFloatOrNull())) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                cursorColor = ByggePilotenBlue
            ),
            modifier = Modifier.fillMaxWidth(0.8f)
        )

        if (floorArea > 0f) {
            Spacer(Modifier.height(32.dp))
            Box(
                modifier = Modifier
                    .background(ByggePilotenBlue.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                    .padding(16.dp)
            ) {
                Text(
                    "Gulvareal: ${"%.2f".format(floorArea)} m²",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}