// Fil: app/src/main/java/dk/byggepiloten/firma/ui/screen/new_task/categories/opmuring/OpmuringOpeningsStep.kt
// FULD OPDATERET – Ændret til viewModel-parameter
// Bind direkte til viewModel.updateWallData og updateStepPhotos
// Layout uændret (individuelle åbninger + photo upload)

package dk.byggepiloten.firma.ui.screen.new_task.categories.opmuring

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dk.byggepiloten.firma.data.model.task.OpeningMeasurement
import dk.byggepiloten.firma.ui.screen.new_task.components.PhotoUploadSection
import dk.byggepiloten.firma.ui.screen.new_task.components.common.ChoiceBox
import dk.byggepiloten.firma.ui.screen.new_task.components.common.StyledTextField
import dk.byggepiloten.firma.ui.theme.ByggePilotenBlue
import dk.byggepiloten.firma.ui.viewmodel.task.OpmuringTaskViewModel

private val modeOptions = listOf("Samlet areal", "Individuelle åbninger")

@Composable
fun OpmuringOpeningsStep(
    viewModel: OpmuringTaskViewModel
) {
    val data by viewModel.wallData.collectAsStateWithLifecycle()
    val openingsPhotos = viewModel.stepPhotos.collectAsState().value["openings"] ?: emptyList()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(40.dp)
    ) {
        Text(
            text = "Er der åbninger i muren (vinduer/døre)?",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        ChoiceBox(
            options = modeOptions,
            selectedOption = data.openingMode,
            onOptionSelected = { viewModel.updateWallData(data.copy(openingMode = it)) }
        )

        if (data.openingMode == "Individuelle åbninger") {
            data.openingMeasurements.forEachIndexed { index, measurement ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    StyledTextField(
                        value = measurement.widthCm?.toString() ?: "",
                        onValueChange = { value ->
                            val newList = data.openingMeasurements.toMutableList()
                            newList[index] = measurement.copy(widthCm = value.toFloatOrNull())
                            viewModel.updateWallData(data.copy(openingMeasurements = newList))
                        },
                        label = "Bredde (cm)",
                        keyboardType = KeyboardType.Decimal,
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )

                    Spacer(Modifier.width(16.dp))

                    StyledTextField(
                        value = measurement.heightCm?.toString() ?: "",
                        onValueChange = { value ->
                            val newList = data.openingMeasurements.toMutableList()
                            newList[index] = measurement.copy(heightCm = value.toFloatOrNull())
                            viewModel.updateWallData(data.copy(openingMeasurements = newList))
                        },
                        label = "Højde (cm)",
                        keyboardType = KeyboardType.Decimal,
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )

                    IconButton(
                        onClick = {
                            val newList = data.openingMeasurements.toMutableList()
                            if (newList.size > 1) newList.removeAt(index)
                            viewModel.updateWallData(data.copy(openingMeasurements = newList))
                        },
                        enabled = data.openingMeasurements.size > 1
                    ) {
                        Icon(Icons.Filled.Close, contentDescription = "Fjern åbning", tint = Color.White)
                    }
                }
            }

            FilledTonalButton(
                onClick = {
                    viewModel.updateWallData(
                        data.copy(
                            openingMeasurements = data.openingMeasurements + OpeningMeasurement()
                        )
                    )
                },
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = Color.White,
                    contentColor = ByggePilotenBlue
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Tilføj åbning", fontWeight = FontWeight.Medium)
            }
        }

        PhotoUploadSection(
            label = "Upload billeder af åbninger (anbefalet)",
            isRequired = false,
            currentUris = openingsPhotos,
            onUrisChange = { viewModel.updateStepPhotos("openings", it) }
        )
    }
}