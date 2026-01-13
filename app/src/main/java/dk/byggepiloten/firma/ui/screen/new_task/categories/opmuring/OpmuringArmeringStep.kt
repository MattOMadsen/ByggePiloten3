// Fil: app/src/main/java/dk/byggepiloten/firma/ui/screen/new_task/categories/opmuring/OpmuringArmeringStep.kt
// FULD FIX – korrekt reinforcement + note om rå væg + ensartet Box-stil
// Linjer: 92

package dk.byggepiloten.firma.ui.screen.new_task.categories.opmuring

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import dk.byggepiloten.firma.data.model.task.WallData
import dk.byggepiloten.firma.ui.theme.ByggePilotenBlue

@Composable
fun OpmuringArmeringStep(
    data: WallData,
    onDataChange: (WallData) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Skal muren armeres?",
            style = MaterialTheme.typography.titleMedium,
            color = Color.White,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Text(
            text = "Armering anbefales kun ved pudset eller malet overflade for at undgå revner. Ved rå murværk er armering normalt ikke nødvendigt.",
            color = Color.Yellow,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        val options = listOf("Ja" to true, "Nej" to false)

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            options.forEach { (text, value) ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onDataChange(data.copy(reinforcement = value)) }
                        .background(if (data.reinforcement == value) ByggePilotenBlue else Color.White, RoundedCornerShape(8.dp))
                        .padding(vertical = 20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = text,
                        color = if (data.reinforcement == value) Color.White else Color.Black,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }
}