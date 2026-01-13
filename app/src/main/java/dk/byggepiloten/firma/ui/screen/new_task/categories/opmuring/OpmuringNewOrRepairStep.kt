// Fil: app/src/main/java/dk/byggepiloten/firma/ui/screen/new_task/categories/opmuring/OpmuringNewOrRepairStep.kt
// FULD RETTET – MED FILLMAXWIDTH IMPORT
// Linjer: 66

package dk.byggepiloten.firma.ui.screen.new_task.categories.opmuring

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import dk.byggepiloten.firma.data.model.task.WallData
import dk.byggepiloten.firma.ui.screen.new_task.components.common.ChoiceBoxRow

@Composable
fun OpmuringNewOrRepairStep(
    data: WallData,
    onDataChange: (WallData) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Skal der opmures nyt, eller repareres eksisterende mur?",
            style = MaterialTheme.typography.titleMedium,
            color = Color.White,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        val options = listOf("Ny opmuring", "Reparation af eksisterende")

        ChoiceBoxRow(
            options = options,
            selectedOption = if (data.isRepair == true) "Reparation af eksisterende" else if (data.isRepair == false) "Ny opmuring" else null,
            onOptionSelected = {
                val isRepair = it == "Reparation af eksisterende"
                onDataChange(data.copy(isRepair = isRepair))
            }
        )
    }
}