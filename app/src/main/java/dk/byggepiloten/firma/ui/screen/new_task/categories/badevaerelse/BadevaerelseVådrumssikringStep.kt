// Fil: app/src/main/java/dk/byggepiloten/firma/ui/screen/new_task/categories/badevaerelse/BadevaerelseVådrumssikringStep.kt
package dk.byggepiloten.firma.ui.screen.new_task.categories.badevaerelse

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dk.byggepiloten.firma.data.model.BadevaerelseData

/**
 * Step 9: Vådrumssikring.
 * Membran (med rød advarsel hvis nej) + ventilation.
 */
@Composable
fun BadevaerelseVådrumssikringStep(
    data: BadevaerelseData,
    onDataChange: (BadevaerelseData) -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            "Vådrumssikring",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Spacer(Modifier.height(16.dp))
        Text(
            "Obligatoriske elementer i vådrum",
            color = Color.White,
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(Modifier.height(32.dp))

        Text("Vådrumsmembran?", color = Color.White, style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))
        if (data.hasMembrane == false) {
            Text(
                "Advarsel: Vådrumsmembran er obligatorisk i Danmark!",
                color = Color.Red,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(16.dp))
        }
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            listOf("Ja", "Nej").forEach { option ->
                val selected = if (option == "Ja") data.hasMembrane == true else data.hasMembrane == false
                Box(
                    modifier = Modifier
                        .clickable { onDataChange(data.copy(hasMembrane = option == "Ja")) }
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

        Spacer(Modifier.height(32.dp))
        Text("Ventilation?", color = Color.White, style = MaterialTheme.typography.titleMedium)
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            listOf("Ja", "Nej").forEach { option ->
                val selected = if (option == "Ja") data.hasVentilation == true else data.hasVentilation == false
                Box(
                    modifier = Modifier
                        .clickable { onDataChange(data.copy(hasVentilation = option == "Ja")) }
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
    }
}