// app/src/main/java/dk/byggepiloten/firma/ui/screen/new_task/categories/badevaerelse/BadevaerelseVådrumssikringStep.kt
// RETTET: Fjernet header-kommentar før package

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
import dk.byggepiloten.firma.data.model.task.BadevaerelseData

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
                color = Color(0xFFFF4444),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(16.dp))
        }
        val membraneOptions = listOf("Ja", "Nej")
        membraneOptions.forEach { option ->
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .background(
                        if ((option == "Ja" && data.hasMembrane == true) || (option == "Nej" && data.hasMembrane == false)) MaterialTheme.colorScheme.primary else Color.White,
                        RoundedCornerShape(16.dp)
                    )
                    .clickable { onDataChange(data.copy(hasMembrane = option == "Ja")) }
                    .padding(vertical = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    option,
                    color = if ((option == "Ja" && data.hasMembrane == true) || (option == "Nej" && data.hasMembrane == false)) Color.White else Color.Black,
                    style = MaterialTheme.typography.titleMedium
                )
            }

            Spacer(Modifier.height(16.dp))
        }

        Spacer(Modifier.height(32.dp))
        Text("Ventilation?", color = Color.White, style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(16.dp))
        val ventOptions = listOf("Ja", "Nej")
        ventOptions.forEach { option ->
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .background(
                        if ((option == "Ja" && data.hasVentilation == true) || (option == "Nej" && data.hasVentilation == false)) MaterialTheme.colorScheme.primary else Color.White,
                        RoundedCornerShape(16.dp)
                    )
                    .clickable { onDataChange(data.copy(hasVentilation = option == "Ja")) }
                    .padding(vertical = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    option,
                    color = if ((option == "Ja" && data.hasVentilation == true) || (option == "Nej" && data.hasVentilation == false)) Color.White else Color.Black,
                    style = MaterialTheme.typography.titleMedium
                )
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}
