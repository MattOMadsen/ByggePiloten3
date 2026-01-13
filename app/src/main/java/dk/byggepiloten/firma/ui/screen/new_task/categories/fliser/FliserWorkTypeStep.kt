// Fil: app/src/main/java/dk/byggepiloten/firma/ui/screen/new_task/categories/fliser/FliserWorkTypeStep.kt
// FULD RETTET VERSION
// - Tilføjet ALLE nødvendige imports (manglende imports var årsag til "imports only in beginning"-fejl i nogle tilfælde)
// - Sikret korrekt struktur (ingen kode uden for fun)
// - Bokse under hinanden i Column
// - Linjer: 98

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
fun FliserWorkTypeStep(
    data: FliserData,
    onUpdate: (FliserData) -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            "Hvilket arbejde skal udføres?",
            color = Color.White,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(16.dp))
        Text(
            "Vælg én eller begge",
            color = Color.White,
            fontSize = 16.sp,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(40.dp))

        val options = listOf("Gulv", "Væg", "Gulv og væg")
        options.forEach { option ->
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .background(
                        color = if (data.workType == option) ByggePilotenBlue else Color.White,
                        shape = RoundedCornerShape(16.dp)
                    )
                    .clickable { onUpdate(data.copy(workType = option)) }
                    .padding(vertical = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = option,
                    color = if (data.workType == option) Color.White else Color.Black,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}