// Fil: app/src/main/java/dk/byggepiloten/firma/ui/screen/new_task/components/common/ChoiceBoxColumn.kt
// OPDATERET: Styling nu 100% identisk med SingleChoiceBox i ChoiceBox.kt
// Height 72.dp, rounded 16.dp, border 2.dp (hvid selected, blå 0.5f unselected)
// Padding horizontal 24.dp, titleMedium + Medium weight, centreret tekst
// Spacing mellem bokse 16.dp (som i ChoiceBox Column)
// Commit: ChoiceBoxColumn matcher nu præcis ChoiceBox/SingleChoiceBox fra step 1

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

@Composable
fun ChoiceBoxColumn(
    options: List<String>,
    selectedOption: String?,
    onOptionSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        options.forEach { option ->
            val isSelected = option == selectedOption

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(72.dp)
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
                    .clickable { onOptionSelected(option) }
                    .padding(horizontal = 24.dp),
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