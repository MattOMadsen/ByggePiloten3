// Fil: app/src/main/java/dk/byggepiloten/firma/ui/screen/new_task/categories/fliser/FliserUnderlagStep.kt
// FULD RETTET VERSION
// - Fjernet destructuring (årsag til component3-fejl)
// - Simpel loop med separate variabler for label, currentValue og setter
// - Korrekt Boolean?-håndtering (true = Ja, false = Nej)
// - Tilføjet manglende imports (TextAlign osv.)
// - Linjer: 178

package dk.byggepiloten.firma.ui.screen.new_task.categories.fliser

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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dk.byggepiloten.firma.data.model.task.FliserData
import dk.byggepiloten.firma.ui.theme.ByggePilotenBlue

@Composable
fun FliserUnderlagStep(
    data: FliserData,
    onUpdate: (FliserData) -> Unit,
    netArea: Float,
    showFloorQuestions: Boolean
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Underlag", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(32.dp))
        Text("Nettoareal: ${"%.2f".format(netArea)} m²", color = Color.White, fontSize = 18.sp)

        Spacer(Modifier.height(40.dp))

        if (showFloorQuestions) {
            Text("Gulv – underlagstilstand", color = Color.White, fontSize = 20.sp, textAlign = TextAlign.Center)
            Spacer(Modifier.height(24.dp))

            // Liste over gulv-spørgsmål med label, current value og setter-funktion
            val floorQuestions = listOf(
                Triple("Eksisterende fliser på gulv?", data.hasOldTiles) { value: Boolean? -> onUpdate(data.copy(hasOldTiles = value)) },
                Triple("Revner i underlag?", data.hasCracks) { value: Boolean? -> onUpdate(data.copy(hasCracks = value)) },
                Triple("Fugt i underlag?", data.hasMoisture) { value: Boolean? -> onUpdate(data.copy(hasMoisture = value)) },
                Triple("Er gulvet i vater?", data.isFloorLevel) { value: Boolean? -> onUpdate(data.copy(isFloorLevel = value)) },
                Triple("Lunker eller nedbulinger?", data.hasDentsOrDepressions) { value: Boolean? -> onUpdate(data.copy(hasDentsOrDepressions = value)) }
            )

            floorQuestions.forEach { question ->
                val label = question.first
                val currentValue = question.second
                val setter = question.third

                Text(label, color = Color.White, fontSize = 18.sp)
                Spacer(Modifier.height(16.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(32.dp)) {
                    listOf("Ja" to true, "Nej" to false).forEach { (optionLabel, optionValue) ->
                        Box(
                            modifier = Modifier
                                .background(
                                    if (currentValue == optionValue) ByggePilotenBlue else Color.White,
                                    RoundedCornerShape(12.dp)
                                )
                                .clickable { setter(optionValue) }
                                .padding(horizontal = 24.dp, vertical = 16.dp)
                        ) {
                            Text(
                                optionLabel,
                                color = if (currentValue == optionValue) Color.White else Color.Black,
                                fontSize = 18.sp
                            )
                        }
                    }
                }

                Spacer(Modifier.height(40.dp))
            }
        }
    }
}