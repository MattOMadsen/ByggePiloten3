// Fil: app/src/main/java/dk/byggepiloten/firma/ui/screen/new_task/categories/opmuring/OpmuringSurfaceStep.kt
// FULD OPDATERET – Ændret til viewModel-parameter
// Bind direkte til viewModel.updateWallData
// Layout uændret (ChoiceBox + custom overflade)

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

private val surfaceOptions = listOf("Pudset", "Blank mur", "Filt/glasvæv", "Andet")

@Composable
fun OpmuringSurfaceStep(
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
            text = "Ønsket overfladebehandling?",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        ChoiceBox(
            options = surfaceOptions,
            selectedOption = data.surfaceFinish,
            onOptionSelected = { option ->
                viewModel.updateWallData(
                    data.copy(
                        surfaceFinish = option,
                        customSurface = if (option == "Andet") data.customSurface else null
                    )
                )
            }
        )

        if (data.surfaceFinish == "Andet") {
            StyledTextField(
                value = data.customSurface ?: "",
                onValueChange = { viewModel.updateWallData(data.copy(customSurface = it)) },
                label = "Beskriv ønsket overflade",
                keyboardType = androidx.compose.ui.text.input.KeyboardType.Text,
                singleLine = false
            )
        }
    }
}