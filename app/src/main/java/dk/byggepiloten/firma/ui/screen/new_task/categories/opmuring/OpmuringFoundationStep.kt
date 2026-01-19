// Fil: app/src/main/java/dk/byggepiloten/firma/ui/screen/new_task/categories/opmuring/OpmuringFoundationStep.kt
// OPDATERET: Skiftet til viewModel-signatur (live data via collectAsStateWithLifecycle)
// Photos hentes og opdateres via viewModel.stepPhotos / updateStepPhotos("foundation", ...)
// Beholdt fuld logik: ChoiceBoxRow + conditional "Andet" + PhotoUploadSection
// Commit: Konsistent viewModel-signatur for FoundationStep + live photos
// Baseret på repo-version (102 linjer) → nu ca. 105 linjer

package dk.byggepiloten.firma.ui.screen.new_task.categories.opmuring

import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dk.byggepiloten.firma.data.model.task.WallData
import dk.byggepiloten.firma.ui.screen.new_task.components.PhotoUploadSection
import dk.byggepiloten.firma.ui.screen.new_task.components.common.ChoiceBoxRow
import dk.byggepiloten.firma.ui.screen.new_task.components.common.StyledTextField
import dk.byggepiloten.firma.ui.viewmodel.task.OpmuringTaskViewModel

@Composable
fun OpmuringFoundationStep(
    viewModel: OpmuringTaskViewModel
) {
    val data by viewModel.wallData.collectAsStateWithLifecycle()
    val stepPhotos by viewModel.stepPhotos.collectAsStateWithLifecycle()
    val foundationPhotos = stepPhotos["foundation"] ?: emptyList()

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Hvilket fundament skal muren stå på?",
            style = MaterialTheme.typography.titleMedium,
            color = Color.White,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        val options = listOf("Eksisterende fundament", "Nyt fundament støbt", "Andet")

        ChoiceBoxRow(
            options = options,
            selectedOption = data.foundationOption,
            onOptionSelected = {
                viewModel.updateWallData(
                    data.copy(
                        foundationOption = it,
                        customFoundation = if (it != "Andet") null else data.customFoundation
                    )
                )
            }
        )

        if (data.foundationOption == "Andet") {
            Spacer(Modifier.height(24.dp))
            StyledTextField(
                value = data.customFoundation ?: "",
                onValueChange = { viewModel.updateWallData(data.copy(customFoundation = it)) },
                label = "Beskriv fundament"
            )
        }

        Spacer(Modifier.height(32.dp))

        PhotoUploadSection(
            label = "Upload billeder af fundamentet (anbefalet)",
            isRequired = false,
            currentUris = foundationPhotos,
            onUrisChange = { viewModel.updateStepPhotos("foundation", it) }
        )
    }
}