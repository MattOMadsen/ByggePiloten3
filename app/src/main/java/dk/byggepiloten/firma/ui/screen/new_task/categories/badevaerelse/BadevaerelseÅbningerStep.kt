// Fil: app/src/main/java/dk/byggepiloten/firma/ui/screen/new_task/categories/badevaerelse/BadevaerelseÅbningerStep.kt
// RETTET: Ny OutlinedTextFieldDefaults.colors() signatur.

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
import dk.byggepiloten.firma.data.model.BadevaerelseData

/**
 * Step 10: Åbninger og detaljer.
 * Manuel fradrag m² for vinduer/døre + toilet/vask montering.
 * Live visning af netto vægareal (brutto væg - fradrag).
 */
@Composable
fun BadevaerelseÅbningerStep(
    data: BadevaerelseData,
    onDataChange: (BadevaerelseData) -> Unit
) {
    var deductionText by remember { mutableStateOf(data.deductionAreaWalls?.toString() ?: "") }

    val deduction = deductionText.toFloatOrNull() ?: 0f

    // Beregn brutto vægareal (fra step 3)
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
        OutlinedTextField(
            value = deductionText,
            onValueChange = { deductionText = it },
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
            )
        )

        Spacer(Modifier.height(24.dp))
        if (bruttoVaegAreal > 0f) {
            Card(colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.2f))) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "Brutto vægareal: $bruttoVaegAreal m²",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "Netto vægareal: $nettoVaegAreal m²",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(Modifier.height(32.dp))
        Text("Montering af toilet og vask?", color = Color.White, style = MaterialTheme.typography.titleMedium)
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            listOf("Ja", "Nej").forEach { option ->
                val selected = if (option == "Ja") data.installToiletSink == true else data.installToiletSink == false
                Box(
                    modifier = Modifier
                        .clickable { onDataChange(data.copy(installToiletSink = option == "Ja")) }
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