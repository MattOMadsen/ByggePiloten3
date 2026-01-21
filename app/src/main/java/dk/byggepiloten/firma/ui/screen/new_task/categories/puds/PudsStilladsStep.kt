// Fil: app/src/main/java/dk/byggepiloten/firma/ui/screen/new_task/categories/puds/PudsStilladsStep.kt
// RETTET – ChoiceBoxRow overalt (ingen RadioButton), stepPhotos-type tvunget

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
import dk.byggepiloten.firma.ui.screen.new_task.components.common.StyledTextField
import dk.byggepiloten.firma.ui.viewmodel.task.PudsTaskViewModel

@Composable
fun PudsStilladsStep(
    viewModel: PudsTaskViewModel
) {
    val pudsData by viewModel.pudsData.collectAsStateWithLifecycle()
    val stepPhotos by viewModel.stepPhotos.collectAsStateWithLifecycle(emptyMap<String, List<android.net.Uri>>())

    var adgangText by remember { mutableStateOf(pudsData.stilladsAdgang ?: "") }
    var trapperText by remember { mutableStateOf(pudsData.stilladsTrapper ?: "") }

    LaunchedEffect(pudsData.stilladsNoedvendigt, adgangText, trapperText) {
        if (pudsData.stilladsNoedvendigt == "Ja") {
            viewModel.updatePudsData(pudsData.copy(
                stilladsAdgang = adgangText.takeIf { it.isNotEmpty() },
                stilladsTrapper = trapperText.takeIf { it.isNotEmpty() }
            ))
        } else {
            viewModel.updatePudsData(pudsData.copy(
                stilladsAdgang = null,
                stilladsTrapper = null
            ))
        }
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text(
            "Stillads og adgangsforhold",
            style = MaterialTheme.typography.titleLarge,
            color = Color.Black
        )

        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.95f)),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                ChoiceBoxRow(
                    label = "Er der behov for stillads?",
                    options = listOf("Ja", "Nej"),
                    selectedOption = pudsData.stilladsNoedvendigt ?: "",
                    onOptionSelected = {
                        viewModel.updatePudsData(pudsData.copy(stilladsNoedvendigt = it))
                    }
                )

                if (pudsData.stilladsNoedvendigt == "Ja") {
                    Spacer(Modifier.height(16.dp))
                    StyledTextField(
                        value = adgangText,
                        onValueChange = { adgangText = it },
                        label = "Beskriv adgangsforhold (f.eks. via gård, altan osv.)"
                    )
                    Spacer(Modifier.height(12.dp))
                    StyledTextField(
                        value = trapperText,
                        onValueChange = { trapperText = it },
                        label = "Beskriv trapper/elevator (hvis relevant)"
                    )
                }
            }
        }

        PhotoUploadSection(
            label = "Billeder af adgang/stilladsbehov (anbefalet)",
            isRequired = false,
            currentUris = stepPhotos["stillads"] ?: emptyList(),
            onUrisChange = { viewModel.updateStepPhotos("stillads", it) }
        )
    }
}