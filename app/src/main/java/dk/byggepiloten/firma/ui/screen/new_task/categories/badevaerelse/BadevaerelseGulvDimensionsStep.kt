// Fil: app/src/main/java/dk/byggepiloten/firma/ui/screen/new_task/categories/badevaerelse/BadevaerelseGulvDimensionsStep.kt
// RETTET: TextFields nu pænt alignet i Row (samme bundlinje, bedre spacing).

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
import dk.byggepiloten.firma.data.model.BadevaerelseData

@Composable
fun BadevaerelseGulvDimensionsStep(
    data: BadevaerelseData,
    onDataChange: (BadevaerelseData) -> Unit
) {
    var lengthText by remember { mutableStateOf(data.floorLength?.toString() ?: "") }
    var widthText by remember { mutableStateOf(data.floorWidth?.toString() ?: "") }

    val length = lengthText.toFloatOrNull()
    val width = widthText.toFloatOrNull()
    val gulvAreal = if (length != null && width != null) length * width else 0f

    LaunchedEffect(lengthText, widthText) {
        if (length != null && width != null) {
            onDataChange(data.copy(floorLength = length, floorWidth = width))
        }
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            "Gulv dimensioner",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Spacer(Modifier.height(16.dp))
        Text(
            "Indtast længde og bredde i meter",
            color = Color.White,
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(Modifier.height(32.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.Bottom // Samme bundlinje
        ) {
            OutlinedTextField(
                value = lengthText,
                onValueChange = { lengthText = it },
                label = { Text("Længde (m)") },
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
                modifier = Modifier.weight(1f)
            )

            Text("×", color = Color.White, style = MaterialTheme.typography.headlineMedium)

            OutlinedTextField(
                value = widthText,
                onValueChange = { widthText = it },
                label = { Text("Bredde (m)") },
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
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(Modifier.height(24.dp))

        if (gulvAreal > 0f) {
            Card(colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.2f))) {
                Text(
                    "Gulvareal: $gulvAreal m²",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}