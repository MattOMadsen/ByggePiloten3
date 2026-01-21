// Fil: app/src/main/java/dk/byggepiloten/firma/ui/screen/new_task/categories/puds/PudsArmeringsisoleringStep.kt
// RETTET – ChoiceBoxRow overalt, stepPhotos-type tvunget

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

private val isoleringTyper = listOf("Mineraluld", "EPS", "Anden")

@Composable
fun PudsArmeringsisoleringStep(
    viewModel: PudsTaskViewModel
) {
    val pudsData by viewModel.pudsData.collectAsStateWithLifecycle()
    val stepPhotos by viewModel.stepPhotos.collectAsStateWithLifecycle(emptyMap<String, List<android.net.Uri>>())

    var customIsolering by remember { mutableStateOf(pudsData.isoleringType ?: "") }

    LaunchedEffect(pudsData.armeringsnet, pudsData.isolering, pudsData.isoleringType, customIsolering) {
        val updated = pudsData.copy(
            isoleringType = if (pudsData.isolering == "Ja" && pudsData.isoleringType == "Anden") customIsolering.takeIf { it.isNotEmpty() } else pudsData.isoleringType
        )
        if (updated != pudsData) {
            viewModel.updatePudsData(updated)
        }
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text(
            "Armering og isolering",
            style = MaterialTheme.typography.titleLarge,
            color = Color.Black
        )

        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.95f)),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                ChoiceBoxRow(
                    label = "Skal der armeringsnet?",
                    options = listOf("Ja", "Nej"),
                    selectedOption = pudsData.armeringsnet ?: "",
                    onOptionSelected = {
                        viewModel.updatePudsData(pudsData.copy(armeringsnet = it))
                    }
                )

                if (pudsData.vaegtype == "Mursten") {
                    Text(
                        "Ved mursten anbefales armeringsnet",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Black.copy(alpha = 0.7f),
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                Spacer(Modifier.height(16.dp))

                ChoiceBoxRow(
                    label = "Skal der isoleres?",
                    options = listOf("Ja", "Nej"),
                    selectedOption = pudsData.isolering ?: "",
                    onOptionSelected = {
                        viewModel.updatePudsData(pudsData.copy(isolering = it))
                    }
                )

                if (pudsData.isolering == "Ja") {
                    Spacer(Modifier.height(16.dp))

                    ChoiceBoxRow(
                        label = "Vælg isoleringstype",
                        options = isoleringTyper,
                        selectedOption = pudsData.isoleringType ?: "",
                        onOptionSelected = {
                            viewModel.updatePudsData(pudsData.copy(isoleringType = it))
                        }
                    )

                    if (pudsData.isoleringType == "Anden") {
                        Spacer(Modifier.height(12.dp))
                        StyledTextField(
                            value = customIsolering,
                            onValueChange = { customIsolering = it },
                            label = "Beskriv isoleringstype"
                        )
                    }
                }
            }
        }

        PhotoUploadSection(
            label = "Billeder af væg/underlag (valgfrit)",
            isRequired = false,
            currentUris = stepPhotos["isolering"] ?: emptyList(),
            onUrisChange = { viewModel.updateStepPhotos("isolering", it) }
        )
    }
}