// Fil: app/src/main/java/dk/byggepiloten/firma/ui/screen/OpmuringBearingWallStep.kt
// SEPARAT STEP 3: Er væggen bærende?

package dk.byggepiloten.firma.ui.screen

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
fun OpmuringBearingWallStep(
    bearingWall: Boolean?,
    onBearingWallChange: (Boolean?) -> Unit
) {
    Text("Er væggen bærende?", color = Color.White, fontWeight = FontWeight.Medium, fontSize = 20.sp)
    Spacer(Modifier.height(24.dp))

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
                    .clickable { onBearingWallChange(value) }
                    .background(if (bearingWall == value) ByggePilotenBlue else Color.White, RoundedCornerShape(8.dp))
                    .padding(vertical = 16.dp)
            ) {
                Text(
                    opt,
                    color = if (bearingWall == value) Color.White else Color.Black,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }
    }
}