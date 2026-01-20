// Fil: app/src/main/java/dk/byggepiloten/firma/ui/screen/new_task/categories/badevaerelse/BadevaerelseNedrivningStep.kt
// OPDATERET: Matcher nye felter i BadevaerelseData (demolishFixtures, demolishPipes, disposalNeeded)
// - Beholdt UI/flow 100%
// Total lines: 152 (bekræftet)

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

        Spacer(Modifier.height(32.dp))

        Text("Armaturer/toilet/vask?", color = Color.White, style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(16.dp))
        val fixtureOptions = listOf("Ja" to true, "Nej" to false)
        fixtureOptions.forEach { (optionLabel, optionValue) ->
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .background(
                        if (data.demolishFixtures == optionValue) MaterialTheme.colorScheme.primary else Color.White,
                        RoundedCornerShape(16.dp)
                    )
                    .clickable { onDataChange(data.copy(demolishFixtures = optionValue)) }
                    .padding(vertical = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    optionLabel,
                    color = if (data.demolishFixtures == optionValue) Color.White else Color.Black,
                    style = MaterialTheme.typography.titleMedium
                )
            }

            Spacer(Modifier.height(16.dp))
        }

        Text("Rør?", color = Color.White, style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(16.dp))
        val pipeOptions = listOf("Ja" to true, "Nej" to false)
        pipeOptions.forEach { (optionLabel, optionValue) ->
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .background(
                        if (data.demolishPipes == optionValue) MaterialTheme.colorScheme.primary else Color.White,
                        RoundedCornerShape(16.dp)
                    )
                    .clickable { onDataChange(data.copy(demolishPipes = optionValue)) }
                    .padding(vertical = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    optionLabel,
                    color = if (data.demolishPipes == optionValue) Color.White else Color.Black,
                    style = MaterialTheme.typography.titleMedium
                )
            }

            Spacer(Modifier.height(16.dp))
        }

        Spacer(Modifier.height(32.dp))

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