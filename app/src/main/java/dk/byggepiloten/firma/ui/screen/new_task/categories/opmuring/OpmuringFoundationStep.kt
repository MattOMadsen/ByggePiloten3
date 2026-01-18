// Fil: app/src/main/java/dk/byggepiloten/firma/ui/screen/new_task/categories/opmuring/OpmuringFoundationStep.kt
// FULD OPDATERET – Ændret til viewModel-parameter
// Bind direkte til viewModel.updateWallData
// Layout uændret (ChoiceBox + custom fundament)

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

private val foundationOptions = listOf("Eksisterende fundament", "Nyt fundament", "Andet")

@Composable
fun OpmuringFoundationStep(
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
            text = "Hvad med fundamentet?",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        ChoiceBox(
            options = foundationOptions,
            selectedOption = data.foundationOption,
            onOptionSelected = { option ->
                viewModel.updateWallData(
                    data.copy(
                        foundationOption = option,
                        customFoundation = if (option == "Andet") data.customFoundation else null
                    )
                )
            }
        )

        if (data.foundationOption == "Andet") {
            StyledTextField(
                value = data.customFoundation ?: "",
                onValueChange = { viewModel.updateWallData(data.copy(customFoundation = it)) },
                label = "Beskriv fundament",
                singleLine = false
            )
        }
    }
}