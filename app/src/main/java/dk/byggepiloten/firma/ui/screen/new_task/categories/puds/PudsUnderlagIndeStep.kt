// Fil: app/src/main/java/dk/byggepiloten/firma/ui/screen/new_task/categories/puds/PudsUnderlagIndeStep.kt
// OPDATERET – bruger nu reusable DamageAssessmentSection (showOldPuds = false)
// Step-filen er nu meget kortere

package dk.byggepiloten.firma.ui.screen.new_task.categories.puds

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dk.byggepiloten.firma.ui.screen.new_task.components.common.DamageAssessmentSection
import dk.byggepiloten.firma.ui.viewmodel.task.PudsTaskViewModel

@Composable
fun PudsUnderlagIndeStep(
    viewModel: PudsTaskViewModel
) {
    val data by viewModel.pudsData.collectAsStateWithLifecycle()
    val stepPhotos by viewModel.stepPhotos.collectAsStateWithLifecycle(emptyMap<String, List<android.net.Uri>>())

    DamageAssessmentSection(
        title = "Hvordan er underlaget?",
        showOldPuds = false,
        revner = data.underlagRevner,
        fugt = data.underlagFugt,
        gammelPuds = null,
        photoKey = "underlag_inde",
        currentPhotos = stepPhotos["underlag_inde"] ?: emptyList(),
        onDataChanged = { key, value ->
            when (key) {
                "underlagRevner" -> viewModel.updatePudsData(data.copy(underlagRevner = value as String?))
                "underlagFugt" -> viewModel.updatePudsData(data.copy(underlagFugt = value as String?))
            }
        },
        onPhotosChanged = { viewModel.updateStepPhotos("underlag_inde", it) }
    )
}