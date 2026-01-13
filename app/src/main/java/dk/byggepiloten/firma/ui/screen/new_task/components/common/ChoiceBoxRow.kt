// Fil: app/src/main/java/dk/byggepiloten/firma/ui/screen/new_task/components/common/ChoiceBoxRow.kt
// FULD RETTET – KORREKT FLOWRROW MED SPACEDBY + ALLE IMPORTS
// Linjer: 76

package dk.byggepiloten.firma.ui.screen.new_task.components.common

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import dk.byggepiloten.firma.ui.theme.ByggePilotenBlue
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Arrangement

@Composable
fun ChoiceBoxRow(
    options: List<String>,
    selectedOption: String?,
    onOptionSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    FlowRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = spacedBy(12.dp),
        verticalArrangement = spacedBy(12.dp)
    ) {
        options.forEach { option ->
            val isSelected = selectedOption == option
            Box(
                modifier = Modifier
                    .clickable { onOptionSelected(option) }
                    .background(
                        color = if (isSelected) ByggePilotenBlue else Color.White,
                        shape = MaterialTheme.shapes.medium
                    )
                    .border(
                        width = 1.dp,
                        color = if (isSelected) ByggePilotenBlue else Color.LightGray,
                        shape = MaterialTheme.shapes.medium
                    )
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = option,
                    color = if (isSelected) Color.White else Color.Black,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}