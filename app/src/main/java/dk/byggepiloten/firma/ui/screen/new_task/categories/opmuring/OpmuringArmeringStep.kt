// Fil: app/src/main/java/dk/byggepiloten/firma/ui/screen/new_task/categories/opmuring/OpmuringArmeringStep.kt
// FULD OPDATERET – 3 valg for pudsarmering (String-baseret)
// Bruger ChoiceBoxRow + stærk anbefaling
// Bind via viewModel.updateWallData(data.copy(...))

package dk.byggepiloten.firma.ui.screen.new_task.categories.opmuring

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dk.byggepiloten.firma.ui.screen.new_task.components.common.ChoiceBoxRow
import dk.byggepiloten.firma.ui.viewmodel.task.OpmuringTaskViewModel

@Composable
fun OpmuringArmeringStep(
    viewModel: OpmuringTaskViewModel
) {
    val data by viewModel.wallData.collectAsStateWithLifecycle()

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Skal der armeringsnet i pudslaget?",
            style = MaterialTheme.typography.titleMedium,
            color = Color.White,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Text(
            text = "Armeringsnet lægges over hele murfladen inde i pudslaget og er standard ved pudset facade for at forhindre revner fra bevægelser, temperatur eller pudsspændinger.\nUden net er risikoen for revner markant højere.",
            color = Color(0xFFFFEB3B),
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        val options = listOf(
            "Ingen armeringsnet",
            "Standard armeringsnet over hele fladen (anbefalet)",
            "Forstærket armeringsnet (ekstra lag eller tættere ved høj risiko)"
        )

        val selectedOption = when (data.reinforcementLevel) {
            "none" -> "Ingen armeringsnet"
            "standard" -> "Standard armeringsnet over hele fladen (anbefalet)"
            "reinforced" -> "Forstærket armeringsnet (ekstra lag eller tættere ved høj risiko)"
            else -> null
        }

        ChoiceBoxRow(
            options = options,
            selectedOption = selectedOption,
            onOptionSelected = { selected ->
                val level = when (selected) {
                    "Ingen armeringsnet" -> "none"
                    "Standard armeringsnet over hele fladen (anbefalet)" -> "standard"
                    "Forstærket armeringsnet (ekstra lag eller tættere ved høj risiko)" -> "reinforced"
                    else -> null
                }
                viewModel.updateWallData(data.copy(reinforcementLevel = level))
            },
            modifier = Modifier.padding(bottom = 24.dp)
        )

        Text(
            text = "Bemærk: Strukturel armering i fuger (rustfri stål) håndteres separat ved skader eller særlige krav til bæreevne.",
            color = Color.White,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}