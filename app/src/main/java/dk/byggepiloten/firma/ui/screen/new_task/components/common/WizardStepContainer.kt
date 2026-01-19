// Fil: app/src/main/java/dk/byggepiloten/firma/ui/screen/new_task/components/common/WizardStepContainer.kt
// NY REUSABLE COMPONENT – Standard container til alle wizard-steps
// Inkluderer WizardStepTitle + Column med padding(16.dp) + spacedBy(32.dp)
// Brug: WizardStepContainer(title = "Dit spørgsmål") { ... valg, felter osv. }
// Giver 100% ens layout i alle steps uden kopiering
// Commit: Reusable WizardStepContainer – fuld konsistens i step-struktur

package dk.byggepiloten.firma.ui.screen.new_task.components.common

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun WizardStepContainer(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(32.dp)
    ) {
        WizardStepTitle(text = title)
        content()
    }
}