// Fil: app/src/main/java/dk/byggepiloten/firma/ui/screen/new_task/categories/opmuring/OpmuringThicknessStep.kt
// OPDATERET: Compile-fix – alle updateWallData → updateWallDataDirect
// - Custom felt + keyboardType beholdt
// Total lines: ~100 (uændret)

package dk.byggepiloten.firma.ui.screen.new_task.categories.opmuring

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
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
                viewModel.updateWallDataDirect(
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
                    viewModel.updateWallDataDirect(data.copy(customThickness = value.toIntOrNull()))
                },
                label = "Angiv tykkelse i mm",
                keyboardType = KeyboardType.Number,
                singleLine = true
            )
        }
    }
}