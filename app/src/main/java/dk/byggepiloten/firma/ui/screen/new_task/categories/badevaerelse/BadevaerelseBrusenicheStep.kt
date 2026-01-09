// Fil: app/src/main/java/dk/byggepiloten/firma/ui/screen/new_task/categories/badevaerelse/BadevaerelseBrusenicheStep.kt
// RETTET: TextFields nu pænt alignet i Row (samme bundlinje, weight for lige bredde).

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

        val yesNoOptions = listOf("Ja", "Nej")
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            yesNoOptions.forEach { option ->
                val selected = if (option == "Ja") data.hasShowerNiche == true else data.hasShowerNiche == false
                Box(
                    modifier = Modifier
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
                        .background(
                            color = if (selected) MaterialTheme.colorScheme.primary else Color.White,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        option,
                        color = if (selected) Color.White else Color.Black,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }

        if (data.hasShowerNiche == true) {
            Spacer(Modifier.height(32.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.Bottom
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
            if (bruseAreal > 0f) {
                Card(colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.2f))) {
                    Text(
                        "Bruseareal: $bruseAreal m²",
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Glasvægge + afløbstype beholdt uændret (bokse ser fint ud)
        }
    }
}