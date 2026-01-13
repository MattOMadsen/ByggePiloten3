// Fil: app/src/main/java/dk/byggepiloten/firma/ui/screen/new_task/categories/opmuring/OpmuringSurfaceStep.kt
// UÆNDRET – NU TIDLIGERE I FLOW
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
fun OpmuringSurfaceStep(
    data: WallData,
    onDataChange: (WallData) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Hvilken overfladebehandling ønskes?",
            style = MaterialTheme.typography.titleMedium,
            color = Color.White,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        val options = listOf("Rå mur", "Pudset", "Malet", "Andet")

        ChoiceBoxRow(
            options = options,
            selectedOption = data.surfaceFinish,
            onOptionSelected = { onDataChange(data.copy(surfaceFinish = it, customSurface = if (it != "Andet") null else data.customSurface)) }
        )

        if (data.surfaceFinish == "Andet") {
            Spacer(Modifier.height(24.dp))
            StyledTextField(
                value = data.customSurface ?: "",
                onValueChange = { onDataChange(data.copy(customSurface = it)) },
                label = "Beskriv overflade"
            )
        }
    }
}