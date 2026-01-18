// Fil: app/src/main/java/dk/byggepiloten/firma/ui/screen/new_task/categories/opmuring/OpmuringMortarStep.kt
// FULD OPDATERET – Ændret til viewModel-parameter
// Bind direkte til viewModel.updateWallData
// Layout uændret (ChoiceBox + custom mørtel)

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

private val mortarOptions = listOf("KC-mørtel", "Traditionel kalkmørtel", "Andet")

@Composable
fun OpmuringMortarStep(
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
            text = "Hvilken type mørtel foretrækkes?",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        ChoiceBox(
            options = mortarOptions,
            selectedOption = data.mortarType,
            onOptionSelected = { option ->
                viewModel.updateWallData(
                    data.copy(
                        mortarType = option,
                        customMortarType = if (option == "Andet") data.customMortarType else null
                    )
                )
            }
        )

        if (data.mortarType == "Andet") {
            StyledTextField(
                value = data.customMortarType ?: "",
                onValueChange = { viewModel.updateWallData(data.copy(customMortarType = it)) },
                label = "Beskriv mørteltype",
                singleLine = false
            )
        }
    }
}