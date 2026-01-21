// Fil: app/src/main/java/dk/byggepiloten/firma/ui/screen/new_task/categories/puds/PudsIndeUdeStep.kt
// NY FIL – Trin 1: Inde eller ude? Bruger ChoiceBoxRow som i opmuring (ingen RadioButton)

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

private val indeUdeOptions = listOf("Inde", "Ude")

/**
 * Trin 1 i puds-wizarden: Brugeren vælger om pudsningen er inde eller ude.
 * Valget styrer hvilke trin der skippes senere (højde, stillads, vejr, armering/isolering).
 * Bruger ChoiceBoxRow for ensartet design med opmuring.
 */
@Composable
fun PudsIndeUdeStep(
    viewModel: PudsTaskViewModel
) {
    val pudsData by viewModel.pudsData.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text(
            text = "Er pudsningen inde eller ude?",
            style = MaterialTheme.typography.titleLarge,
            color = Color.Black
        )

        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.95f)),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                ChoiceBoxRow(
                    label = null,
                    options = indeUdeOptions,
                    selectedOption = pudsData.indeUde ?: "",
                    onOptionSelected = {
                        viewModel.updatePudsData(pudsData.copy(indeUde = it))
                    }
                )
            }
        }
    }
}