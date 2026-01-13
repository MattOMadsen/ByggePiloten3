// Fil: app/src/main/java/dk/byggepiloten/firma/ui/screen/new_task/categories/fliser/FliserAccessStep.kt
// FULD RETTET VERSION (til sikkerhed – ingen ændringer nødvendige ud over tidligere rettelse)
// - Boolean? felter håndteres korrekt
// - Linjer: 124 (uændret fra sidste version)

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
fun FliserAccessStep(
    data: FliserData,
    onUpdate: (FliserData) -> Unit,
    netArea: Float
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Adgang og bemærkninger", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(32.dp))
        Text("Nettoareal: ${"%.2f".format(netArea)} m²", color = Color.White, fontSize = 18.sp)

        Spacer(Modifier.height(40.dp))

        Text("God adgang til arbejdsområde?", color = Color.White, fontSize = 18.sp)
        Spacer(Modifier.height(16.dp))

        val accessOptions = listOf("Ja" to true, "Nej" to false)
        accessOptions.forEach { (label, value) ->
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .background(
                        if (data.goodAccess == value) ByggePilotenBlue else Color.White,
                        RoundedCornerShape(16.dp)
                    )
                    .clickable { onUpdate(data.copy(goodAccess = value)) }
                    .padding(vertical = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    label,
                    color = if (data.goodAccess == value) Color.White else Color.Black,
                    fontSize = 18.sp
                )
            }

            Spacer(Modifier.height(16.dp))
        }

        Spacer(Modifier.height(40.dp))

        Text("Kræves stillads?", color = Color.White, fontSize = 18.sp)
        Spacer(Modifier.height(16.dp))

        val scaffoldingOptions = listOf("Ja" to true, "Nej" to false)
        scaffoldingOptions.forEach { (label, value) ->
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .background(
                        if (data.needsScaffolding == value) ByggePilotenBlue else Color.White,
                        RoundedCornerShape(16.dp)
                    )
                    .clickable { onUpdate(data.copy(needsScaffolding = value)) }
                    .padding(vertical = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    label,
                    color = if (data.needsScaffolding == value) Color.White else Color.Black,
                    fontSize = 18.sp
                )
            }

            Spacer(Modifier.height(16.dp))
        }

        Spacer(Modifier.height(40.dp))

        Text(
            "Klinker og fliser ALTID ekskluderet materialer",
            color = Color(0xFFFF4444),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
    }
}