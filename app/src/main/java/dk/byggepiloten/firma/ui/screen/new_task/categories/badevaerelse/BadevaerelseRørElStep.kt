// Fil: app/src/main/java/dk/byggepiloten/firma/ui/screen/new_task/categories/badevaerelse/BadevaerelseRørElStep.kt
// FULD OPDATERET: Vertikalt layout, labels over beskrivelsesfelter
// - Conditional beskrivelsesfelt ved "Ja"
// - onDataChange callback
// - Linjer: 148

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

        Text("Flytte rør?", color = Color.White, style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(16.dp))
        val pipeOptions = listOf("Ja" to true, "Nej" to false)
        pipeOptions.forEach { (optionLabel, optionValue) ->
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .background(
                        if (data.relocatePipes == optionValue) MaterialTheme.colorScheme.primary else Color.White,
                        RoundedCornerShape(16.dp)
                    )
                    .clickable {
                        onDataChange(data.copy(relocatePipes = optionValue, pipeDescription = null))
                        pipeDesc = ""
                    }
                    .padding(vertical = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    optionLabel,
                    color = if (data.relocatePipes == optionValue) Color.White else Color.Black,
                    style = MaterialTheme.typography.titleMedium
                )
            }

            Spacer(Modifier.height(16.dp))
        }

        if (data.relocatePipes == true) {
            Spacer(Modifier.height(16.dp))
            Text("Beskriv rørflytning", color = Color.White, style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = pipeDesc,
                onValueChange = { pipeDesc = it },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    cursorColor = MaterialTheme.colorScheme.primary
                ),
                minLines = 3,
                modifier = Modifier.fillMaxWidth(0.8f)
            )
        }

        Spacer(Modifier.height(32.dp))

        Text("Flytte el-installationer?", color = Color.White, style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(16.dp))
        val elOptions = listOf("Ja" to true, "Nej" to false)
        elOptions.forEach { (optionLabel, optionValue) ->
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .background(
                        if (data.relocateElectrical == optionValue) MaterialTheme.colorScheme.primary else Color.White,
                        RoundedCornerShape(16.dp)
                    )
                    .clickable {
                        onDataChange(data.copy(relocateElectrical = optionValue, electricalDescription = null))
                        electricalDesc = ""
                    }
                    .padding(vertical = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    optionLabel,
                    color = if (data.relocateElectrical == optionValue) Color.White else Color.Black,
                    style = MaterialTheme.typography.titleMedium
                )
            }

            Spacer(Modifier.height(16.dp))
        }

        if (data.relocateElectrical == true) {
            Spacer(Modifier.height(16.dp))
            Text("Beskriv el-flytting", color = Color.White, style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = electricalDesc,
                onValueChange = { electricalDesc = it },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    cursorColor = MaterialTheme.colorScheme.primary
                ),
                minLines = 3,
                modifier = Modifier.fillMaxWidth(0.8f)
            )
        }
    }
}
