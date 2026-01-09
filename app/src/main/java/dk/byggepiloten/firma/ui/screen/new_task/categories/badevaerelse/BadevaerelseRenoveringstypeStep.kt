// Fil: app/src/main/java/dk/byggepiloten/firma/ui/screen/new_task/categories/badevaerelse/BadevaerelseRenoveringstypeStep.kt
// RETTET: Skiftet LazyVerticalStaggeredGrid → FlowRow (som FacadePudsningScreen + planen anbefaler).
// TILFØJET: import androidx.compose.foundation.layout.FlowRow

package dk.byggepiloten.firma.ui.screen.new_task.categories.badevaerelse

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dk.byggepiloten.firma.data.model.BadevaerelseData

/**
 * Step 1: Valg af renoveringstype.
 * FlowRow for automatisk wrap (som FacadePudsningScreen).
 */
@Composable
fun BadevaerelseRenoveringstypeStep(
    data: BadevaerelseData,
    onDataChange: (BadevaerelseData) -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            "Hvilken type renovering?",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Spacer(Modifier.height(16.dp))
        Text(
            "Skal hele badeværelset rives ned, eller kun overfladen (fliser mm.) udskiftes?",
            color = Color.White,
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(Modifier.height(32.dp))

        val options = listOf(
            "Fuldt nyt (med nedrivning)",
            "Delvis (kun overflade)"
        )

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            options.forEach { option ->
                val selected = data.renovationType == option
                Box(
                    modifier = Modifier
                        .clickable { onDataChange(data.copy(renovationType = option)) }
                        .background(
                            color = if (selected) MaterialTheme.colorScheme.primary else Color.White,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = option,
                        color = if (selected) Color.White else Color.Black,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}