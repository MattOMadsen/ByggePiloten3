// Fil: app/src/main/java/dk/byggepiloten/firma/ui/screen/new_task/categories/opmuring/OpmuringSurfaceStep.kt
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
fun OpmuringSurfaceStep(
    data: WallData,
    onDataChange: (WallData) -> Unit
) {
    Text("Overfladebehandling (ny mur)", color = Color.White, fontWeight = FontWeight.Medium, fontSize = 20.sp)
    Spacer(Modifier.height(24.dp))

    val options = listOf(
        "Rå mur (ingen behandling)",
        "Pudset",
        "Malet",
        "Andet"
    )

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        options.forEach { option ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        if (option == "Andet") onDataChange(data.copy(surfaceFinish = option, customSurface = null))
                        else onDataChange(data.copy(surfaceFinish = option, customSurface = null))
                    }
                    .background(if (data.surfaceFinish == option) ByggePilotenBlue else Color.White, RoundedCornerShape(8.dp))
                    .padding(vertical = 20.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(option, color = if (data.surfaceFinish == option) Color.White else Color.Black, fontSize = 16.sp)
            }
        }
    }

    if (data.surfaceFinish == "Andet") {
        Spacer(Modifier.height(16.dp))
        OutlinedTextField(
            value = data.customSurface ?: "",
            onValueChange = { onDataChange(data.copy(customSurface = it)) },
            label = { Text("Beskriv overfladebehandling") },
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