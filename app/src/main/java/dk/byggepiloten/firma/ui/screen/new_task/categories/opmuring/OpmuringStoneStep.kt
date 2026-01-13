// Fil: app/src/main/java/dk/byggepiloten/firma/ui/screen/new_task/categories/opmuring/OpmuringStoneStep.kt
// 100% MATCH MED ORIGINAL – ALLE STEN-TYPER BEHOLDT
// Layout refactored til ChoiceBoxRow + StyledTextField
// Linjer: 82

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
fun OpmuringStoneStep(
    data: WallData,
    onDataChange: (WallData) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Hvilken type sten skal bruges?",
            style = MaterialTheme.typography.titleMedium,
            color = Color.White,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        val options = listOf(
            "Standard teglsten (rød)",
            "Gule teglsten",
            "Gasbetonblokke",
            "Letbetonblokke",
            "Andet"
        )

        ChoiceBoxRow(
            options = options,
            selectedOption = data.stoneType,
            onOptionSelected = { onDataChange(data.copy(stoneType = it, customStoneType = if (it != "Andet") null else data.customStoneType)) }
        )

        if (data.stoneType == "Andet") {
            Spacer(Modifier.height(24.dp))
            StyledTextField(
                value = data.customStoneType ?: "",
                onValueChange = { onDataChange(data.copy(customStoneType = it)) },
                label = "Beskriv sten-type"
            )
        }
    }
}