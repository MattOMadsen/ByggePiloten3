// Fil: app/src/main/java/dk/byggepiloten/firma/ui/screen/OpmuringStoneStep.kt
// SEPARAT STEP 6: Sten type (kun ved ny mur)

package dk.byggepiloten.firma.ui.screen.new_task.categories.opmuring

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dk.byggepiloten.firma.ui.theme.ByggePilotenBlue

@Composable
fun OpmuringStoneStep(
    stoneType: String?,
    onStoneTypeChange: (String) -> Unit,
    customStoneType: String?,
    onCustomStoneTypeChange: (String) -> Unit
) {
    Text("Sten type", color = Color.White, fontWeight = FontWeight.Medium, fontSize = 20.sp)
    Spacer(Modifier.height(24.dp))

    val options = listOf(
        "Almindelig rød mursten",
        "Gul mursten",
        "Facadesten / tegl",
        "Håndstrøgne sten",
        "Gasbetonblokke",
        "Letbetonblokke",
        "Kalksandsten",
        "Anden"
    )
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        options.forEach { option ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onStoneTypeChange(option) }
                    .background(if (stoneType == option) ByggePilotenBlue else Color.White, RoundedCornerShape(8.dp))
                    .padding(vertical = 16.dp)
            ) {
                Text(
                    option,
                    color = if (stoneType == option) Color.White else Color.Black,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }
    }

    if (stoneType == "Anden") {
        Spacer(Modifier.height(16.dp))
        OutlinedTextField(
            value = customStoneType ?: "",
            onValueChange = onCustomStoneTypeChange,
            label = { Text("Beskriv sten") },
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