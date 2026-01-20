// Fil: app/src/main/java/dk/byggepiloten/firma/ui/screen/new_task/categories/opmuring/OpmuringOpeningsStep.kt
// OPDATERET: Compile-fix – alle updateWallData → updateWallDataDirect
// - Lokal state + syncToViewModel beholdt (god praksis for kompleks input)
// - Teaser + billeder uændret
// Total lines: ~280 (uændret)

package dk.byggepiloten.firma.ui.screen.new_task.categories.opmuring

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
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
import dk.byggepiloten.firma.ui.screen.new_task.components.common.ChoiceBoxColumn
import dk.byggepiloten.firma.ui.screen.new_task.components.common.OpeningMeasurementRow
import dk.byggepiloten.firma.ui.screen.new_task.components.common.StyledTextField
import dk.byggepiloten.firma.ui.viewmodel.task.OpmuringTaskViewModel

@Composable
fun OpmuringOpeningsStep(
    viewModel: OpmuringTaskViewModel
) {
    val data by viewModel.wallData.collectAsStateWithLifecycle()
    val openingsPhotos = viewModel.stepPhotos.collectAsStateWithLifecycle().value["openings"] ?: emptyList()

    var localMeasurements by remember { mutableStateOf(data.openingMeasurements.toMutableList()) }

    fun syncToViewModel() {
        viewModel.updateWallDataDirect(data.copy(openingMeasurements = localMeasurements.toList()))
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text(
            text = "Åbninger (døre/vinduer)",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        Text(
            text = "Angiv om der er døre eller vinduer i muren – det fratrækkes fra det samlede areal.",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White.copy(alpha = 0.85f)
        )

        val modeOptions = listOf("Ingen åbninger", "Samlet areal", "Individuelle åbninger")

        ChoiceBoxColumn(
            options = modeOptions,
            selectedOption = when (data.openingMode) {
                null -> "Ingen åbninger"
                "samlet" -> "Samlet areal"
                "individuel" -> "Individuelle åbninger"
                else -> null
            },
            onOptionSelected = { selected ->
                when (selected) {
                    "Ingen åbninger" -> {
                        localMeasurements.clear()
                        viewModel.updateWallDataDirect(
                            data.copy(
                                openingMode = null,
                                openingTotalAreaM2 = null,
                                openingMeasurements = emptyList()
                            )
                        )
                    }
                    "Samlet areal" -> {
                        localMeasurements.clear()
                        viewModel.updateWallDataDirect(
                            data.copy(
                                openingMode = "samlet",
                                openingMeasurements = emptyList()
                            )
                        )
                    }
                    "Individuelle åbninger" -> {
                        viewModel.updateWallDataDirect(
                            data.copy(
                                openingMode = "individuel",
                                openingTotalAreaM2 = null
                            )
                        )
                    }
                }
                syncToViewModel()
            }
        )

        if (data.openingMode == "samlet") {
            StyledTextField(
                value = data.openingTotalAreaM2?.toString() ?: "",
                onValueChange = { newValue ->
                    val cleaned = newValue.replace(',', '.')
                    viewModel.updateWallDataDirect(data.copy(openingTotalAreaM2 = cleaned.toFloatOrNull()))
                },
                label = "Samlet åbningsareal (m²)",
                keyboardType = KeyboardType.Decimal,
                singleLine = true
            )

            val totalSamletArea = data.openingTotalAreaM2 ?: 0f

            if (totalSamletArea > 0f) {
                Text(
                    text = "Samlet åbningsareal: ${String.format("%.2f", totalSamletArea)} m²",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Black,
                    color = Color.White,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White.copy(alpha = 0.35f), MaterialTheme.shapes.medium)
                        .padding(16.dp)
                )
            }
        }

        if (data.openingMode == "individuel") {
            LaunchedEffect(Unit) {
                if (localMeasurements.isEmpty()) {
                    localMeasurements.add(OpeningMeasurement())
                    syncToViewModel()
                }
            }

            val totalIndividualArea = localMeasurements.sumOf {
                (it.widthCm ?: 0f) * (it.heightCm ?: 0f) / 10000.0
            }.toFloat()

            localMeasurements.forEachIndexed { index, measurement ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OpeningMeasurementRow(
                        measurements = listOf(measurement),
                        onMeasurementsChange = { newList ->
                            localMeasurements = localMeasurements.toMutableList().apply {
                                set(index, newList.firstOrNull() ?: OpeningMeasurement())
                            }
                            syncToViewModel()
                        },
                        modifier = Modifier.weight(1f)
                    )

                    if (localMeasurements.size > 1) {
                        IconButton(onClick = {
                            localMeasurements.removeAt(index)
                            syncToViewModel()
                        }) {
                            Icon(Icons.Default.Delete, "Fjern åbning", tint = Color.White)
                        }
                    }
                }
            }

            OutlinedButton(
                onClick = {
                    localMeasurements.add(OpeningMeasurement())
                    syncToViewModel()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Tilføj ny åbning")
            }

            if (totalIndividualArea > 0f) {
                Text(
                    text = "Samlet åbningsareal: ${String.format("%.2f", totalIndividualArea)} m²",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Black,
                    color = Color.White,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White.copy(alpha = 0.35f), MaterialTheme.shapes.medium)
                        .padding(16.dp)
                )
            }
        }

        PhotoUploadSection(
            label = "Upload billeder af åbninger (valgfrit, anbefalet ved større projekter)",
            isRequired = false,
            currentUris = openingsPhotos,
            onUrisChange = { viewModel.updateStepPhotos("openings", it) }
        )
    }
}