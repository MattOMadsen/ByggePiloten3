// Fil: app/src/main/java/dk/byggepiloten/firma/ui/screen/OpmuringArmeringStep.kt
// SEPARAT STEP 10: Armering/forstærkning (kun ved ny mur)

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
fun OpmuringArmeringStep(
    reinforcement: Boolean?,
    onReinforcementChange: (Boolean?) -> Unit
) {
    Text("Armering/forstærkning nødvendig?", color = Color.White, fontWeight = FontWeight.Medium, fontSize = 20.sp)
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
                    .clickable { onReinforcementChange(value) }
                    .background(if (reinforcement == value) ByggePilotenBlue else Color.White, RoundedCornerShape(8.dp))
                    .padding(vertical = 16.dp)
            ) {
                Text(
                    opt,
                    color = if (reinforcement == value) Color.White else Color.Black,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }
    }
}