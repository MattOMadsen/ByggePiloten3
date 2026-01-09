// Fil: app/src/main/java/dk/byggepiloten/firma/ui/screen/OpmuringFoundationStep.kt
// SEPARAT STEP 13: Fundament (kun ved ny mur)

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
fun OpmuringFoundationStep(
    foundationOption: String?,
    onFoundationOptionChange: (String) -> Unit,
    customFoundation: String,
    onCustomFoundationChange: (String) -> Unit
) {
    Text("Fundament", color = Color.White, fontWeight = FontWeight.Medium, fontSize = 20.sp)
    Spacer(Modifier.height(24.dp))

    val options = listOf("Ja, nyt nødvendigt", "Nej, eksisterende OK", "Uvidende", "Andet")
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        options.forEach { option ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onFoundationOptionChange(option) }
                    .background(if (foundationOption == option) ByggePilotenBlue else Color.White, RoundedCornerShape(8.dp))
                    .padding(vertical = 16.dp)
            ) {
                Text(
                    option,
                    color = if (foundationOption == option) Color.White else Color.Black,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }
    }

    if (foundationOption == "Andet") {
        Spacer(Modifier.height(16.dp))
        OutlinedTextField(
            value = customFoundation,
            onValueChange = onCustomFoundationChange,
            label = { Text("Beskriv") },
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