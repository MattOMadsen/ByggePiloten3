// Fil: app/src/main/java/dk/byggepiloten/firma/ui/screen/new_task/categories/opmuring/OpmuringThicknessStep.kt
// FULD OPDATERET – Ændret til viewModel-parameter
// Bind direkte til viewModel.updateWallData
// Layout uændret (ChoiceBox + custom felt)

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
import dk.byggepiloten.firma.ui.screen.new_task.components.common.ChoiceBox
import dk.byggepiloten.firma.ui.screen.new_task.components.common.StyledTextField
import dk.byggepiloten.firma.ui.viewmodel.task.OpmuringTaskViewModel

private val thicknessOptions = listOf("108 mm (halvsten)", "228 mm (helsten)", "348 mm (1½ sten)", "Andet")

@Composable
fun OpmuringThicknessStep(
    viewModel: OpmuringTaskViewModel
) {
    val data by viewModel.wallData.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(32.dp)
    ) {
        Text(
            text = "Hvilken tykkelse skal muren have?",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        ChoiceBox(
            options = thicknessOptions,
            selectedOption = data.thicknessOption,
            onOptionSelected = { option ->
                viewModel.updateWallData(
                    data.copy(
                        thicknessOption = option,
                        customThickness = if (option == "Andet") data.customThickness else null
                    )
                )
            }
        )

        if (data.thicknessOption == "Andet") {
            StyledTextField(
                value = data.customThickness?.toString() ?: "",
                onValueChange = { value ->
                    viewModel.updateWallData(data.copy(customThickness = value.toIntOrNull()))
                },
                label = "Angiv tykkelse i mm",
                keyboardType = androidx.compose.ui.text.input.KeyboardType.Number,
                singleLine = true
            )
        }
    }
}