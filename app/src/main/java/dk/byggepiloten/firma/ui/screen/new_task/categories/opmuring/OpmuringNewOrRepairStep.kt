// Fil: app/src/main/java/dk/byggepiloten/firma/ui/screen/OpmuringNewOrRepairStep.kt
// SEPARAT STEP 2: Ny mur eller reparation?

package dk.byggepiloten.firma.ui.screen.new_task.categories.opmuring

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dk.byggepiloten.firma.ui.theme.ByggePilotenBlue

@Composable
fun OpmuringNewOrRepairStep(
    isRepair: Boolean?,
    onIsRepairChange: (Boolean) -> Unit
) {
    Text("Ny mur eller reparation?", color = Color.White, fontWeight = FontWeight.Medium, fontSize = 20.sp)
    Spacer(Modifier.height(24.dp))

    val options = listOf("Ny mur", "Reparation af eksisterende mur")
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        options.forEach { option ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onIsRepairChange(option == "Reparation af eksisterende mur") }
                    .background(if (isRepair == (option == "Reparation af eksisterende mur")) ByggePilotenBlue else Color.White, RoundedCornerShape(8.dp))
                    .padding(vertical = 16.dp)
            ) {
                Text(
                    option,
                    color = if (isRepair == (option == "Reparation af eksisterende mur")) Color.White else Color.Black,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }
    }
}