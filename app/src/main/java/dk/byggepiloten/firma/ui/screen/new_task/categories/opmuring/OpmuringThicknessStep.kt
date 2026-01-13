// Fil: app/src/main/java/dk/byggepiloten/firma/ui/screen/new_task/categories/opmuring/OpmuringThicknessStep.kt
// 100% MATCH MED ORIGINAL – KORREKT MM-VÆRDIER ("108 mm (½ sten)" osv.)
// Layout refactored til ChoiceBoxRow + StyledTextField (Int for mm)
// Linjer: 82

package dk.byggepiloten.firma.ui.screen.new_task.categories.opmuring

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import dk.byggepiloten.firma.data.model.task.WallData
import dk.byggepiloten.firma.ui.screen.new_task.components.common.ChoiceBoxRow
import dk.byggepiloten.firma.ui.screen.new_task.components.common.StyledTextField

@Composable
fun OpmuringThicknessStep(
    data: WallData,
    onDataChange: (WallData) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Hvilken tykkelse skal muren have?",
            style = MaterialTheme.typography.titleMedium,
            color = Color.White,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        val options = listOf("108 mm (½ sten)", "228 mm (1 sten)", "348 mm (1½ sten)", "Andet")

        ChoiceBoxRow(
            options = options,
            selectedOption = data.thicknessOption,
            onOptionSelected = { onDataChange(data.copy(thicknessOption = it, customThickness = if (it != "Andet") null else data.customThickness)) }
        )

        if (data.thicknessOption == "Andet") {
            Spacer(Modifier.height(24.dp))
            StyledTextField(
                value = data.customThickness?.toString() ?: "",
                onValueChange = { if (it.isEmpty() || it.toIntOrNull() != null) onDataChange(data.copy(customThickness = it.toIntOrNull())) },
                label = "Tykkelse (mm)",
                keyboardType = KeyboardType.Number
            )
        }
    }
}