// Fil: app/src/main/java/dk/byggepiloten/firma/ui/screen/new_task/categories/opmuring/OpmuringInsulationStep.kt
// OPDATERET: Compile-fix – alle updateWallData → updateWallDataDirect
// - Ja/Nej + tykkelse-felt beholdt
// Total lines: ~110 (uændret)

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

private val options = listOf("Ja", "Nej")

@Composable
fun OpmuringInsulationStep(
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
            text = "Ønskes isolering i muren?",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        ChoiceBox(
            options = options,
            selectedOption = if (data.insulationWanted == true) "Ja" else if (data.insulationWanted == false) "Nej" else null,
            onOptionSelected = { option ->
                viewModel.updateWallDataDirect(
                    data.copy(
                        insulationWanted = option == "Ja",
                        insulationThickness = if (option == "Nej") null else data.insulationThickness
                    )
                )
            }
        )

        if (data.insulationWanted == true) {
            StyledTextField(
                value = data.insulationThickness?.toString() ?: "",
                onValueChange = { value ->
                    viewModel.updateWallDataDirect(data.copy(insulationThickness = value.toFloatOrNull()))
                },
                label = "Isoleringstykkelse (cm)",
                keyboardType = KeyboardType.Decimal,
                singleLine = true
            )
        }
    }
}