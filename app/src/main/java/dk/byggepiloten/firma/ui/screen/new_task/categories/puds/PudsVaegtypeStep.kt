// Fil: app/src/main/java/dk/byggepiloten/firma/ui/screen/new_task/categories/puds/PudsVaegtypeStep.kt
// RETTET – Bruger nu ChoiceBoxRow (som opmuring) + custom tekstfelt ved "Anden"
// stepPhotos-type tvunget for at undgå delegation-fejl

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

private val vaegTyper = listOf("Gasbeton", "Beton", "Mursten", "Letbeton", "Anden")

/**
 * Trin 3 i puds-wizarden: Valg af vægtype/underlag.
 * Ved "Anden" vises tekstfelt til fri beskrivelse – præcis som i opmuring.
 */
@Composable
fun PudsVaegtypeStep(
    viewModel: PudsTaskViewModel
) {
    val pudsData by viewModel.pudsData.collectAsStateWithLifecycle()
    val stepPhotos by viewModel.stepPhotos.collectAsStateWithLifecycle(emptyMap<String, List<android.net.Uri>>())

    var customType by remember { mutableStateOf(pudsData.andenVaegtype ?: "") }

    LaunchedEffect(pudsData.vaegtype, customType) {
        val updated = pudsData.copy(
            vaegtype = pudsData.vaegtype,
            andenVaegtype = if (pudsData.vaegtype == "Anden") customType.takeIf { it.isNotEmpty() } else null
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
            text = "Hvilken type væg/underlag?",
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
                    options = vaegTyper,
                    selectedOption = pudsData.vaegtype ?: "",
                    onOptionSelected = {
                        viewModel.updatePudsData(pudsData.copy(vaegtype = it))
                    }
                )

                if (pudsData.vaegtype == "Anden") {
                    Spacer(Modifier.height(12.dp))
                    StyledTextField(
                        value = customType,
                        onValueChange = { customType = it },
                        label = "Beskriv vægtype"
                    )
                }
            }
        }

        PhotoUploadSection(
            label = "Billeder af væg/underlag (anbefalet)",
            isRequired = false,
            currentUris = stepPhotos["vaegtype"] ?: emptyList(),
            onUrisChange = { viewModel.updateStepPhotos("vaegtype", it) }
        )
    }
}