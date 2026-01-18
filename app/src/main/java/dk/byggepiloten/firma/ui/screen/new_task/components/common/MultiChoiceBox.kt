// Fil: app/src/main/java/dk/byggepiloten/firma/ui/screen/new_task/components/common/MultiChoiceBox.kt
// FULD RETTET – FlowRow import tilføjet

package dk.byggepiloten.firma.ui.screen.new_task.components.common

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dk.byggepiloten.firma.ui.theme.ByggePilotenBlue
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.FlowRow

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MultiChoiceBox(
    options: List<String>,
    selectedOptions: List<String>,
    onOptionsChange: (List<String>) -> Unit,
    modifier: Modifier = Modifier
) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        options.forEach { option ->
            val isSelected = selectedOptions.contains(option)

            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        if (isSelected) ByggePilotenBlue.copy(alpha = 0.9f) else Color.White
                    )
                    .border(
                        border = BorderStroke(
                            width = 2.dp,
                            color = if (isSelected) Color.White else ByggePilotenBlue.copy(alpha = 0.5f)
                        ),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .clickable {
                        val newSelected = if (isSelected) {
                            selectedOptions - option
                        } else {
                            selectedOptions + option
                        }
                        onOptionsChange(newSelected)
                    }
                    .padding(vertical = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = option,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    color = if (isSelected) Color.White else Color.Black
                )
            }
        }
    }
}