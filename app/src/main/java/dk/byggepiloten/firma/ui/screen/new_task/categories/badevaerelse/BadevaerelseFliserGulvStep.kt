// Fil: app/src/main/java/dk/byggepiloten/firma/ui/screen/new_task/categories/badevaerelse/BadevaerelseFliserGulvStep.kt
// RETTET: Tilføjet import androidx.compose.ui.text.style.TextAlign (løser Unresolved reference).

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
import androidx.compose.ui.text.style.TextAlign // NY import
import androidx.compose.ui.unit.dp
import dk.byggepiloten.firma.data.model.BadevaerelseData

@Composable
fun BadevaerelseFliserGulvStep(
    data: BadevaerelseData,
    onDataChange: (BadevaerelseData) -> Unit
) {
    var customPattern by remember { mutableStateOf(data.customFloorPattern ?: "") }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            "Fliser til gulv",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Spacer(Modifier.height(16.dp))
        Text(
            "Vælg flisestørrelse og mønster",
            color = Color.White,
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(Modifier.height(32.dp))

        Text("Flisestørrelse", color = Color.White, style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(16.dp))
        val sizes = listOf("30x60 cm", "60x60 cm", "80x80 cm", "10x10 cm (mosaik)", "Andet")
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            sizes.forEach { size ->
                val selected = data.floorTileSize == size
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            onDataChange(data.copy(floorTileSize = size))
                        }
                        .background(
                            color = if (selected) MaterialTheme.colorScheme.primary else Color.White,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        size,
                        color = if (selected) Color.White else Color.Black,
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        Spacer(Modifier.height(32.dp))
        Text("Mønster", color = Color.White, style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(16.dp))
        val patterns = listOf("Lige forbandt", "Sildeben", "Firkantet forbandt", "Andet")
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            patterns.forEach { pattern ->
                val selected = data.floorTilePattern == pattern || (pattern == "Andet" && data.customFloorPattern != null)
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            if (pattern == "Andet") {
                                // custom håndteres separat
                            } else {
                                onDataChange(data.copy(floorTilePattern = pattern, customFloorPattern = null))
                                customPattern = ""
                            }
                        }
                        .background(
                            color = if (selected) MaterialTheme.colorScheme.primary else Color.White,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        pattern,
                        color = if (selected) Color.White else Color.Black,
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        if (data.floorTilePattern == "Andet" || customPattern.isNotEmpty()) {
            Spacer(Modifier.height(16.dp))
            OutlinedTextField(
                value = customPattern,
                onValueChange = {
                    customPattern = it
                    onDataChange(data.copy(customFloorPattern = it, floorTilePattern = "Andet"))
                },
                label = { Text("Beskriv mønster") },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedLabelColor = Color.White,
                    unfocusedLabelColor = Color.White,
                    cursorColor = Color.White,
                    focusedBorderColor = Color.White,
                    unfocusedBorderColor = Color.White.copy(alpha = 0.5f)
                ),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}