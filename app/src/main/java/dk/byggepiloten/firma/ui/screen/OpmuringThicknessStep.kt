// Fil: app/src/main/java/dk/byggepiloten/firma/ui/screen/OpmuringThicknessStep.kt
// RETTET: Tilføjet import androidx.compose.ui.text.input.KeyboardType

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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dk.byggepiloten.firma.ui.theme.ByggePilotenBlue

@Composable
fun OpmuringThicknessStep(
    thicknessOption: String?,
    onThicknessOptionChange: (String) -> Unit,
    customThickness: String,
    onCustomThicknessChange: (String) -> Unit
) {
    Text("Tykkelse", color = Color.White, fontWeight = FontWeight.Medium, fontSize = 20.sp)
    Spacer(Modifier.height(24.dp))

    val options = listOf("108 mm (halvsten)", "228 mm (helsten)", "Anden")
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        options.forEach { option ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onThicknessOptionChange(option) }
                    .background(if (thicknessOption == option) ByggePilotenBlue else Color.White, RoundedCornerShape(8.dp))
                    .padding(vertical = 16.dp)
            ) {
                Text(
                    option,
                    color = if (thicknessOption == option) Color.White else Color.Black,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }
    }

    if (thicknessOption == "Anden") {
        Spacer(Modifier.height(16.dp))
        OutlinedTextField(
            value = customThickness,
            onValueChange = { if (it.all { c -> c.isDigit() }) onCustomThicknessChange(it) },
            label = { Text("Tykkelse (mm)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
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