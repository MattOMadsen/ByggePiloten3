// Fil: app/src/main/java/dk/byggepiloten/firma/ui/screen/new_task/categories/opmuring/OpmuringDescriptionStep.kt
// FULD RETTET – Tilføjet import androidx.compose.runtime.getValue
// by-delegation fungerer nu korrekt

package dk.byggepiloten.firma.ui.screen.new_task.categories.opmuring

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dk.byggepiloten.firma.ui.screen.new_task.components.common.DescriptionSection
import dk.byggepiloten.firma.ui.viewmodel.task.OpmuringTaskViewModel

@Composable
fun OpmuringDescriptionStep(
    viewModel: OpmuringTaskViewModel
) {
    val description by viewModel.description.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(40.dp)
    ) {
        Text(
            text = "Tilføj en beskrivelse (valgfrit)",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        DescriptionSection(
            description = description,
            onDescriptionChange = { viewModel.updateDescription(it) },
            label = "Beskriv opgaven nærmere",
            placeholder = "Fx. placering i huset, særlige ønsker, tidsfrister eller andet relevant..."
        )
    }
}