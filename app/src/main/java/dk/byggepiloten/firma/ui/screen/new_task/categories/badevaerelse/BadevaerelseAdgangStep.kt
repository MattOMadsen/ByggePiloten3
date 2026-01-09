// Fil: app/src/main/java/dk/byggepiloten/firma/ui/screen/new_task/categories/badevaerelse/BadevaerelseAdgangStep.kt
// RETTET: floorNumber-reference nu gyldig (efter tilføjelse i BadevaerelseData).
// - Logik: "Er der trappeopgang?" → Ja = goodAccess = false → vis etage-felt.

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

@Composable
fun BadevaerelseAdgangStep(
    data: BadevaerelseData,
    onDataChange: (BadevaerelseData) -> Unit
) {
    var floorText by remember { mutableStateOf(data.floorNumber?.toString() ?: "") }

    LaunchedEffect(floorText) {
        floorText.toIntOrNull()?.let {
            onDataChange(data.copy(floorNumber = it))
        }
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            "Adgang til badeværelset",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Spacer(Modifier.height(16.dp))
        Text(
            "Er der trappeopgang?",
            color = Color.White,
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(Modifier.height(32.dp))

        val yesNo = listOf("Ja", "Nej")
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            yesNo.forEach { option ->
                val selected = if (option == "Ja") data.goodAccess == false else data.goodAccess == true
                Box(
                    modifier = Modifier
                        .clickable {
                            onDataChange(data.copy(goodAccess = option == "Nej", floorNumber = null))
                            floorText = ""
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

        if (data.goodAccess == false) {
            Spacer(Modifier.height(32.dp))
            Text("Hvilken etage?", color = Color.White, style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(16.dp))
            OutlinedTextField(
                value = floorText,
                onValueChange = { floorText = it },
                label = { Text("Etage (tal)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
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
        }
    }
}