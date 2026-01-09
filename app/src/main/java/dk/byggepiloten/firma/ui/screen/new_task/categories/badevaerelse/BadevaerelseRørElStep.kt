// Fil: app/src/main/java/dk/byggepiloten/firma/ui/screen/new_task/categories/badevaerelse/BadevaerelseRørElStep.kt
// RETTET: Ny OutlinedTextFieldDefaults.colors() signatur på begge beskrivelsesfelter.

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
 * Step 11: Rør- og el-flytting.
 * Ja/nej for hver + conditional beskrivelsesfelt.
 */
@Composable
fun BadevaerelseRørElStep(
    data: BadevaerelseData,
    onDataChange: (BadevaerelseData) -> Unit
) {
    var pipeDesc by remember { mutableStateOf(data.pipeDescription ?: "") }
    var electricalDesc by remember { mutableStateOf(data.electricalDescription ?: "") }

    LaunchedEffect(pipeDesc, electricalDesc) {
        onDataChange(data.copy(pipeDescription = pipeDesc.ifBlank { null }, electricalDescription = electricalDesc.ifBlank { null }))
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            "Rør- og el-flytting",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Spacer(Modifier.height(16.dp))
        Text(
            "Skal rør eller el flyttes?",
            color = Color.White,
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(Modifier.height(32.dp))

        // Rør
        Text("Flytte rør?", color = Color.White, style = MaterialTheme.typography.titleMedium)
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            listOf("Ja", "Nej").forEach { option ->
                val selected = if (option == "Ja") data.relocatePipes == true else data.relocatePipes == false
                Box(
                    modifier = Modifier
                        .clickable {
                            onDataChange(data.copy(relocatePipes = option == "Ja", pipeDescription = null))
                            pipeDesc = ""
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

        if (data.relocatePipes == true) {
            Spacer(Modifier.height(16.dp))
            OutlinedTextField(
                value = pipeDesc,
                onValueChange = { pipeDesc = it },
                label = { Text("Beskriv rørflytning") },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedLabelColor = Color.White,
                    unfocusedLabelColor = Color.White,
                    cursorColor = Color.White,
                    focusedBorderColor = Color.White,
                    unfocusedBorderColor = Color.White.copy(alpha = 0.5f)
                ),
                minLines = 3
            )
        }

        Spacer(Modifier.height(32.dp))

        // El
        Text("Flytte el-installationer?", color = Color.White, style = MaterialTheme.typography.titleMedium)
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            listOf("Ja", "Nej").forEach { option ->
                val selected = if (option == "Ja") data.relocateElectrical == true else data.relocateElectrical == false
                Box(
                    modifier = Modifier
                        .clickable {
                            onDataChange(data.copy(relocateElectrical = option == "Ja", electricalDescription = null))
                            electricalDesc = ""
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

        if (data.relocateElectrical == true) {
            Spacer(Modifier.height(16.dp))
            OutlinedTextField(
                value = electricalDesc,
                onValueChange = { electricalDesc = it },
                label = { Text("Beskriv el-flytting") },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedLabelColor = Color.White,
                    unfocusedLabelColor = Color.White,
                    cursorColor = Color.White,
                    focusedBorderColor = Color.White,
                    unfocusedBorderColor = Color.White.copy(alpha = 0.5f)
                ),
                minLines = 3
            )
        }
    }
}