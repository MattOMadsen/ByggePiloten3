// Fil: app/src/main/java/dk/byggepiloten/firma/ui/screen/new_task/categories/badevaerelse/BadevaerelseGulvvarmeStep.kt
package dk.byggepiloten.firma.ui.screen.new_task.categories.badevaerelse

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dk.byggepiloten.firma.data.model.BadevaerelseData

/**
 * Step 8: Gulvvarme.
 * Ja/nej + conditional valg af type (elektrisk eller vandbåren).
 */
@Composable
fun BadevaerelseGulvvarmeStep(
    data: BadevaerelseData,
    onDataChange: (BadevaerelseData) -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            "Gulvvarme?",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Spacer(Modifier.height(16.dp))
        Text(
            "Skal der installeres gulvvarme?",
            color = Color.White,
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(Modifier.height(32.dp))

        val yesNoOptions = listOf("Ja", "Nej")
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            yesNoOptions.forEach { option ->
                val selected = if (option == "Ja") data.hasFloorHeating == true else data.hasFloorHeating == false
                Box(
                    modifier = Modifier
                        .clickable {
                            onDataChange(
                                data.copy(
                                    hasFloorHeating = option == "Ja",
                                    floorHeatingType = null
                                )
                            )
                        }
                        .background(
                            color = if (selected) MaterialTheme.colorScheme.primary else Color.White,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        option,
                        color = if (selected) Color.White else Color.Black,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }

        if (data.hasFloorHeating == true) {
            Spacer(Modifier.height(32.dp))
            Text("Type gulvvarme", color = Color.White, style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(16.dp))
            val types = listOf("Elektrisk", "Vandbåren")
            FlowRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                types.forEach { type ->
                    val selected = data.floorHeatingType == type
                    Box(
                        modifier = Modifier
                            .clickable { onDataChange(data.copy(floorHeatingType = type)) }
                            .background(
                                color = if (selected) MaterialTheme.colorScheme.primary else Color.White,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .padding(16.dp)
                    ) {
                        Text(
                            type,
                            color = if (selected) Color.White else Color.Black,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            }
        }
    }
}