// Fil: app/src/main/java/dk/byggepiloten/firma/ui/screen/new_task/categories/badevaerelse/BadevaerelseBrusenicheStep.kt
// FULD OPDATERET: Vertikalt layout, labels over felter
// - Live bruseareal beholdt
// - Bokse under hinanden for Ja/Nej + glasvægge/afløb
// - onDataChange callback
// - Linjer: 168

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
import dk.byggepiloten.firma.data.model.task.BadevaerelseData

@Composable
fun BadevaerelseBrusenicheStep(
    data: BadevaerelseData,
    onDataChange: (BadevaerelseData) -> Unit
) {
    var lengthText by remember { mutableStateOf(data.showerLength?.toString() ?: "") }
    var widthText by remember { mutableStateOf(data.showerWidth?.toString() ?: "") }

    val length = lengthText.toFloatOrNull()
    val width = widthText.toFloatOrNull()
    val bruseAreal = if (length != null && width != null) length * width else 0f

    LaunchedEffect(lengthText, widthText) {
        if (data.hasShowerNiche == true && length != null && width != null) {
            onDataChange(data.copy(showerLength = length, showerWidth = width))
        }
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            "Bruseniche?",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Spacer(Modifier.height(16.dp))

        Text("Skal der være bruseniche?", color = Color.White, style = MaterialTheme.typography.bodyLarge)
        Spacer(Modifier.height(32.dp))

        val yesNoOptions = listOf("Ja", "Nej")
        yesNoOptions.forEach { option ->
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .background(
                        if ((option == "Ja" && data.hasShowerNiche == true) || (option == "Nej" && data.hasShowerNiche == false)) MaterialTheme.colorScheme.primary else Color.White,
                        RoundedCornerShape(16.dp)
                    )
                    .clickable {
                        onDataChange(
                            data.copy(
                                hasShowerNiche = option == "Ja",
                                showerLength = null,
                                showerWidth = null,
                                hasGlassWalls = null,
                                drainType = null
                            )
                        )
                        lengthText = ""
                        widthText = ""
                    }
                    .padding(vertical = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    option,
                    color = if ((option == "Ja" && data.hasShowerNiche == true) || (option == "Nej" && data.hasShowerNiche == false)) Color.White else Color.Black,
                    style = MaterialTheme.typography.titleMedium
                )
            }

            Spacer(Modifier.height(16.dp))
        }

        if (data.hasShowerNiche == true) {
            Spacer(Modifier.height(32.dp))

            Text("Længde (m)", color = Color.White, style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = lengthText,
                onValueChange = { lengthText = it },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    cursorColor = MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier.fillMaxWidth(0.8f)
            )

            Spacer(Modifier.height(24.dp))
            Text("Bredde (m)", color = Color.White, style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = widthText,
                onValueChange = { widthText = it },
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
            if (bruseAreal > 0f) {
                Card(colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.2f))) {
                    Text(
                        "Bruseareal: ${"%.2f".format(bruseAreal)} m²",
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Glasvægge + afløbstype beholdt som bokse under hinanden (for bedre overskuelighed)
            Spacer(Modifier.height(32.dp))
            Text("Glasvægge?", color = Color.White, style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(16.dp))
            val glassOptions = listOf("Ja", "Nej")
            glassOptions.forEach { option ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .background(
                            if ((option == "Ja" && data.hasGlassWalls == true) || (option == "Nej" && data.hasGlassWalls == false)) MaterialTheme.colorScheme.primary else Color.White,
                            RoundedCornerShape(16.dp)
                        )
                        .clickable { onDataChange(data.copy(hasGlassWalls = option == "Ja")) }
                        .padding(vertical = 24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        option,
                        color = if ((option == "Ja" && data.hasGlassWalls == true) || (option == "Nej" && data.hasGlassWalls == false)) Color.White else Color.Black,
                        style = MaterialTheme.typography.titleMedium
                    )
                }

                Spacer(Modifier.height(16.dp))
            }

            Spacer(Modifier.height(32.dp))
            Text("Afløbstype", color = Color.White, style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(16.dp))
            val drainOptions = listOf("Punkt afløb", "Lineær afløb")
            drainOptions.forEach { option ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .background(
                            if (data.drainType == option) MaterialTheme.colorScheme.primary else Color.White,
                            RoundedCornerShape(16.dp)
                        )
                        .clickable { onDataChange(data.copy(drainType = option)) }
                        .padding(vertical = 24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        option,
                        color = if (data.drainType == option) Color.White else Color.Black,
                        style = MaterialTheme.typography.titleMedium
                    )
                }

                Spacer(Modifier.height(16.dp))
            }
        }
    }
}
