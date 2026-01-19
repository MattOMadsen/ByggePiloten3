// Fil: app/src/main/java/dk/byggepiloten/firma/ui/screen/new_task/categories/opmuring/OpmuringAiPreviewStep.kt
// FIX: Vis error fra viewModel.error (collect fra BaseTaskViewModel via cast)
// - Rød fejlmeddelelse vises centreret hvis generation fejlede
// - Ingen neutral fallback – error kommer direkte fra generatoren
// Total lines: 98

package dk.byggepiloten.firma.ui.screen.new_task.categories.opmuring

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dk.byggepiloten.firma.ui.screen.photos.components.AiEstimateSection
import dk.byggepiloten.firma.ui.viewmodel.task.BaseTaskViewModel
import dk.byggepiloten.firma.ui.viewmodel.task.OpmuringTaskViewModel

@Composable
fun OpmuringAiPreviewStep(
    viewModel: OpmuringTaskViewModel // Vi caster til Base for at få error-flow
) {
    val baseViewModel = viewModel as BaseTaskViewModel
    val isGenerating by viewModel.isGeneratingEstimate.collectAsStateWithLifecycle()
    val aiEstimate by viewModel.aiPriceEstimate.collectAsStateWithLifecycle()
    val error by baseViewModel.error.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Dit AI-estimat",
            style = MaterialTheme.typography.titleLarge,
            color = Color.White,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        AiEstimateSection(
            isGeneratingEstimate = isGenerating,
            aiPriceEstimate = aiEstimate,
            modifier = Modifier.fillMaxWidth()
        )

        // Vis error hvis generation fejlede
        error?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(top = 16.dp)
            )
        }

        Spacer(Modifier.height(32.dp))

        Text(
            text = "Dette er et estimat baseret på dine oplysninger og billeder.\nEndeligt tilbud fra håndværkere kan variere.",
            color = Color.White.copy(alpha = 0.8f),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}