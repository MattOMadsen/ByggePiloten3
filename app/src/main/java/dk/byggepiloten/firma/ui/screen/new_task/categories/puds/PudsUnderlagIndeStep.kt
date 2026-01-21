// Fil: app/src/main/java/dk/byggepiloten/firma/ui/screen/new_task/categories/puds/PudsUnderlagIndeStep.kt
// RETTET – let version til "Inde" (kun revner + fugt), ChoiceBoxRow

package dk.byggepiloten.firma.ui.screen.new_task.categories.puds

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dk.byggepiloten.firma.ui.screen.new_task.components.common.ChoiceBoxRow
import dk.byggepiloten.firma.ui.viewmodel.task.PudsTaskViewModel

@Composable
fun PudsUnderlagIndeStep(
    viewModel: PudsTaskViewModel
) {
    val pudsData by viewModel.pudsData.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text(
            "Hvordan er underlaget?",
            style = MaterialTheme.typography.titleLarge,
            color = Color.Black
        )

        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.95f)),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                ChoiceBoxRow(
                    label = "Er der revner?",
                    options = listOf("Ja", "Nej"),
                    selectedOption = pudsData.underlagRevner ?: "",
                    onOptionSelected = { viewModel.updatePudsData(pudsData.copy(underlagRevner = it)) }
                )
                Spacer(Modifier.height(12.dp))
                ChoiceBoxRow(
                    label = "Er der fugtskader?",
                    options = listOf("Ja", "Nej"),
                    selectedOption = pudsData.underlagFugt ?: "",
                    onOptionSelected = { viewModel.updatePudsData(pudsData.copy(underlagFugt = it)) }
                )
            }
        }
    }
}