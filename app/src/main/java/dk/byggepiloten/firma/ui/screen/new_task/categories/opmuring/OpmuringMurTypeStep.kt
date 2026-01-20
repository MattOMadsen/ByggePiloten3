// Fil: app/src/main/java/dk/byggepiloten/firma/ui/screen/new_task/categories/opmuring/OpmuringMurTypeStep.kt
// OPDATERET: Compile-fix – alle updateWallData → updateWallDataDirect
// - WizardStepTitle og custom-felt beholdt
// Total lines: ~100 (uændret)

package dk.byggepiloten.firma.ui.screen.new_task.categories.opmuring

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dk.byggepiloten.firma.ui.screen.new_task.components.common.ChoiceBox
import dk.byggepiloten.firma.ui.screen.new_task.components.common.StyledTextField
import dk.byggepiloten.firma.ui.screen.new_task.components.common.WizardStepTitle
import dk.byggepiloten.firma.ui.viewmodel.task.OpmuringTaskViewModel

private val murTypeOptions = listOf(
    "Facademur (skalmur/ydervæg)",
    "Indvendig væg",
    "Have-/ støttemur",
    "Andet"
)

@Composable
fun OpmuringMurTypeStep(
    viewModel: OpmuringTaskViewModel
) {
    val data by viewModel.wallData.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(32.dp)
    ) {
        WizardStepTitle(
            text = "Hvilken type mur skal opmures eller repareres?"
        )

        ChoiceBox(
            options = murTypeOptions,
            selectedOption = data.murType,
            onOptionSelected = { option ->
                viewModel.updateWallDataDirect(
                    data.copy(
                        murType = option,
                        customMurType = if (option == "Andet") data.customMurType else null
                    )
                )
            }
        )

        if (data.murType == "Andet") {
            StyledTextField(
                value = data.customMurType ?: "",
                onValueChange = { viewModel.updateWallDataDirect(data.copy(customMurType = it)) },
                label = "Beskriv hvilken type mur",
                singleLine = false
            )
        }
    }
}