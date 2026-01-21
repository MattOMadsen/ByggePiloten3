// Fil: app/src/main/java/dk/byggepiloten/firma/ui/screen/new_task/categories/puds/PudsArmeringsisoleringStep.kt
// OPDATERET – bruger nu reusable InsulationArmeringSection
// Step-filen er nu meget kortere

package dk.byggepiloten.firma.ui.screen.new_task.categories.puds

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dk.byggepiloten.firma.ui.screen.new_task.components.common.InsulationArmeringSection
import dk.byggepiloten.firma.ui.viewmodel.task.PudsTaskViewModel

@Composable
fun PudsArmeringsisoleringStep(
    viewModel: PudsTaskViewModel
) {
    val data by viewModel.pudsData.collectAsStateWithLifecycle()
    val stepPhotos by viewModel.stepPhotos.collectAsStateWithLifecycle(emptyMap<String, List<android.net.Uri>>())

    var customIsolering by remember { mutableStateOf(data.isoleringType ?: "") }

    InsulationArmeringSection(
        vaegtype = data.vaegtype,
        armeringsnet = data.armeringsnet,
        isolering = data.isolering,
        isoleringType = data.isoleringType,
        customIsolering = customIsolering,
        photoKey = "isolering",
        currentPhotos = stepPhotos["isolering"] ?: emptyList(),
        onDataChanged = { key, value ->
            when (key) {
                "armeringsnet" -> viewModel.updatePudsData(data.copy(armeringsnet = value as String?))
                "isolering" -> viewModel.updatePudsData(data.copy(isolering = value as String?))
                "isoleringType" -> viewModel.updatePudsData(data.copy(isoleringType = value as String?))
                "customIsolering" -> {
                    customIsolering = value as String
                    if (data.isolering == "Ja" && data.isoleringType == "Anden") {
                        viewModel.updatePudsData(data.copy(isoleringType = value as String?))
                    }
                }
            }
        },
        onPhotosChanged = { viewModel.updateStepPhotos("isolering", it) }
    )
}