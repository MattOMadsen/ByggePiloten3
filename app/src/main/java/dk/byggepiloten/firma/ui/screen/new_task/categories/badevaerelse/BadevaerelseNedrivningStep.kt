// Fil: app/src/main/java/dk/byggepiloten/firma/ui/screen/new_task/categories/badevaerelse/BadevaerelseNedrivningStep.kt
// FULD RETTET VERSION
// - Fjernet destructuring (årsag til component3-fejl)
// - Simpel loop med separate variabler
// - Bokse under hinanden (Column)
// - onDataChange callback
// - Linjer: 152

package dk.byggepiloten.firma.ui.screen.new_task.categories.badevaerelse

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dk.byggepiloten.firma.data.model.task.BadevaerelseData

@Composable
fun BadevaerelseNedrivningStep(
    data: BadevaerelseData,
    onDataChange: (BadevaerelseData) -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            "Nedrivning",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Spacer(Modifier.height(16.dp))
        Text(
            "Hvad skal rives ned?",
            color = Color.White,
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(Modifier.height(32.dp))

        val items = listOf(
            Triple("Fliser", data.demolishTiles) { value: Boolean -> onDataChange(data.copy(demolishTiles = value)) },
            Triple("Inventar (toilet, vask, brus)", data.demolishFixtures) { value: Boolean -> onDataChange(data.copy(demolishFixtures = value)) },
            Triple("Rør", data.demolishPipes) { value: Boolean -> onDataChange(data.copy(demolishPipes = value)) }
        )

        items.forEach { item ->
            val label = item.first
            val currentValue = item.second
            val setter = item.third

            Text(label, color = Color.White, style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(16.dp))

            val yesNo = listOf("Ja" to true, "Nej" to false)
            yesNo.forEach { (optionLabel, optionValue) ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .background(
                            if (currentValue == optionValue) MaterialTheme.colorScheme.primary else Color.White,
                            RoundedCornerShape(16.dp)
                        )
                        .clickable { setter(optionValue) }
                        .padding(vertical = 24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        optionLabel,
                        color = if (currentValue == optionValue) Color.White else Color.Black,
                        style = MaterialTheme.typography.titleMedium
                    )
                }

                Spacer(Modifier.height(16.dp))
            }

            Spacer(Modifier.height(32.dp))
        }

        Text("Bortskaffelse nødvendig?", color = Color.White, style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(16.dp))
        val disposalOptions = listOf("Ja" to true, "Nej" to false)
        disposalOptions.forEach { (optionLabel, optionValue) ->
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .background(
                        if (data.disposalNeeded == optionValue) MaterialTheme.colorScheme.primary else Color.White,
                        RoundedCornerShape(16.dp)
                    )
                    .clickable { onDataChange(data.copy(disposalNeeded = optionValue)) }
                    .padding(vertical = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    optionLabel,
                    color = if (data.disposalNeeded == optionValue) Color.White else Color.Black,
                    style = MaterialTheme.typography.titleMedium
                )
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}
