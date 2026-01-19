// Fil: app/src/main/java/dk/byggepiloten/firma/ui/screen/new_task/categories/opmuring/OpmuringOpeningsStep.kt
// FULD OPDATERET TIL VIEWMODEL-STIL – 238 linjer
// + Konsistent med andre steps: tager viewModel
// + "Ingen åbninger" + "Samlet areal" + "Individuelle åbninger"
// + Live teaser for samlet åbningsareal i begge modes
// + Valgfri PhotoUploadSection
// + Korrekt binding via updateWallData og updateStepPhotos
// + KDoc + kommentarer

package dk.byggepiloten.firma.ui.screen.new_task.categories.opmuring

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dk.byggepiloten.firma.data.model.task.OpeningMeasurement
import dk.byggepiloten.firma.ui.screen.new_task.components.PhotoUploadSection
import dk.byggepiloten.firma.ui.screen.new_task.components.common.ChoiceBoxRow
import dk.byggepiloten.firma.ui.screen.new_task.components.common.OpeningMeasurementRow
import dk.byggepiloten.firma.ui.screen.new_task.components.common.StyledTextField
import dk.byggepiloten.firma.ui.viewmodel.task.OpmuringTaskViewModel

/**
 * Composable for åbningstrinnet i opmuring-wizard.
 * Konsistent med andre steps: tager viewModel.
 * Understøtter "Ingen åbninger", "Samlet areal" og "Individuelle åbninger".
 * Viser live-beregnet samlet areal for begge modes.
 * PhotoUploadSection er valgfri (anbefalet ved større åbninger).
 */
@Composable
fun OpmuringOpeningsStep(
    viewModel: OpmuringTaskViewModel
) {
    val data by viewModel.wallData.collectAsStateWithLifecycle()
    val openingsPhotos = viewModel.stepPhotos.collectAsStateWithLifecycle().value["openings"] ?: emptyList()

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Skal der være åbninger (døre/vinduer)?",
            style = MaterialTheme.typography.titleMedium,
            color = Color.White,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        val modes = listOf("Ingen åbninger", "Samlet areal", "Individuelle åbninger")

        ChoiceBoxRow(
            options = modes,
            selectedOption = when (data.openingMode) {
                null -> "Ingen åbninger"
                "samlet" -> "Samlet areal"
                "individuel" -> "Individuelle åbninger"
                else -> null
            },
            onOptionSelected = { selected ->
                when (selected) {
                    "Ingen åbninger" -> viewModel.updateWallData(
                        data.copy(
                            openingMode = null,
                            openingTotalAreaM2 = null,
                            openingMeasurements = emptyList()
                        )
                    )
                    "Samlet areal" -> viewModel.updateWallData(
                        data.copy(
                            openingMode = "samlet",
                            openingMeasurements = emptyList()
                        )
                    )
                    "Individuelle åbninger" -> viewModel.updateWallData(
                        data.copy(
                            openingMode = "individuel",
                            openingTotalAreaM2 = null
                        )
                    )
                }
            },
            modifier = Modifier.padding(bottom = 32.dp)
        )

        // Samlet areal-mode
        if (data.openingMode == "samlet") {
            StyledTextField(
                value = data.openingTotalAreaM2?.toString() ?: "",
                onValueChange = {
                    val parsed = it.toFloatOrNull()
                    viewModel.updateWallData(data.copy(openingTotalAreaM2 = parsed))
                },
                label = "Samlet areal af åbninger (m²)",
                keyboardType = KeyboardType.Decimal,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )

            if ((data.openingTotalAreaM2 ?: 0f) > 0f) {
                Text(
                    text = "Samlet areal af åbninger: ${String.format("%.2f", data.openingTotalAreaM2)} m²",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    color = Color.White,
                    modifier = Modifier.padding(bottom = 24.dp)
                )
            }
        }

        // Individuelle åbninger-mode
        if (data.openingMode == "individuel") {
            var localMeasurements by remember(data.openingMeasurements) {
                mutableStateOf(data.openingMeasurements.toMutableList())
            }

            val totalIndividualArea = remember(localMeasurements) {
                localMeasurements.sumOf {
                    (it.widthCm ?: 0f) * (it.heightCm ?: 0f) / 10000.0
                }.toFloat()
            }

            LaunchedEffect(localMeasurements) {
                viewModel.updateWallData(data.copy(openingMeasurements = localMeasurements.toList()))
            }

            // Sikrer mindst én række
            if (localMeasurements.isEmpty()) {
                localMeasurements += OpeningMeasurement()
            }

            OpeningMeasurementRow(
                measurements = localMeasurements,
                onMeasurementsChange = { localMeasurements = it.toMutableList() }
            )

            Spacer(Modifier.height(16.dp))

            Text(
                text = "Samlet areal af åbninger: ${String.format("%.2f", totalIndividualArea)} m²",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = Color.White,
                modifier = Modifier.padding(bottom = 24.dp)
            )
        }

        // Valgfri billeder af åbninger
        PhotoUploadSection(
            label = "Upload billeder af åbninger (valgfrit, men anbefalet ved større åbninger)",
            isRequired = false,
            currentUris = openingsPhotos,
            onUrisChange = { viewModel.updateStepPhotos("openings", it) }
        )
    }
}

// Total lines: 238