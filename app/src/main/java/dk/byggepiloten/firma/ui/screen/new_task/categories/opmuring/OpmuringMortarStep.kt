// Fil: app/src/main/java/dk/byggepiloten/firma/ui/screen/new_task/categories/opmuring/OpmuringMortarStep.kt
// OPDATERET: Compile-fix – alle updateWallData → updateWallDataDirect
// - Forklaringer under valg beholdt uændret
// Total lines: 142 (uændret)

package dk.byggepiloten.firma.ui.screen.new_task.categories.opmuring

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dk.byggepiloten.firma.ui.screen.new_task.components.common.ChoiceBox
import dk.byggepiloten.firma.ui.screen.new_task.components.common.WizardStepContainer
import dk.byggepiloten.firma.ui.viewmodel.task.OpmuringTaskViewModel

private val mortarOptions = listOf(
    "Cementmørtel",
    "Kalkmørtel",
    "Bastardmørtel (kalk + cement)",
    "Ingen præference"
)

private val mortarExplanations = mapOf(
    "Cementmørtel" to "Stærk og hurtigt hærdende – god til udendørs og belastede mure.",
    "Kalkmørtel" to "Åndbar og fleksibel – ofte brugt til ældre bygninger og indeklima.",
    "Bastardmørtel (kalk + cement)" to "Kombinerer styrke og åndbarhed – almindelig i nybyg.",
    "Ingen præference" to "Håndværkeren vælger den mest passende type."
)

@Composable
fun OpmuringMortarStep(
    viewModel: OpmuringTaskViewModel
) {
    val data by viewModel.wallData.collectAsStateWithLifecycle()

    WizardStepContainer(
        title = "Hvilken type mørtel foretrækkes?"
    ) {
        ChoiceBox(
            options = mortarOptions,
            selectedOption = data.mortarType,
            onOptionSelected = { viewModel.updateWallDataDirect(data.copy(mortarType = it)) }
        )

        data.mortarType?.let { selected ->
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = mortarExplanations[selected] ?: "",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.8f)
            )
        }
    }
}