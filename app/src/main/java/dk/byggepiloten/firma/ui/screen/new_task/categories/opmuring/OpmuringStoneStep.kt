// Fil: app/src/main/java/dk/byggepiloten/firma/ui/screen/new_task/categories/opmuring/OpmuringStoneStep.kt
// OPDATERET: Compile-fix – alle updateWallData → updateWallDataDirect
// - ChoiceBoxColumn + conditional specialsten beholdt
// Total lines: ~150 (uændret)

package dk.byggepiloten.firma.ui.screen.new_task.categories.opmuring

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dk.byggepiloten.firma.ui.screen.new_task.components.common.ChoiceBoxColumn
import dk.byggepiloten.firma.ui.screen.new_task.components.common.ConditionalContent
import dk.byggepiloten.firma.ui.screen.new_task.components.common.StyledTextField
import dk.byggepiloten.firma.ui.viewmodel.task.OpmuringTaskViewModel

@Composable
fun OpmuringStoneStep(
    viewModel: OpmuringTaskViewModel
) {
    val data by viewModel.wallData.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text(
            text = "Sten type",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        Text(
            text = "Hvilken type sten skal muren bygges med?",
            style = MaterialTheme.typography.titleMedium,
            color = Color.White
        )

        val options = listOf(
            "Standard mursten (rød)",
            "Gule mursten",
            "Grå betonsten",
            "Letbetonsten",
            "Cellesten",
            "Special sten"
        )

        ChoiceBoxColumn(
            options = options,
            selectedOption = data.stoneType,
            onOptionSelected = {
                viewModel.updateWallDataDirect(
                    data.copy(
                        stoneType = it,
                        specialStoneName = if (it == "Special sten") data.specialStoneName else null,
                        specialStoneLink = if (it == "Special sten") data.specialStoneLink else null
                    )
                )
            }
        )

        ConditionalContent(visible = data.stoneType == "Special sten") {
            StyledTextField(
                value = data.specialStoneName ?: "",
                onValueChange = { viewModel.updateWallDataDirect(data.copy(specialStoneName = it)) },
                label = "Navn på special sten",
                singleLine = true
            )

            Spacer(Modifier.height(16.dp))

            StyledTextField(
                value = data.specialStoneLink ?: "",
                onValueChange = { viewModel.updateWallDataDirect(data.copy(specialStoneLink = it)) },
                label = "Link til sten (valgfrit – f.eks. leverandørs side)",
                keyboardType = KeyboardType.Uri,
                singleLine = true
            )

            Spacer(Modifier.height(16.dp))

            Text(
                text = "Linket hjælper håndværkeren med at se præcis hvilken sten du ønsker.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.9f)
            )
        }
    }
}