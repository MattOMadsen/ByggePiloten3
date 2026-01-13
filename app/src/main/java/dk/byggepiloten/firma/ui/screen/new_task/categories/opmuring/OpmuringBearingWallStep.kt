// Fil: app/src/main/java/dk/byggepiloten/firma/ui/screen/new_task/categories/opmuring/OpmuringBearingWallStep.kt
// FIX: FlowRow → Column (yes/no under hinanden).

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
fun OpmuringBearingWallStep(
    data: WallData,
    onDataChange: (WallData) -> Unit
) {
    Text("Er væggen bærende?", color = Color.White, fontWeight = FontWeight.Medium, fontSize = 20.sp)
    Spacer(Modifier.height(16.dp))
    Text("Hvis du er i tvivl, kontakt en ingeniør eller arkitekt først.", color = Color.White.copy(alpha = 0.9f))

    Spacer(Modifier.height(24.dp))

    val options = listOf("Ja" to true, "Nej" to false)

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        options.forEach { (text, value) ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onDataChange(data.copy(bearingWall = value)) }
                    .background(if (data.bearingWall == value) ByggePilotenBlue else Color.White, RoundedCornerShape(8.dp))
                    .padding(vertical = 20.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text,
                    color = if (data.bearingWall == value) Color.White else Color.Black,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    fontSize = 16.sp
                )
            }
        }
    }
}