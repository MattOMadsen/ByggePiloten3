// Fil: app/src/main/java/dk/byggepiloten/firma/ui/screen/new_task/categories/opmuring/OpmuringAiPreviewStep.kt
// NY FIL – 92 linjer
// + Viser AI-estimat via reusable AiEstimateSection
// + Henter isGenerating + aiPriceEstimate fra BaseTaskViewModel (antager felter der)
// + Fallback-tekst hvis ingen estimat
// + Centreret layout med kort forklaring

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

/**
 * Sidste step i wizard: Visning af AI-genereret prisestimat.
 * Bruger reusable AiEstimateSection.
 * Kaldes automatisk i summary/AI-step efter calculateAndGenerateEstimate().
 */
@Composable
fun OpmuringAiPreviewStep(
    viewModel: BaseTaskViewModel
) {
    val isGenerating by viewModel.isGeneratingEstimate.collectAsStateWithLifecycle()
    val aiEstimate by viewModel.aiPriceEstimate.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Dit AI-estimat er klar",
            style = MaterialTheme.typography.titleLarge,
            color = Color.White,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        AiEstimateSection(
            isGeneratingEstimate = isGenerating,
            aiPriceEstimate = aiEstimate,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(32.dp))

        Text(
            text = "Dette er et estimat baseret på dine oplysninger og billeder.\nEndeligt tilbud fra håndværkere kan variere.",
            color = Color.White.copy(alpha = 0.8f),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}

// Total lines: 92