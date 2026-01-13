// Fil: app/src/main/java/dk/byggepiloten/firma/ui/screen/new_task/categories/badevaerelse/BadevaerelseVaeggeStep.kt
// FULD OPDATERET: Vertikalt layout, labels over felt
// - Live vægareal beholdt (perimeter × højde)
// - onDataChange callback
// - Linjer: 112

package dk.byggepiloten.firma.ui.screen.new_task.categories.badevaerelse

import androidx.compose.foundation.layout.*
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
fun BadevaerelseVaeggeStep(
    data: BadevaerelseData,
    onDataChange: (BadevaerelseData) -> Unit
) {
    var heightText by remember { mutableStateOf(data.wallHeight?.toString() ?: "") }

    val height = heightText.toFloatOrNull()
    val perimeter = if (data.floorLength != null && data.floorWidth != null) {
        (data.floorLength!! * 2) + (data.floorWidth!! * 2)
    } else 0f
    val vaegAreal = if (height != null && perimeter > 0f) perimeter * height else 0f

    LaunchedEffect(heightText) {
        if (height != null) {
            onDataChange(data.copy(wallHeight = height))
        }
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            "Vægge",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Spacer(Modifier.height(16.dp))
        Text(
            "Indtast væghøjde (samme for alle vægge)",
            color = Color.White,
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(Modifier.height(32.dp))

        Text("Højde (m)", color = Color.White, style = MaterialTheme.typography.titleMedium)
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

        Spacer(Modifier.height(32.dp))

        if (vaegAreal > 0f) {
            Card(colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.2f))) {
                Text(
                    "Vægareal (brutto): ${"%.2f".format(vaegAreal)} m²",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        } else if (perimeter == 0f) {
            Text(
                "Udfyld gulv dimensioner først",
                color = Color.White.copy(alpha = 0.7f)
            )
        }
    }
}
