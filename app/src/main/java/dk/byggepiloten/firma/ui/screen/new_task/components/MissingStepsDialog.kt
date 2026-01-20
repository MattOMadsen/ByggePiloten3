// Fil: app/src/main/java/dk/byggepiloten/firma/ui/screen/new_task/components/MissingStepsDialog.kt
// NY FIL: Reusable dialog til manglende steps (kan genbruges i andre wizards)
// - Viser liste af manglende step-numre + knap til at hoppe direkte
// - onGoToStep(stepIndex: Int) callback
// - onDismiss callback
// Total linjer: 92

package dk.byggepiloten.firma.ui.screen.new_task.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun MissingStepsDialog(
    missingSteps: List<Int>, // 1-baseret step-numre
    onGoToStep: (Int) -> Unit, // 0-baseret index
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Manglende felter",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Du mangler at udfylde følgende trin før du kan sende opgaven:")
                missingSteps.forEach { stepNum ->
                    Button(
                        onClick = { onGoToStep(stepNum - 1) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White)
                    ) {
                        Text("Gå til trin $stepNum")
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Annuller")
            }
        }
    )
}