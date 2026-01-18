// Fil: app/src/main/java/dk/byggepiloten/firma/ui/screen/new_task/categories/opmuring/OpmuringAccessStep.kt
// FULD OPDATERET – Ændret til viewModel-parameter
// Bind direkte til viewModel.updateWallData og updateStepPhotos("access")
// ChoiceBox + MultiChoiceBox + conditional custom + PhotoUploadSection
// Layout uændret

package dk.byggepiloten.firma.ui.screen.new_task.categories.opmuring

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dk.byggepiloten.firma.data.model.task.WallData
import dk.byggepiloten.firma.ui.screen.new_task.components.PhotoUploadSection
import dk.byggepiloten.firma.ui.screen.new_task.components.common.ChoiceBox
import dk.byggepiloten.firma.ui.screen.new_task.components.common.MultiChoiceBox
import dk.byggepiloten.firma.ui.screen.new_task.components.common.StyledTextField
import dk.byggepiloten.firma.ui.viewmodel.task.OpmuringTaskViewModel

private val accessOptions = listOf("Ja", "Nej")
private val problemOptions = listOf("Trange adgangsforhold", "Højt oppe", "Andre forhindringer")

@Composable
fun OpmuringAccessStep(
    viewModel: OpmuringTaskViewModel
) {
    val data by viewModel.wallData.collectAsStateWithLifecycle()
    val accessPhotos = viewModel.stepPhotos.collectAsState().value["access"] ?: emptyList()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(40.dp)
    ) {
        Text(
            text = "Er der god adgang til arbejdsområdet?",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        ChoiceBox(
            options = accessOptions,
            selectedOption = if (data.goodAccess == true) "Ja" else if (data.goodAccess == false) "Nej" else null,
            onOptionSelected = { option ->
                viewModel.updateWallData(
                    data.copy(
                        goodAccess = option == "Ja",
                        accessProblems = if (option == "Ja") emptyList() else data.accessProblems,
                        accessCustomDescription = if (option == "Ja") null else data.accessCustomDescription
                    )
                )
            }
        )

        if (data.goodAccess == false) {
            MultiChoiceBox(
                options = problemOptions,
                selectedOptions = data.accessProblems,
                onOptionsChange = { viewModel.updateWallData(data.copy(accessProblems = it)) }
            )

            if (data.accessProblems.isNotEmpty()) {
                StyledTextField(
                    value = data.accessCustomDescription ?: "",
                    onValueChange = { viewModel.updateWallData(data.copy(accessCustomDescription = it)) },
                    label = "Yderligere beskrivelse af adgangsproblemer",
                    singleLine = false
                )
            }

            PhotoUploadSection(
                label = "Upload billeder af adgangsforhold",
                isRequired = data.accessProblems.isNotEmpty(),
                currentUris = accessPhotos,
                onUrisChange = { viewModel.updateStepPhotos("access", it) }
            )
        }
    }
}