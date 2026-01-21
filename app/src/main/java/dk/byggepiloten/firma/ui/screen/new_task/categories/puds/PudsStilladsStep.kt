// Fil: app/src/main/java/dk/byggepiloten/firma/ui/screen/new_task/categories/puds/PudsStilladsStep.kt
// OPDATERET – bruger nu reusable AccessConditionsSection
// Step-filen er nu meget kortere og ensartet

package dk.byggepiloten.firma.ui.screen.new_task.categories.puds

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dk.byggepiloten.firma.ui.screen.new_task.components.common.AccessConditionsSection
import dk.byggepiloten.firma.ui.viewmodel.task.PudsTaskViewModel

@Composable
fun PudsStilladsStep(
    viewModel: PudsTaskViewModel
) {
    val data by viewModel.pudsData.collectAsStateWithLifecycle()
    val stepPhotos by viewModel.stepPhotos.collectAsStateWithLifecycle(emptyMap<String, List<android.net.Uri>>())

    AccessConditionsSection(
        title = "Stillads og adgangsforhold",
        stilladsNoedvendigt = data.stilladsNoedvendigt,
        adgang = data.stilladsAdgang,
        trapper = data.stilladsTrapper,
        photoKey = "stillads",
        currentPhotos = stepPhotos["stillads"] ?: emptyList(),
        requiredPhotos = true, // Obligatorisk billeder ved "Ja"
        onDataChanged = { key, value ->
            when (key) {
                "stilladsNoedvendigt" -> viewModel.updatePudsData(data.copy(stilladsNoedvendigt = value as String?))
                "stilladsAdgang" -> viewModel.updatePudsData(data.copy(stilladsAdgang = value as String?))
                "stilladsTrapper" -> viewModel.updatePudsData(data.copy(stilladsTrapper = value as String?))
            }
        },
        onPhotosChanged = { viewModel.updateStepPhotos("stillads", it) }
    )
}