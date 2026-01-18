// Fil: app/src/main/java/dk/byggepiloten/firma/ui/screen/new_task/components/common/ChoiceBox.kt
// NY REUSABLE COMPONENT – Erstatter gamle FlowRow-logik i steps med mange valg
// Automatisk: Hvis ≤2 optioner → side om side (Ja/Nej-stil)
// Hvis >2 optioner → vertikal stack (aflange fuld-bredde bokse, én pr. række)
// Bedre kontrast: Selected = mørkere blå + hvid border
// Unselected = hvid med tynd blå border
// Større touch-target + mere padding → lettere at trykke og se valg

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
fun ChoiceBox(
    options: List<String>,
    selectedOption: String?,
    onOptionSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val isHorizontal = options.size <= 2

    if (isHorizontal) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = modifier.fillMaxWidth()
        ) {
            options.forEach { option ->
                SingleChoiceBox(
                    text = option,
                    isSelected = selectedOption == option,
                    onClick = { onOptionSelected(option) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    } else {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = modifier.fillMaxWidth()
        ) {
            options.forEach { option ->
                SingleChoiceBox(
                    text = option,
                    isSelected = selectedOption == option,
                    onClick = { onOptionSelected(option) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun SingleChoiceBox(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
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
            .clickable(onClick = onClick)
            .padding(horizontal = 24.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium,
            color = if (isSelected) Color.White else Color.Black
        )
    }
}