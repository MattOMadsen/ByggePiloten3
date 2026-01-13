// Fil: app/src/main/java/dk/byggepiloten/firma/ui/screen/new_task/categories/opmuring/OpmuringMortarStep.kt
// OPDATERET TIL FÆLLES COMPONENTS
// - ChoiceBoxRow + StyledTextField til "Andet"
// Linjer: 78

package dk.byggepiloten.firma.ui.screen.new_task.categories.opmuring

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import dk.byggepiloten.firma.data.model.task.WallData
import dk.byggepiloten.firma.ui.screen.new_task.components.common.ChoiceBoxRow
import dk.byggepiloten.firma.ui.screen.new_task.components.common.StyledTextField

@Composable
fun OpmuringMortarStep(
    data: WallData,
    onDataChange: (WallData) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Hvilken type mørtel skal bruges?",
            style = MaterialTheme.typography.titleMedium,
            color = Color.White,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        val options = listOf("KC-mørtel", "Traditionel kalkmørtel", "Andet")

        ChoiceBoxRow(
            options = options,
            selectedOption = data.mortarType,
            onOptionSelected = { onDataChange(data.copy(mortarType = it, customMortarType = if (it != "Andet") null else data.customMortarType)) }
        )

        if (data.mortarType == "Andet") {
            Spacer(Modifier.height(24.dp))
            StyledTextField(
                value = data.customMortarType ?: "",
                onValueChange = { onDataChange(data.copy(customMortarType = it)) },
                label = "Beskriv mørtel-type"
            )
        }
    }
}