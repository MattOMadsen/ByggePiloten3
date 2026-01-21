// Fil: app/src/main/java/dk/byggepiloten/firma/ui/screen/new_task/categories/puds/PudsVaegtypeStep.kt
// OPDATERET – conditional tekstfelt ved "Anden", ens Card-struktur

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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dk.byggepiloten.firma.ui.screen.new_task.components.PhotoUploadSection
import dk.byggepiloten.firma.ui.screen.new_task.components.common.ChoiceBoxRow
import dk.byggepiloten.firma.ui.screen.new_task.components.common.StyledTextField
import dk.byggepiloten.firma.ui.viewmodel.task.PudsTaskViewModel

private val vaegTyper = listOf("Gasbeton", "Beton", "Mursten", "Letbeton", "Anden")

@Composable
fun PudsVaegtypeStep(
    viewModel: PudsTaskViewModel
) {
    val data by viewModel.pudsData.collectAsStateWithLifecycle()
    val stepPhotos by viewModel.stepPhotos.collectAsStateWithLifecycle(emptyMap<String, List<android.net.Uri>>())

    var customType by remember { mutableStateOf(data.andenVaegtype ?: "") }

    LaunchedEffect(data.vaegtype, customType) {
        val updated = data.copy(
            andenVaegtype = if (data.vaegtype == "Anden") customType.takeIf { it.isNotEmpty() } else null
        )
        if (updated != data) {
            viewModel.updatePudsData(updated)
        }
    }

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
                    options = vaegTyper,
                    selectedOption = data.vaegtype ?: "",
                    onOptionSelected = {
                        viewModel.updatePudsData(data.copy(vaegtype = it))
                    }
                )

                if (data.vaegtype == "Anden") {
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