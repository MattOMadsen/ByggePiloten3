// Fil: app/src/main/java/dk/byggepiloten/firma/ui/screen/new_task/categories/badevaerelse/BadevaerelseFliserVaeggeStep.kt
// RETTET: Tilføjet import androidx.compose.ui.text.style.TextAlign.

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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign // NY import
import androidx.compose.ui.unit.dp
import dk.byggepiloten.firma.data.model.BadevaerelseData

@Composable
fun BadevaerelseFliserVaeggeStep(
    data: BadevaerelseData,
    onDataChange: (BadevaerelseData) -> Unit
) {
    var heightText by remember { mutableStateOf(data.wallTileHeightIfNotCeiling?.toString() ?: "") }

    LaunchedEffect(heightText) {
        heightText.toFloatOrNull()?.let {
            onDataChange(data.copy(wallTileHeightIfNotCeiling = it))
        }
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            "Fliser til vægge",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Spacer(Modifier.height(16.dp))
        Text(
            "Samme størrelse som gulv eller anden?",
            color = Color.White,
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(Modifier.height(32.dp))

        val sizes = listOf("Samme som gulv", "30x60 cm", "60x60 cm", "10x10 cm (mosaik)", "Andet")
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            sizes.forEach { size ->
                val selected = data.wallTileSize == size || (size == "Samme som gulv" && data.wallTileSize == data.floorTileSize)
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            val finalSize = if (size == "Samme som gulv") data.floorTileSize else size
                            onDataChange(data.copy(wallTileSize = finalSize))
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
        Text("Fliser helt til loft?", color = Color.White, style = MaterialTheme.typography.titleMedium)
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            listOf("Ja", "Nej").forEach { option ->
                val selected = if (option == "Ja") data.tilesToCeiling == true else data.tilesToCeiling == false
                Box(
                    modifier = Modifier
                        .clickable {
                            onDataChange(
                                data.copy(
                                    tilesToCeiling = option == "Ja",
                                    wallTileHeightIfNotCeiling = null
                                )
                            )
                            heightText = ""
                        }
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

        if (data.tilesToCeiling == false) {
            Spacer(Modifier.height(24.dp))
            OutlinedTextField(
                value = heightText,
                onValueChange = { heightText = it },
                label = { Text("Højde på fliser (m)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
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