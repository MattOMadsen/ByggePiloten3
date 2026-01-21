// Fil: app/src/main/java/dk/byggepiloten/firma/ui/screen/new_task/categories/puds/PudsUnderlagStep.kt
// FULD RETTET – tilføjet import getValue (fikser delegation fejl)
// Eksplicit parameter i PhotoUploadSection lambda (fikser type-konflikt med 'it')

package dk.byggepiloten.firma.ui.screen.new_task.categories.puds

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
            onUrisChange = { uris -> viewModel.updateStepPhotos("underlag", uris) }
        )
    }
}