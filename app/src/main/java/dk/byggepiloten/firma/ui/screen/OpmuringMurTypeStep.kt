// Fil: app/src/main/java/dk/byggepiloten/firma/ui/screen/OpmuringMurTypeStep.kt
// SEPARAT STEP 1: Hvilken type mur? (med custom "Andet")

package dk.byggepiloten.firma.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dk.byggepiloten.firma.ui.theme.ByggePilotenBlue
import androidx.compose.foundation.text.KeyboardOptions as FoundationKeyboardOptions

@Composable
fun OpmuringMurTypeStep(
    murType: String?,
    onMurTypeChange: (String) -> Unit,
    customMurType: String?,
    onCustomMurTypeChange: (String) -> Unit
) {
    Text("Hvilken type mur?", color = Color.White, fontWeight = FontWeight.Medium, fontSize = 20.sp)
    Spacer(Modifier.height(24.dp))

    val options = listOf(
        "Facademur (skalmur/ydervæg)",
        "Bagmur eller indvendig væg",
        "Havemur eller støttemur",
        "Andet"
    )
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        options.forEach { option ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onMurTypeChange(option) }
                    .background(if (murType == option) ByggePilotenBlue else Color.White, RoundedCornerShape(8.dp))
                    .padding(vertical = 16.dp)
            ) {
                Text(
                    option,
                    color = if (murType == option) Color.White else Color.Black,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }
    }

    if (murType == "Andet") {
        Spacer(Modifier.height(16.dp))
        OutlinedTextField(
            value = customMurType ?: "",
            onValueChange = onCustomMurTypeChange,
            label = { Text("Beskriv murtype") },
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                cursorColor = ByggePilotenBlue
            )
        )
    }
}