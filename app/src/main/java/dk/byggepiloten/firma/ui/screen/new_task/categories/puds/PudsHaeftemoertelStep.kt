// Fil: app/src/main/java/dk/byggepiloten/firma/ui/screen/new_task/categories/puds/PudsHaeftemoertelStep.kt
// OPDATERET – bruger nu reusable HaeftemoertelSection
// Step-filen er nu meget kortere

package dk.byggepiloten.firma.ui.screen.new_task.categories.puds

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dk.byggepiloten.firma.ui.screen.new_task.components.common.HaeftemoertelSection
import dk.byggepiloten.firma.ui.viewmodel.task.PudsTaskViewModel

@Composable
fun PudsHaeftemoertelStep(
    viewModel: PudsTaskViewModel
) {
    val data by viewModel.pudsData.collectAsStateWithLifecycle()
    val stepPhotos by viewModel.stepPhotos.collectAsStateWithLifecycle(emptyMap<String, List<android.net.Uri>>())

    HaeftemoertelSection(
        haeftemoertelType = data.haeftemoertelType,
        andenHaeftemoertel = data.andenHaeftemoertel,
        durapudsFarve = data.durapudsFarve,
        skalcemFarve = data.skalcemFarve,
        photoKey = "haeftemoertel",
        currentPhotos = stepPhotos["haeftemoertel"] ?: emptyList(),
        onDataChanged = { key, value ->
            when (key) {
                "haeftemoertelType" -> viewModel.updatePudsData(data.copy(haeftemoertelType = value))
                "andenHaeftemoertel" -> viewModel.updatePudsData(data.copy(andenHaeftemoertel = value))
                "durapudsFarve" -> viewModel.updatePudsData(data.copy(durapudsFarve = value))
                "skalcemFarve" -> viewModel.updatePudsData(data.copy(skalcemFarve = value))
            }
        },
        onPhotosChanged = { viewModel.updateStepPhotos("haeftemoertel", it) }
    )
}