// Fil: app/src/main/java/dk/byggepiloten/firma/ui/screen/new_task/categories/badevaerelse/BadevaerelseNedrivningStep.kt
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
import dk.byggepiloten.firma.data.model.BadevaerelseData

/**
 * Step 5: Nedrivning (kun ved "Fuldt nyt").
 * Multiple ja/nej valg for hvad der skal rives ned + bortskaffelse.
 */
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
            "Fliser" to data.demolishTiles,
            "Inventar (toilet, vask, brus)" to data.demolishFixtures,
            "Rør" to data.demolishPipes
        )

        items.forEach { (label, value) ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(label, color = Color.White, style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.weight(1f))
                val yesNo = listOf("Ja", "Nej")
                yesNo.forEach { option ->
                    val selected = if (option == "Ja") value == true else value == false
                    Box(
                        modifier = Modifier
                            .clickable {
                                when (label) {
                                    "Fliser" -> onDataChange(data.copy(demolishTiles = option == "Ja"))
                                    "Inventar (toilet, vask, brus)" -> onDataChange(data.copy(demolishFixtures = option == "Ja"))
                                    "Rør" -> onDataChange(data.copy(demolishPipes = option == "Ja"))
                                }
                            }
                            .background(
                                color = if (selected) MaterialTheme.colorScheme.primary else Color.White,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .padding(horizontal = 20.dp, vertical = 10.dp)
                    ) {
                        Text(
                            option,
                            color = if (selected) Color.White else Color.Black
                        )
                    }
                }
            }
            Spacer(Modifier.height(24.dp))
        }

        Text("Bortskaffelse nødvendig?", color = Color.White, style = MaterialTheme.typography.titleMedium)
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            val yesNo = listOf("Ja", "Nej")
            yesNo.forEach { option ->
                val selected = if (option == "Ja") data.disposalNeeded == true else data.disposalNeeded == false
                Box(
                    modifier = Modifier
                        .clickable { onDataChange(data.copy(disposalNeeded = option == "Ja")) }
                        .background(
                            color = if (selected) MaterialTheme.colorScheme.primary else Color.White,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(16.dp)
                ) {
                    Text(
                        option,
                        color = if (selected) Color.White else Color.Black,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }
    }
}