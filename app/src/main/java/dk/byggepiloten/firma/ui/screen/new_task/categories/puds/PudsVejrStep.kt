// Fil: app/src/main/java/dk/byggepiloten/firma/ui/screen/new_task/categories/puds/PudsVejrStep.kt
// OPDATERET – bruger nu reusable WeatherExposureSection
// Step-filen er nu meget kortere

package dk.byggepiloten.firma.ui.screen.new_task.categories.puds

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dk.byggepiloten.firma.ui.screen.new_task.components.common.WeatherExposureSection
import dk.byggepiloten.firma.ui.viewmodel.task.PudsTaskViewModel

@Composable
fun PudsVejrStep(
    viewModel: PudsTaskViewModel
) {
    val data by viewModel.pudsData.collectAsStateWithLifecycle()
    val stepPhotos by viewModel.stepPhotos.collectAsStateWithLifecycle(emptyMap<String, List<android.net.Uri>>())

    WeatherExposureSection(
        vejretidspunkt = data.vejretidspunkt,
        photoKey = "vejr",
        currentPhotos = stepPhotos["vejr"] ?: emptyList(),
        onDataChanged = { viewModel.updatePudsData(data.copy(vejretidspunkt = it)) },
        onPhotosChanged = { viewModel.updateStepPhotos("vejr", it) }
    )
}