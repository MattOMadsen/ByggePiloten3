// Fil: app/src/main/java/dk/byggepiloten/firma/ui/screen/new_task/components/common/WizardStepTitle.kt
// OPDATERET: Matcher nu præcis titlen fra OpmuringMurTypeStep (headlineMedium + Bold)
// Ingen padding bottom – plads håndteres via Column.spacedBy(32.dp) i steps
// Centreret, hvid tekst, fillMaxWidth
// Brug i alle steps for 100% ens overskrift
// Commit: WizardStepTitle matcher step 1 headlineMedium + Bold

package dk.byggepiloten.firma.ui.screen.new_task.components.common

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign

@Composable
fun WizardStepTitle(
    text: String
) {
    Text(
        text = text,
        style = MaterialTheme.typography.headlineMedium,
        fontWeight = FontWeight.Bold,
        color = Color.White,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth()
    )
}