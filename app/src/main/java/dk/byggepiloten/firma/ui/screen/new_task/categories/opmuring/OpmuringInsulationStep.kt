// Fil: app/src/main/java/dk/byggepiloten/firma/ui/screen/new_task/categories/opmuring/OpmuringInsulationStep.kt
// FIX: FlowRow → Column (valg under hinanden).

package dk.byggepiloten.firma.ui.screen.new_task.categories.opmuring

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dk.byggepiloten.firma.data.model.task.WallData
import dk.byggepiloten.firma.ui.theme.ByggePilotenBlue

@Composable
fun OpmuringInsulationStep(
    data: WallData,
    onDataChange: (WallData) -> Unit
) {
    Text("Isolering (facademur)", color = Color.White, fontWeight = FontWeight.Medium, fontSize = 20.sp)
    Spacer(Modifier.height(16.dp))
    Text("Ønskes isolering i skalmuren?", color = Color.White.copy(alpha = 0.9f))

    Spacer(Modifier.height(24.dp))

    val options = listOf("Ja" to true, "Nej" to false)

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        options.forEach { (text, value) ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        onDataChange(
                            data.copy(
                                insulationWanted = value,
                                insulationThickness = if (value == false) null else data.insulationThickness
                            )
                        )
                    }
                    .background(if (data.insulationWanted == value) ByggePilotenBlue else Color.White, RoundedCornerShape(8.dp))
                    .padding(vertical = 20.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(text, color = if (data.insulationWanted == value) Color.White else Color.Black, fontSize = 16.sp)
            }
        }
    }

    if (data.insulationWanted == true) {
        Spacer(Modifier.height(16.dp))
        OutlinedTextField(
            value = data.insulationThickness?.toString() ?: "",
            onValueChange = { if (it.isEmpty() || it.toFloatOrNull() != null) onDataChange(data.copy(insulationThickness = it.toFloatOrNull())) },
            label = { Text("Isoleringstykkelse (cm)") },
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
    }
}