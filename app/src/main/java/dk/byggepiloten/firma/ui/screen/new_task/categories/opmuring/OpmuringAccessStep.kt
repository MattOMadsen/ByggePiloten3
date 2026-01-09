// Fil: app/src/main/java/dk/byggepiloten/firma/ui/screen/OpmuringAccessStep.kt
// SEPARAT STEP 14: Adgang & stillads (altid sidste step)

package dk.byggepiloten.firma.ui.screen.new_task.categories.opmuring

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dk.byggepiloten.firma.ui.theme.ByggePilotenBlue

@Composable
fun OpmuringAccessStep(
    height: String,
    goodAccess: Boolean?,
    onGoodAccessChange: (Boolean?) -> Unit
) {
    Text("Adgang og stillads", color = Color.White, fontWeight = FontWeight.Medium, fontSize = 20.sp)
    Spacer(Modifier.height(24.dp))

    val avgHeight = height.toFloatOrNull() ?: 0f
    if (avgHeight > 3f) {
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.2f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                "Højde > 3 m – stillads sandsynligvis nødvendigt",
                color = Color.White,
                modifier = Modifier.padding(16.dp)
            )
        }
        Spacer(Modifier.height(16.dp))
    }

    Text("God adgang til opsætning af stillads?", color = Color.White, fontSize = 16.sp)
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        listOf("Ja", "Nej", "Uvidende").forEach { opt ->
            val value = when (opt) {
                "Ja" -> true
                "Nej" -> false
                else -> null
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onGoodAccessChange(value) }
                    .background(if (goodAccess == value) ByggePilotenBlue else Color.White, RoundedCornerShape(8.dp))
                    .padding(vertical = 16.dp)
            ) {
                Text(
                    opt,
                    color = if (goodAccess == value) Color.White else Color.Black,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }
    }
}