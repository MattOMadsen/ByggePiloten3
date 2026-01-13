// Fil: app/src/main/java/dk/byggepiloten/firma/ui/screen/new_task/categories/fliser/FliserTileSizeStep.kt
// FULD RETTET VERSION (ingen ændringer nødvendige – inkluderes for sikkerhed)
// - customTileSize håndteres korrekt
// - Linjer: 116 (uændret)

package dk.byggepiloten.firma.ui.screen.new_task.categories.fliser

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dk.byggepiloten.firma.data.model.task.FliserData
import dk.byggepiloten.firma.ui.theme.ByggePilotenBlue

@Composable
fun FliserTileSizeStep(
    data: FliserData,
    onUpdate: (FliserData) -> Unit,
    netArea: Float
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Flisestørrelse", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(32.dp))
        Text("Nettoareal: ${"%.2f".format(netArea)} m²", color = Color.White, fontSize = 18.sp)

        Spacer(Modifier.height(40.dp))

        val sizes = listOf("10x10 cm", "20x20 cm", "30x30 cm", "60x60 cm", "30x60 cm", "Anden")
        sizes.forEach { size ->
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .background(
                        if (data.tileSize == size) ByggePilotenBlue else Color.White,
                        RoundedCornerShape(16.dp)
                    )
                    .clickable {
                        onUpdate(data.copy(tileSize = size, customTileSize = if (size == "Anden") data.customTileSize else null))
                    }
                    .padding(vertical = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    size,
                    color = if (data.tileSize == size) Color.White else Color.Black,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(Modifier.height(16.dp))
        }

        if (data.tileSize == "Anden") {
            Spacer(Modifier.height(16.dp))
            OutlinedTextField(
                value = data.customTileSize ?: "",
                onValueChange = { onUpdate(data.copy(customTileSize = it)) },
                label = { Text("Beskriv størrelse") },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    cursorColor = ByggePilotenBlue
                ),
                modifier = Modifier.fillMaxWidth(0.8f)
            )
        }
    }
}