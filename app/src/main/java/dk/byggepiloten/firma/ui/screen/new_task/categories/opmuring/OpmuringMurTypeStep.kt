// Fil: app/src/main/java/dk/byggepiloten/firma/ui/screen/new_task/categories/opmuring/OpmuringMurTypeStep.kt
// 100% MATCH MED ORIGINAL – INGEN ÆNDRING I INDHOLD/OPTIONS
// Layout refactored til ChoiceBoxRow + StyledTextField
// Linjer: 92

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
fun OpmuringMurTypeStep(
    data: WallData,
    onDataChange: (WallData) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Hvilken type mur skal opmures eller repareres?",
            style = MaterialTheme.typography.titleMedium,
            color = Color.White,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        val options = listOf(
            "Facademur (skalmur/ydervæg)",
            "Bagmur eller indvendig væg",
            "Havemur eller støttemur",
            "Andet"
        )

        ChoiceBoxRow(
            options = options,
            selectedOption = data.murType,
            onOptionSelected = { onDataChange(data.copy(murType = it, customMurType = if (it != "Andet") null else data.customMurType)) }
        )

        if (data.murType == "Andet") {
            Spacer(Modifier.height(24.dp))
            StyledTextField(
                value = data.customMurType ?: "",
                onValueChange = { onDataChange(data.copy(customMurType = it)) },
                label = "Beskriv murtypen"
            )
        }
    }
}