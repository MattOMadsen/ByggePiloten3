// Fil: app/src/main/java/dk/byggepiloten/firma/ui/screen/new_task/categories/opmuring/OpmuringNewOrRepairStep.kt
// FIX: FlowRow → Column (2 valg under hinanden – renere look).

package dk.byggepiloten.firma.ui.screen.new_task.categories.opmuring

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
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
fun OpmuringNewOrRepairStep(
    data: WallData,
    onDataChange: (WallData) -> Unit
) {
    Text("Ny mur eller reparation?", color = Color.White, fontWeight = FontWeight.Medium, fontSize = 20.sp)
    Spacer(Modifier.height(24.dp))

    val options = listOf("Ny mur" to false, "Reparation af eksisterende mur" to true)

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        options.forEach { (text, isRepairValue) ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onDataChange(data.copy(isRepair = isRepairValue)) }
                    .background(if (data.isRepair == isRepairValue) ByggePilotenBlue else Color.White, RoundedCornerShape(8.dp))
                    .padding(vertical = 20.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text,
                    color = if (data.isRepair == isRepairValue) Color.White else Color.Black,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    fontSize = 16.sp
                )
            }
        }
    }
}