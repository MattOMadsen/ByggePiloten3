// Fil: app/src/main/java/dk/byggepiloten/firma/ui/screen/new_task/components/common/MultiChoiceBoxColumn.kt
// NY FIL: Reusable multi-select version af ChoiceBoxColumn
// - 100% identisk design med ChoiceBoxColumn (Ja/Nej-boksene)
// - Højde 72.dp, rounded 16.dp, border 2.dp, horizontal padding 24.dp
// - Selected: ByggePilotenBlue 0.9f baggrund + hvid border + hvid tekst
// - Unselected: White baggrund + blå 0.5f border + sort tekst
// - Spacing mellem bokse: 16.dp
// - Klik registreres øjeblikkeligt med .clickable (ingen Card – præcis som ChoiceBoxColumn)
// - Bruges nu i OpmuringAccessStep for adgangsproblemer
// - Total lines: 68

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

/**
 * Reusable multi-select ChoiceBox – vertikal kolonne med bokse.
 * Design er 100% identisk med ChoiceBoxColumn (single-select Ja/Nej).
 * Bruges til valg hvor flere optioner kan vælges samtidigt (toggle).
 */
@Composable
fun MultiChoiceBoxColumn(
    options: List<String>,
    selectedOptions: Set<String>,
    onToggle: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        options.forEach { option ->
            val isSelected = selectedOptions.contains(option)

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(72.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        color = if (isSelected) ByggePilotenBlue.copy(alpha = 0.9f) else Color.White
                    )
                    .border(
                        border = BorderStroke(
                            width = 2.dp,
                            color = if (isSelected) Color.White else ByggePilotenBlue.copy(alpha = 0.5f)
                        ),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .clickable { onToggle(option) }
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