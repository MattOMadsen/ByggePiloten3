// Fil: app/src/main/java/dk/byggepiloten/firma/ui/screen/OpmuringInsulationStep.kt
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
fun OpmuringInsulationStep(
    insulationWanted: Boolean?,
    onInsulationWantedChange: (Boolean) -> Unit,
    insulationThickness: String,
    onInsulationThicknessChange: (String) -> Unit
) {
    Text("Isolering ønsket?", color = Color.White, fontWeight = FontWeight.Medium, fontSize = 20.sp)
    Spacer(Modifier.height(24.dp))

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        listOf("Ja", "Nej").forEach { opt ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onInsulationWantedChange(opt == "Ja") }
                    .background(if (insulationWanted == (opt == "Ja")) ByggePilotenBlue else Color.White, RoundedCornerShape(8.dp))
                    .padding(vertical = 16.dp)
            ) {
                Text(
                    opt,
                    color = if (insulationWanted == (opt == "Ja")) Color.White else Color.Black,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }
    }

    if (insulationWanted == true) {
        Spacer(Modifier.height(16.dp))
        OutlinedTextField(
            value = insulationThickness,
            onValueChange = { if (it.all { c -> c.isDigit() }) onInsulationThicknessChange(it) },
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