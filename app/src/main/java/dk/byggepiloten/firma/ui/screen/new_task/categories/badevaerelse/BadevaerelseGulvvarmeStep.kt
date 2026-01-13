// app/src/main/java/dk/byggepiloten/firma/ui/screen/new_task/categories/badevaerelse/BadevaerelseGulvvarmeStep.kt
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
fun BadevaerelseGulvvarmeStep(
    data: BadevaerelseData,
    onDataChange: (BadevaerelseData) -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            "Gulvvarme?",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Spacer(Modifier.height(16.dp))
        Text(
            "Skal der installeres gulvvarme?",
            color = Color.White,
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(Modifier.height(32.dp))

        val yesNoOptions = listOf("Ja", "Nej")
        yesNoOptions.forEach { option ->
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .background(
                        if ((option == "Ja" && data.hasFloorHeating == true) || (option == "Nej" && data.hasFloorHeating == false)) MaterialTheme.colorScheme.primary else Color.White,
                        RoundedCornerShape(16.dp)
                    )
                    .clickable {
                        onDataChange(
                            data.copy(
                                hasFloorHeating = option == "Ja",
                                floorHeatingType = null
                            )
                        )
                    }
                    .padding(vertical = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    option,
                    color = if ((option == "Ja" && data.hasFloorHeating == true) || (option == "Nej" && data.hasFloorHeating == false)) Color.White else Color.Black,
                    style = MaterialTheme.typography.titleMedium
                )
            }

            Spacer(Modifier.height(16.dp))
        }

        if (data.hasFloorHeating == true) {
            Spacer(Modifier.height(32.dp))
            Text("Type gulvvarme", color = Color.White, style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(16.dp))
            val types = listOf("Elektrisk", "Vandbåren")
            types.forEach { type ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .background(
                            if (data.floorHeatingType == type) MaterialTheme.colorScheme.primary else Color.White,
                            RoundedCornerShape(16.dp)
                        )
                        .clickable { onDataChange(data.copy(floorHeatingType = type)) }
                        .padding(vertical = 24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        type,
                        color = if (data.floorHeatingType == type) Color.White else Color.Black,
                        style = MaterialTheme.typography.titleMedium
                    )
                }

                Spacer(Modifier.height(16.dp))
            }
        }
    }
}
