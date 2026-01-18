// Fil: app/src/main/java/dk/byggepiloten/firma/ui/screen/new_task/categories/opmuring/OpmuringMurTypeStep.kt
// FULD RETTET – KeyboardOptions rettet (korrekt brug af KeyboardType)
// Tilføjet collectAsStateWithLifecycle

package dk.byggepiloten.firma.ui.screen.new_task.categories.opmuring

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dk.byggepiloten.firma.data.model.task.WallData
import dk.byggepiloten.firma.ui.screen.new_task.components.common.ChoiceBox
import dk.byggepiloten.firma.ui.screen.new_task.components.common.StyledTextField
import dk.byggepiloten.firma.ui.viewmodel.task.OpmuringTaskViewModel

private val murTypeOptions = listOf(
    "Facademur (skalmur/ydervæg)",
    "Indvendig væg",
    "Have-/ støttemur",
    "Andet"
)

@Composable
fun OpmuringMurTypeStep(
    viewModel: OpmuringTaskViewModel
) {
    val data by viewModel.wallData.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(32.dp)
    ) {
        Text(
            text = "Hvilken type mur skal opmures eller repareres?",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        ChoiceBox(
            options = murTypeOptions,
            selectedOption = data.murType,
            onOptionSelected = { option ->
                viewModel.updateWallData(
                    data.copy(
                        murType = option,
                        customMurType = if (option == "Andet") data.customMurType else null
                    )
                )
            }
        )

        if (data.murType == "Andet") {
            StyledTextField(
                value = data.customMurType ?: "",
                onValueChange = { viewModel.updateWallData(data.copy(customMurType = it)) },
                label = "Beskriv hvilken type mur",
                keyboardType = KeyboardType.Text,
                singleLine = false
            )
        }
    }
}