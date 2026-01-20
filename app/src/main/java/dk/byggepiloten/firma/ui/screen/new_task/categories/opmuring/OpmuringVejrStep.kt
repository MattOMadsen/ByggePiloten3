// Fil: app/src/main/java/dk/byggepiloten/firma/ui/screen/new_task/categories/opmuring/OpmuringVejrStep.kt
// OPDATERET: Compile-fix – updateWallData → updateWallDataDirect
// Total lines: ~70 (uændret)

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

private val vejrOptions = listOf("Sommer", "Vinter", "Forår-Efterår")

@Composable
fun OpmuringVejrStep(
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
            text = "Hvornår på året skal arbejdet helst udføres?",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        ChoiceBox(
            options = vejrOptions,
            selectedOption = data.vejrTidspunkt,
            onOptionSelected = { viewModel.updateWallDataDirect(data.copy(vejrTidspunkt = it)) }
        )
    }
}