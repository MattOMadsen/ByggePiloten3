// Fil: app/src/main/java/dk/byggepiloten/firma/ui/screen/new_task/categories/opmuring/OpmuringThicknessStep.kt
// FIX: FlowRow → Column (valg under hinanden – renere look).

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
fun OpmuringThicknessStep(
    data: WallData,
    onDataChange: (WallData) -> Unit
) {
    Text("Tykkelse på ny mur", color = Color.White, fontWeight = FontWeight.Medium, fontSize = 20.sp)
    Spacer(Modifier.height(24.dp))

    val options = listOf("108 mm (½ sten)", "228 mm (1 sten)", "348 mm (1½ sten)", "Andet")

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        options.forEach { option ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        if (option == "Andet") onDataChange(data.copy(thicknessOption = option, customThickness = null))
                        else onDataChange(data.copy(thicknessOption = option, customThickness = null))
                    }
                    .background(if (data.thicknessOption == option) ByggePilotenBlue else Color.White, RoundedCornerShape(8.dp))
                    .padding(vertical = 20.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(option, color = if (data.thicknessOption == option) Color.White else Color.Black, fontSize = 16.sp)
            }
        }
    }

    if (data.thicknessOption == "Andet") {
        Spacer(Modifier.height(16.dp))
        OutlinedTextField(
            value = data.customThickness?.toString() ?: "",
            onValueChange = { if (it.isEmpty() || it.toIntOrNull() != null) onDataChange(data.copy(customThickness = it.toIntOrNull())) },
            label = { Text("Tykkelse i mm") },
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