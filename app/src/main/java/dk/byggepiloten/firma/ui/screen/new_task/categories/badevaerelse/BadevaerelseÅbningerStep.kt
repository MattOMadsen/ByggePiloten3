// Fil: app/src/main/java/dk/byggepiloten/firma/ui/screen/new_task/categories/badevaerelse/BadevaerelseÅbningerStep.kt
// FULD RETTET VERSION
// - Tilføjet manglende import for clickable
// - Live netto vægareal beholdt
// - Bokse under hinanden for montering
// - Linjer: 132

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
fun BadevaerelseÅbningerStep(
    data: BadevaerelseData,
    onDataChange: (BadevaerelseData) -> Unit
) {
    var deductionText by remember { mutableStateOf(data.deductionAreaWalls?.toString() ?: "") }

    val deduction = deductionText.toFloatOrNull() ?: 0f

    val perimeter = if (data.floorLength != null && data.floorWidth != null) {
        (data.floorLength!! * 2) + (data.floorWidth!! * 2)
    } else 0f
    val bruttoVaegAreal = if (data.wallHeight != null && perimeter > 0f) perimeter * data.wallHeight!! else 0f
    val nettoVaegAreal = (bruttoVaegAreal - deduction).coerceAtLeast(0f)

    LaunchedEffect(deductionText) {
        deductionText.toFloatOrNull()?.let {
            onDataChange(data.copy(deductionAreaWalls = it))
        }
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            "Åbninger og detaljer",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Spacer(Modifier.height(16.dp))
        Text(
            "Fradrag for vinduer, døre mm. + montering",
            color = Color.White,
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(Modifier.height(32.dp))

        Text("Fradrag i vægareal (m²)", color = Color.White, style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = deductionText,
            onValueChange = { deductionText = it },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                cursorColor = MaterialTheme.colorScheme.primary
            ),
            modifier = Modifier.fillMaxWidth(0.8f)
        )

        Spacer(Modifier.height(32.dp))
        if (bruttoVaegAreal > 0f) {
            Card(colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.2f))) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "Brutto vægareal: ${"%.2f".format(bruttoVaegAreal)} m²",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "Netto vægareal: ${"%.2f".format(nettoVaegAreal)} m²",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(Modifier.height(32.dp))
        Text("Montering af toilet og vask?", color = Color.White, style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(16.dp))
        val installOptions = listOf("Ja" to true, "Nej" to false)
        installOptions.forEach { (optionLabel, optionValue) ->
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .background(
                        if (data.installToiletSink == optionValue) MaterialTheme.colorScheme.primary else Color.White,
                        RoundedCornerShape(16.dp)
                    )
                    .clickable { onDataChange(data.copy(installToiletSink = optionValue)) }
                    .padding(vertical = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    optionLabel,
                    color = if (data.installToiletSink == optionValue) Color.White else Color.Black,
                    style = MaterialTheme.typography.titleMedium
                )
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}
