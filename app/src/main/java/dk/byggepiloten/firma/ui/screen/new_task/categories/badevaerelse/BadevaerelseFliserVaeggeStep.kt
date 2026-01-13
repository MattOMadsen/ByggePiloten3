// app/src/main/java/dk/byggepiloten/firma/ui/screen/new_task/categories/badevaerelse/BadevaerelseFliserVaeggeStep.kt
// RETTET: Fjernet header-kommentar før package (årsag til compile-fejl)

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
import androidx.compose.ui.unit.dp
import dk.byggepiloten.firma.data.model.task.BadevaerelseData

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
        sizes.forEach { size ->
            val finalSize = if (size == "Samme som gulv") data.floorTileSize else size
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .background(
                        if (data.wallTileSize == finalSize) MaterialTheme.colorScheme.primary else Color.White,
                        RoundedCornerShape(16.dp)
                    )
                    .clickable { onDataChange(data.copy(wallTileSize = finalSize)) }
                    .padding(vertical = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    size,
                    color = if (data.wallTileSize == finalSize) Color.White else Color.Black,
                    style = MaterialTheme.typography.titleMedium
                )
            }

            Spacer(Modifier.height(16.dp))
        }

        Spacer(Modifier.height(32.dp))
        Text("Fliser helt til loft?", color = Color.White, style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(16.dp))
        val ceilingOptions = listOf("Ja", "Nej")
        ceilingOptions.forEach { option ->
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .background(
                        if ((option == "Ja" && data.tilesToCeiling == true) || (option == "Nej" && data.tilesToCeiling == false)) MaterialTheme.colorScheme.primary else Color.White,
                        RoundedCornerShape(16.dp)
                    )
                    .clickable {
                        onDataChange(
                            data.copy(
                                tilesToCeiling = option == "Ja",
                                wallTileHeightIfNotCeiling = null
                            )
                        )
                        heightText = ""
                    }
                    .padding(vertical = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    option,
                    color = if ((option == "Ja" && data.tilesToCeiling == true) || (option == "Nej" && data.tilesToCeiling == false)) Color.White else Color.Black,
                    style = MaterialTheme.typography.titleMedium
                )
            }

            Spacer(Modifier.height(16.dp))
        }

        if (data.tilesToCeiling == false) {
            Spacer(Modifier.height(32.dp))
            Text("Højde på fliser (m)", color = Color.White, style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = heightText,
                onValueChange = { heightText = it },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
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
