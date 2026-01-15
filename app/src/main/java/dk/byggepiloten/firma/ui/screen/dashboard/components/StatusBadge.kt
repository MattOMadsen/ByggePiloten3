// Fil: app/src/main/java/dk/byggepiloten/firma/ui/screen/dashboard/components/StatusBadge.kt
package dk.byggepiloten.firma.ui.screen.dashboard.components

import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Reusable status badge med dansk tekst.
 * Status kommer fra Request.status (f.eks. "new").
 * Farve: primaryContainer for "new", secondaryContainer for andre.
 */
@Composable
fun StatusBadge(
    status: String,
    modifier: Modifier = Modifier
) {
    val displayText = when (status.lowercase()) {
        "new" -> "Ny"
        "waiting_for_bids" -> "Venter på bud"
        "bids_received" -> "Bud modtaget"
        "in_progress" -> "I gang"
        "completed" -> "Afsluttet"
        else -> status.replaceFirstChar { it.uppercase() }
    }

    val containerColor = when (status.lowercase()) {
        "new" -> MaterialTheme.colorScheme.primaryContainer
        else -> MaterialTheme.colorScheme.secondaryContainer
    }

    AssistChip(
        onClick = { /* Ingen interaktion – kun visuel */ },
        label = { Text(displayText) },
        colors = AssistChipDefaults.assistChipColors(containerColor = containerColor),
        modifier = modifier
    )
}