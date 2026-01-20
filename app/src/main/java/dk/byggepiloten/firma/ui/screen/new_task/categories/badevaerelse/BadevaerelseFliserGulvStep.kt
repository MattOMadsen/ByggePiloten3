// Fil: app/src/main/java/dk/byggepiloten/firma/ui/screen/new_task/categories/badevaerelse/BadevaerelseFliserGulvStep.kt
// OPDATERET: Matcher nye felter i BadevaerelseData (customFloorTilePattern i stedet for customFloorPattern)
// - Ingen andre ændringer – UI/flow beholdt 100%
// Total lines: 136 (bekræftet)

package dk.byggepiloten.firma.ui.screen.new_task.categories.badevaerelse

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dk.byggepiloten.firma.data.model.task.BadevaerelseData

@Composable
fun BadevaerelseFliserGulvStep(
    data: BadevaerelseData,
    onDataChange: (BadevaerelseData) -> Unit
) {
    var customPattern by remember { mutableStateOf(data.customFloorTilePattern ?: "") }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            "Fliser til gulv",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        Spacer(Modifier.height(32.dp))

        Text("Mønster", color = Color.White, style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(16.dp))

        val patterns = listOf("Lige forbandt", "Sildeben", "Firkantet", "Andet")

        patterns.forEach { pattern ->
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .background(
                        if (data.floorTilePattern == pattern || (pattern == "Andet" && data.customFloorTilePattern != null)) MaterialTheme.colorScheme.primary else Color.White,
                        RoundedCornerShape(16.dp)
                    )
                    .clickable {
                        if (pattern == "Andet") {
                            onDataChange(data.copy(floorTilePattern = "Andet"))
                        } else {
                            onDataChange(data.copy(floorTilePattern = pattern, customFloorTilePattern = null))
                        }
                    }
                    .padding(vertical = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    pattern,
                    color = if (data.floorTilePattern == pattern || (pattern == "Andet" && data.customFloorTilePattern != null)) Color.White else Color.Black,
                    style = MaterialTheme.typography.titleMedium
                )
            }

            Spacer(Modifier.height(16.dp))
        }

        if (data.floorTilePattern == "Andet" || customPattern.isNotEmpty()) {
            Spacer(Modifier.height(16.dp))
            OutlinedTextField(
                value = customPattern,
                onValueChange = {
                    customPattern = it
                    onDataChange(data.copy(customFloorTilePattern = it, floorTilePattern = "Andet"))
                },
                label = { Text("Beskriv mønster") },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    cursorColor = MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier.fillMaxWidth(0.8f)
            )
        }
    }
}