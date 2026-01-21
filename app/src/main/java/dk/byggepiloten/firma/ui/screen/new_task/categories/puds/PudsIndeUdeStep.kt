// Fil: app/src/main/java/dk/byggepiloten/firma/ui/screen/new_task/categories/puds/PudsIndeUdeStep.kt
// OPDATERET – brug af reusable ChoiceBoxRow i Card (ens med andre simple valg-steps)

package dk.byggepiloten.firma.ui.screen.new_task.categories.puds

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dk.byggepiloten.firma.ui.screen.new_task.components.common.ChoiceBoxRow
import dk.byggepiloten.firma.ui.viewmodel.task.PudsTaskViewModel

private val indeUdeOptions = listOf("Inde", "Ude")

@Composable
fun PudsIndeUdeStep(
    viewModel: PudsTaskViewModel
) {
    val data by viewModel.pudsData.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.95f)),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                ChoiceBoxRow(
                    label = null,
                    options = indeUdeOptions,
                    selectedOption = data.indeUde ?: "",
                    onOptionSelected = {
                        viewModel.updatePudsData(data.copy(indeUde = it))
                    }
                )
            }
        }
    }
}