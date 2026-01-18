// Fil: app/src/main/java/dk/byggepiloten/firma/ui/screen/new_task/categories/opmuring/OpmuringBearingWallStep.kt
// FULD OPDATERET – Ændret til viewModel-parameter
// Bind direkte til viewModel.updateWallData
// Layout uændret (ChoiceBox Ja/Nej)

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
import dk.byggepiloten.firma.ui.viewmodel.task.OpmuringTaskViewModel

private val options = listOf("Ja", "Nej")

@Composable
fun OpmuringBearingWallStep(
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
            text = "Er muren bærende?",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        ChoiceBox(
            options = options,
            selectedOption = if (data.bearingWall == true) "Ja" else if (data.bearingWall == false) "Nej" else null,
            onOptionSelected = { option ->
                viewModel.updateWallData(data.copy(bearingWall = option == "Ja"))
            }
        )
    }
}