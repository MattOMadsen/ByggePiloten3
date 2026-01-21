// Fil: app/src/main/java/dk/byggepiloten/firma/ui/screen/new_task/categories/puds/PudsHaeftemoertelStep.kt
// RETTET – Bruger nu ChoiceBoxRow (ingen RadioButton) + conditional felter ved valg
// stepPhotos-type tvunget

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

private val haeftemoertelTyper = listOf("DuraPuds 615", "Skalcem S2000", "Anden")

@Composable
fun PudsHaeftemoertelStep(
    viewModel: PudsTaskViewModel
) {
    val pudsData by viewModel.pudsData.collectAsStateWithLifecycle()
    val stepPhotos by viewModel.stepPhotos.collectAsStateWithLifecycle(emptyMap<String, List<android.net.Uri>>())

    var customType by remember { mutableStateOf(pudsData.andenHaeftemoertel ?: "") }
    var durapudsFarve by remember { mutableStateOf(pudsData.durapudsFarve ?: "") }
    var skalcemFarve by remember { mutableStateOf(pudsData.skalcemFarve ?: "") }

    LaunchedEffect(pudsData.haeftemoertelType, customType, durapudsFarve, skalcemFarve) {
        val updated = pudsData.copy(
            haeftemoertelType = pudsData.haeftemoertelType,
            andenHaeftemoertel = if (pudsData.haeftemoertelType == "Anden") customType.takeIf { it.isNotEmpty() } else null,
            durapudsFarve = if (pudsData.haeftemoertelType == "DuraPuds 615") durapudsFarve.takeIf { it.isNotEmpty() } else null,
            skalcemFarve = if (pudsData.haeftemoertelType == "Skalcem S2000") skalcemFarve.takeIf { it.isNotEmpty() } else null
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
            "Hvilken hæftemørtel skal bruges?",
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
                    options = haeftemoertelTyper,
                    selectedOption = pudsData.haeftemoertelType ?: "",
                    onOptionSelected = {
                        viewModel.updatePudsData(pudsData.copy(haeftemoertelType = it))
                    }
                )

                if (pudsData.haeftemoertelType == "Anden") {
                    Spacer(Modifier.height(8.dp))
                    StyledTextField(
                        value = customType,
                        onValueChange = { customType = it },
                        label = "Beskriv hæftemørtel"
                    )
                }

                if (pudsData.haeftemoertelType == "DuraPuds 615") {
                    Spacer(Modifier.height(8.dp))
                    StyledTextField(
                        value = durapudsFarve,
                        onValueChange = { durapudsFarve = it },
                        label = "Ønsket farve (DuraPuds)"
                    )
                }

                if (pudsData.haeftemoertelType == "Skalcem S2000") {
                    Spacer(Modifier.height(8.dp))
                    StyledTextField(
                        value = skalcemFarve,
                        onValueChange = { skalcemFarve = it },
                        label = "Ønsket farve (Skalcem)"
                    )
                }
            }
        }

        PhotoUploadSection(
            label = "Billeder af eksisterende puds (valgfrit)",
            isRequired = false,
            currentUris = stepPhotos["haeftemoertel"] ?: emptyList(),
            onUrisChange = { viewModel.updateStepPhotos("haeftemoertel", it) }
        )
    }
}