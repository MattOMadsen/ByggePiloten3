package dk.byggepiloten.firma.ui.screen.new_task.components.common

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import dk.byggepiloten.firma.ui.theme.ByggePilotenBlue

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ChoiceBoxRow(
    options: List<String>,
    selectedOption: String?,
    onOptionSelected: (String) -> Unit,
    label: String? = null,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        if (label != null) {
            Text(
                text = label,
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
                modifier = Modifier.padding(bottom = 12.dp)
            )
        }
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            options.forEach { option ->
                val isSelected = option == selectedOption
                Box(
                    modifier = Modifier
                        .weight(1f, fill = false)
                        .defaultMinSize(minWidth = 100.dp)
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
                        .padding(vertical = 16.dp, horizontal = 12.dp),
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
}
