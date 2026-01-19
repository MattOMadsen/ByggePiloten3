// Fil: app/src/main/java/dk/byggepiloten/firma/ui/screen/new_task/categories/opmuring/OpmuringStoneStep.kt
// OPDATERET: Fjernet ekstra outer Column + verticalScroll (håndteres nu i WizardScaffold)
// Conditional felter synlige uden scroll-problem
// Commit: StoneStep uden lokal scroll – bruger Scaffold's scroll (fix visibility + crash)

package dk.byggepiloten.firma.ui.screen.new_task.categories.opmuring

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dk.byggepiloten.firma.ui.screen.new_task.components.common.ChoiceBox
import dk.byggepiloten.firma.ui.screen.new_task.components.common.ConditionalContent
import dk.byggepiloten.firma.ui.screen.new_task.components.common.StyledTextField
import dk.byggepiloten.firma.ui.screen.new_task.components.common.WizardStepContainer
import dk.byggepiloten.firma.ui.viewmodel.task.OpmuringTaskViewModel

private val stoneOptions = listOf(
    "Standard mursten (rød)",
    "Gule mursten",
    "Grå betonsten",
    "Letbetonsten",
    "Special sten"
)

@Composable
fun OpmuringStoneStep(
    viewModel: OpmuringTaskViewModel
) {
    val data by viewModel.wallData.collectAsStateWithLifecycle()

    WizardStepContainer(
        title = "Hvilken type sten skal bruges?"
    ) {
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

        ConditionalContent(visible = data.stoneType == "Special sten") {
            StyledTextField(
                value = data.specialStoneName ?: "",
                onValueChange = { viewModel.updateWallData(data.copy(specialStoneName = it)) },
                label = "Navn på special sten",
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            StyledTextField(
                value = data.specialStoneLink ?: "",
                onValueChange = { viewModel.updateWallData(data.copy(specialStoneLink = it)) },
                label = "Link til sten (valgfrit – f.eks. leverandørs side)",
                keyboardType = KeyboardType.Uri,
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Linket hjælper håndværkeren med at se præcis hvilken sten du ønsker.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.9f)
            )
        }
    }
}