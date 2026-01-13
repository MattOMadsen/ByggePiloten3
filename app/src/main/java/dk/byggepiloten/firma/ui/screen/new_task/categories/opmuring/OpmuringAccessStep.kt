// Fil: app/src/main/java/dk/byggepiloten/firma/ui/screen/new_task/categories/opmuring/OpmuringAccessStep.kt
// NY/OPDATERET – FÆLLES YesNoRow
// 100% original beholdt (sidste step)
// Linjer: 58

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
fun OpmuringAccessStep(
    data: WallData,
    onDataChange: (WallData) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Er der god adgang til arbejdsområdet?",
            style = MaterialTheme.typography.titleMedium,
            color = Color.White,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        YesNoRow(
            selected = data.goodAccess,
            onSelected = { onDataChange(data.copy(goodAccess = it)) }
        )
    }
}