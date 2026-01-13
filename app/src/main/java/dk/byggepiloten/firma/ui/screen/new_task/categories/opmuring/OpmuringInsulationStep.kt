// Fil: app/src/main/java/dk/byggepiloten/firma/ui/screen/new_task/categories/opmuring/OpmuringInsulationStep.kt
// NY/OPDATERET – FÆLLES YesNoRow + conditional tykkelse
// 100% original beholdt
// Linjer: 78

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
import dk.byggepiloten.firma.ui.screen.new_task.components.common.YesNoRow
import dk.byggepiloten.firma.ui.screen.new_task.components.common.StyledTextField

@Composable
fun OpmuringInsulationStep(
    data: WallData,
    onDataChange: (WallData) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Ønskes isolering i muren (kun ved facademur)?",
            style = MaterialTheme.typography.titleMedium,
            color = Color.White,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        YesNoRow(
            selected = data.insulationWanted,
            onSelected = { onDataChange(data.copy(insulationWanted = it, insulationThickness = null)) }
        )

        if (data.insulationWanted == true) {
            Spacer(Modifier.height(24.dp))
            StyledTextField(
                value = data.insulationThickness?.toString() ?: "",
                onValueChange = { if (it.isEmpty() || it.toFloatOrNull() != null) onDataChange(data.copy(insulationThickness = it.toFloatOrNull())) },
                label = "Isoleringstykkelse (mm)",
                keyboardType = KeyboardType.Decimal
            )
        }
    }
}