// Fil: app/src/main/java/dk/byggepiloten/firma/ui/screen/new_task/categories/opmuring/OpmuringNewOrRepairStep.kt
// OPDATERET: Bruger WizardStepTitle (headlineMedium + Bold)
// Column spacedBy(32.dp) – giver præcis samme plads efter titel som step 1
// ChoiceBoxColumn beholdt (bokse allerede ens)
// Nu er step 2 100% identisk med step 1: titel-størrelse, vægt, plads, bokse
// Commit: Step 2 titel matcher step 1 headlineMedium + Bold + samme spacing

package dk.byggepiloten.firma.ui.screen.new_task.categories.opmuring

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dk.byggepiloten.firma.ui.screen.new_task.components.common.ChoiceBoxColumn
import dk.byggepiloten.firma.ui.screen.new_task.components.common.WizardStepTitle
import dk.byggepiloten.firma.ui.viewmodel.task.OpmuringTaskViewModel

@Composable
fun OpmuringNewOrRepairStep(
    viewModel: OpmuringTaskViewModel
) {
    val data by viewModel.wallData.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(32.dp)
    ) {
        WizardStepTitle(
            text = "Skal der opmures nyt, eller repareres eksisterende mur?"
        )

        val options = listOf("Ny opmuring", "Reparation af eksisterende")

        ChoiceBoxColumn(
            options = options,
            selectedOption = if (data.isRepair == true) "Reparation af eksisterende"
            else if (data.isRepair == false) "Ny opmuring"
            else null,
            onOptionSelected = { selected ->
                val isRepair = selected == "Reparation af eksisterende"
                viewModel.updateWallData(data.copy(isRepair = isRepair))
            }
        )
    }
}