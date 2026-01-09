// Fil: app/src/main/java/dk/byggepiloten/firma/ui/screen/OpmuringMortarStep.kt
// SEPARAT STEP 7: Mørtel type (kun ved ny mur)

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
fun OpmuringMortarStep(
    mortarType: String?,
    onMortarTypeChange: (String) -> Unit,
    customMortarType: String?,
    onCustomMortarTypeChange: (String) -> Unit
) {
    Text("Mørtel type", color = Color.White, fontWeight = FontWeight.Medium, fontSize = 20.sp)
    Spacer(Modifier.height(24.dp))

    val options = listOf("Standard KC-mørtel", "Bastardmørtel", "Lime-mørtel", "Andet")
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        options.forEach { option ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onMortarTypeChange(option) }
                    .background(if (mortarType == option) ByggePilotenBlue else Color.White, RoundedCornerShape(8.dp))
                    .padding(vertical = 16.dp)
            ) {
                Text(
                    option,
                    color = if (mortarType == option) Color.White else Color.Black,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }
    }

    if (mortarType == "Andet") {
        Spacer(Modifier.height(16.dp))
        OutlinedTextField(
            value = customMortarType ?: "",
            onValueChange = onCustomMortarTypeChange,
            label = { Text("Beskriv mørtel") },
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