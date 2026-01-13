// Fil: app/src/main/java/dk/byggepiloten/firma/ui/screen/new_task/categories/fliser/FliserPatternStep.kt
// FULD RETTET VERSION (ingen ændringer nødvendige – inkluderes for sikkerhed)
// - customPattern håndteres korrekt
// - Linjer: 112 (uændret)

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
fun FliserPatternStep(
    data: FliserData,
    onUpdate: (FliserData) -> Unit,
    netArea: Float
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Læggemønster", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(32.dp))
        Text("Nettoareal: ${"%.2f".format(netArea)} m²", color = Color.White, fontSize = 18.sp)

        Spacer(Modifier.height(40.dp))

        val patterns = listOf("Lige forskudt", "Diagonal", "Sildebensmønster", "Andet")
        patterns.forEach { pattern ->
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .background(
                        if (data.pattern == pattern) ByggePilotenBlue else Color.White,
                        RoundedCornerShape(16.dp)
                    )
                    .clickable {
                        onUpdate(data.copy(pattern = pattern, customPattern = if (pattern == "Andet") data.customPattern else null))
                    }
                    .padding(vertical = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    pattern,
                    color = if (data.pattern == pattern) Color.White else Color.Black,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(Modifier.height(16.dp))
        }

        if (data.pattern == "Andet") {
            Spacer(Modifier.height(16.dp))
            OutlinedTextField(
                value = data.customPattern ?: "",
                onValueChange = { onUpdate(data.copy(customPattern = it)) },
                label = { Text("Beskriv mønster") },
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