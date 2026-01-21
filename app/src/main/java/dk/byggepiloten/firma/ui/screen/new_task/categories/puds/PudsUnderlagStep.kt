// Fil: app/src/main/java/dk/byggepiloten/firma/ui/screen/new_task/categories/puds/PudsUnderlagStep.kt
// RETTET – fuld version til "Ude" (revner + fugt + gammel puds), ChoiceBoxRow, stepPhotos-type tvunget

package dk.byggepiloten.firma.ui.screen.new_task.categories.puds

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dk.byggepiloten.firma.ui.screen.new_task.components.PhotoUploadSection
import dk.byggepiloten.firma.ui.screen.new_task.components.common.ChoiceBoxRow
import dk.byggepiloten.firma.ui.viewmodel.task.PudsTaskViewModel

@Composable
fun PudsUnderlagStep(
    viewModel: PudsTaskViewModel
) {
    val pudsData by viewModel.pudsData.collectAsStateWithLifecycle()
    val stepPhotos by viewModel.stepPhotos.collectAsStateWithLifecycle(emptyMap<String, List<android.net.Uri>>())

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
                Spacer(Modifier.height(12.dp))
                ChoiceBoxRow(
                    label = "Er der gammel puds?",
                    options = listOf("Ja", "Nej"),
                    selectedOption = pudsData.underlagGammelPuds ?: "",
                    onOptionSelected = { viewModel.updatePudsData(pudsData.copy(underlagGammelPuds = it)) }
                )
            }
        }

        PhotoUploadSection(
            label = "Billeder af underlag (anbefalet)",
            isRequired = false,
            currentUris = stepPhotos["underlag"] ?: emptyList(),
            onUrisChange = { viewModel.updateStepPhotos("underlag", it) }
        )
    }
}