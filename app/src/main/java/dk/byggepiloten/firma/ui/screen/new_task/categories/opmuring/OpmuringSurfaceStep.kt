// Fil: app/src/main/java/dk/byggepiloten/firma/ui/screen/OpmuringSurfaceStep.kt
// SEPARAT STEP 11: Ønsket overflade/afslutning (kun ved ny mur)

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
fun OpmuringSurfaceStep(
    surfaceFinish: String?,
    onSurfaceFinishChange: (String) -> Unit,
    customSurface: String,
    onCustomSurfaceChange: (String) -> Unit
) {
    Text("Ønsket overflade/afslutning", color = Color.White, fontWeight = FontWeight.Medium, fontSize = 20.sp)
    Spacer(Modifier.height(24.dp))

    val options = listOf("Blank mur", "Pudset/vandskuret", "Malet", "Med tagsten/afslutning på top", "Andet")
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        options.forEach { option ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSurfaceFinishChange(option) }
                    .background(if (surfaceFinish == option) ByggePilotenBlue else Color.White, RoundedCornerShape(8.dp))
                    .padding(vertical = 16.dp)
            ) {
                Text(
                    option,
                    color = if (surfaceFinish == option) Color.White else Color.Black,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }
    }

    if (surfaceFinish == "Andet") {
        Spacer(Modifier.height(16.dp))
        OutlinedTextField(
            value = customSurface,
            onValueChange = onCustomSurfaceChange,
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