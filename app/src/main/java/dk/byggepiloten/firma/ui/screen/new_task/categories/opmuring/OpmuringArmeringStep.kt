// Fil: app/src/main/java/dk/byggepiloten/firma/ui/screen/new_task/categories/opmuring/OpmuringArmeringStep.kt
// CONDITIONAL – KUN VIS VED PUDS/MALET (NY MUR)
// Simpelt Ja/Nej (som original)
// Linjer: 62

package dk.byggepiloten.firma.ui.screen.new_task.categories.opmuring

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import dk.byggepiloten.firma.data.model.task.WallData
import dk.byggepiloten.firma.ui.screen.new_task.components.common.YesNoRow

@Composable
fun OpmuringArmeringStep(
    data: WallData,
    onDataChange: (WallData) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Skal der armeres (anbefalet ved pudset overflade)?",
            style = MaterialTheme.typography.titleMedium,
            color = Color.White,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        YesNoRow(
            selected = data.reinforcement,
            onSelected = { onDataChange(data.copy(reinforcement = it)) }
        )
    }
}