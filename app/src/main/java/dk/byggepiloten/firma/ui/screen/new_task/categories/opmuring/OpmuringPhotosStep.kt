// Fil: app/src/main/java/dk/byggepiloten/firma/ui/screen/new_task/categories/opmuring/OpmuringPhotosStep.kt
// FULD RETTET – Tilføjet import androidx.compose.runtime.getValue
// forEach rettet til entries + explicit key/value
// PhotoUploadSection kald inde i Column

package dk.byggepiloten.firma.ui.screen.new_task.categories.opmuring

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dk.byggepiloten.firma.ui.screen.new_task.components.PhotoUploadSection
import dk.byggepiloten.firma.ui.viewmodel.task.OpmuringTaskViewModel

@Composable
fun OpmuringPhotosStep(
    viewModel: OpmuringTaskViewModel
) {
    val generalUris by viewModel.imageUris.collectAsStateWithLifecycle()
    val stepPhotos by viewModel.stepPhotos.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(40.dp)
    ) {
        Text(
            text = "Upload billeder af opgaven",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        PhotoUploadSection(
            label = "Generelle billeder (mindst ét påkrævet)",
            isRequired = true,
            currentUris = generalUris,
            onUrisChange = { viewModel.updateImages(it) }
        )

        if (stepPhotos.isNotEmpty()) {
            Text(
                text = "Allerede uploadede trin-billeder",
                style = MaterialTheme.typography.titleLarge,
                color = Color.White.copy(alpha = 0.9f)
            )

            stepPhotos.entries.forEach { entry ->
                val stepId = entry.key
                val uris = entry.value
                val label = when (stepId) {
                    "damage" -> "Skader"
                    "access" -> "Adgangsforhold"
                    "openings" -> "Åbninger"
                    else -> stepId
                }
                PhotoUploadSection(
                    label = label,
                    isRequired = false,
                    currentUris = uris,
                    onUrisChange = { viewModel.updateStepPhotos(stepId, it) }
                )
            }
        }
    }
}