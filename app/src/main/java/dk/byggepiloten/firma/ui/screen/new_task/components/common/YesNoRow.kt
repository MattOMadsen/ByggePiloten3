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

@Composable
fun YesNoRow(
    selected: Boolean?,
    onSelected: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        listOf("Ja" to true, "Nej" to false).forEach { (text, value) ->
            val isSelected = selected == value
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clickable { onSelected(value) }
                    .background(
                        color = if (isSelected) ByggePilotenBlue else Color.White,
                        shape = MaterialTheme.shapes.medium
                    )
                    .border(
                        width = 1.dp,
                        color = if (isSelected) ByggePilotenBlue else Color.LightGray,
                        shape = MaterialTheme.shapes.medium
                    )
                    .padding(vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = text,
                    color = if (isSelected) Color.White else Color.Black,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            if (text == "Ja") Spacer(Modifier.width(12.dp))
        }
    }
}
