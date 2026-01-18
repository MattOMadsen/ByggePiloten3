// Fil: app/src/main/java/dk/byggepiloten/firma/ui/screen/new_task/categories/opmuring/OpmuringStoneStep.kt
// FULD OPDATERET – Ændret til viewModel-parameter
// Bind direkte til viewModel.updateWallData
// Layout uændret (ChoiceBox + specialsten felter)

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

private val stoneOptions = listOf(
    "Teglsten",
    "Gasbeton",
    "Leca-blokke",
    "Cellesten",
    "Special sten"
)

@Composable
fun OpmuringStoneStep(
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
            text = "Hvilken type sten skal bruges?",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        ChoiceBox(
            options = stoneOptions,
            selectedOption = data.stoneType,
            onOptionSelected = { option ->
                viewModel.updateWallData(
                    data.copy(
                        stoneType = option,
                        specialStoneName = if (option == "Special sten") data.specialStoneName else null,
                        specialStoneLink = if (option == "Special sten") data.specialStoneLink else null
                    )
                )
            }
        )

        if (data.stoneType == "Special sten") {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                StyledTextField(
                    value = data.specialStoneName ?: "",
                    onValueChange = { viewModel.updateWallData(data.copy(specialStoneName = it)) },
                    label = "Navn på specialsten (obligatorisk)",
                    singleLine = true
                )

                StyledTextField(
                    value = data.specialStoneLink ?: "",
                    onValueChange = { viewModel.updateWallData(data.copy(specialStoneLink = it)) },
                    label = "Link til specialsten (valgfrit)",
                    singleLine = true
                )
            }
        }
    }
}