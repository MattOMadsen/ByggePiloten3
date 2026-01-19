// Fil: app/src/main/java/dk/byggepiloten/firma/ui/screen/new_task/categories/opmuring/OpmuringFoundationStep.kt
// OPDATERET: Bruger nu reusable ChoiceBoxColumn
// - Valg vertikalt med samme styling som andre steps
// - Conditional "Andet" felt beholdt

package dk.byggepiloten.firma.ui.screen.new_task.categories.opmuring

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dk.byggepiloten.firma.ui.screen.new_task.components.PhotoUploadSection
import dk.byggepiloten.firma.ui.screen.new_task.components.common.ChoiceBoxColumn
import dk.byggepiloten.firma.ui.screen.new_task.components.common.StyledTextField
import dk.byggepiloten.firma.ui.viewmodel.task.OpmuringTaskViewModel

@Composable
fun OpmuringFoundationStep(
    viewModel: OpmuringTaskViewModel
) {
    val data by viewModel.wallData.collectAsStateWithLifecycle()
    val stepPhotos by viewModel.stepPhotos.collectAsStateWithLifecycle()
    val foundationPhotos = stepPhotos["foundation"] ?: emptyList()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text(
            text = "Fundament",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        Text(
            text = "Hvilket fundament skal muren stå på?",
            style = MaterialTheme.typography.titleMedium,
            color = Color.White
        )

        val options = listOf("Eksisterende fundament", "Nyt fundament støbt", "Andet")

        ChoiceBoxColumn(
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
            StyledTextField(
                value = data.customFoundation ?: "",
                onValueChange = { viewModel.updateWallData(data.copy(customFoundation = it)) },
                label = "Beskriv dit fundament nærmere",
                singleLine = false
            )
        }

        PhotoUploadSection(
            label = "Upload billeder af fundamentet (anbefalet for præcist estimat)",
            isRequired = false,
            currentUris = foundationPhotos,
            onUrisChange = { viewModel.updateStepPhotos("foundation", it) }
        )
    }
}