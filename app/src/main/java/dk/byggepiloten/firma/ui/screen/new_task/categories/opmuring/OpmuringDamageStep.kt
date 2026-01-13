// Fil: app/src/main/java/dk/byggepiloten/firma/ui/screen/new_task/categories/opmuring/OpmuringDamageStep.kt
// NY/OPDATERET – FÆLLES YesNoRow + conditional beskrivelser (kun ved reparation)
// 100% original beholdt
// Linjer: 112

package dk.byggepiloten.firma.ui.screen.new_task.categories.opmuring

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import dk.byggepiloten.firma.data.model.task.WallData
import dk.byggepiloten.firma.ui.screen.new_task.components.common.YesNoRow
import dk.byggepiloten.firma.ui.screen.new_task.components.common.StyledTextField

@Composable
fun OpmuringDamageStep(
    data: WallData,
    onDataChange: (WallData) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Er der synlige skader på muren?",
            style = MaterialTheme.typography.titleMedium,
            color = Color.White,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Text("Revner?", color = Color.White, modifier = Modifier.padding(top = 8.dp))
        YesNoRow(
            selected = data.hasCracks,
            onSelected = { onDataChange(data.copy(hasCracks = it, cracksDescription = null)) }
        )
        if (data.hasCracks == true) {
            Spacer(Modifier.height(16.dp))
            StyledTextField(
                value = data.cracksDescription ?: "",
                onValueChange = { onDataChange(data.copy(cracksDescription = it)) },
                label = "Beskriv revner"
            )
        }

        Text("Fugtsskader?", color = Color.White, modifier = Modifier.padding(top = 24.dp))
        YesNoRow(
            selected = data.hasMoistureDamage,
            onSelected = { onDataChange(data.copy(hasMoistureDamage = it, moistureDescription = null)) }
        )
        if (data.hasMoistureDamage == true) {
            Spacer(Modifier.height(16.dp))
            StyledTextField(
                value = data.moistureDescription ?: "",
                onValueChange = { onDataChange(data.copy(moistureDescription = it)) },
                label = "Beskriv fugtskader"
            )
        }

        Text("Sætningsskader?", color = Color.White, modifier = Modifier.padding(top = 24.dp))
        YesNoRow(
            selected = data.hasSettlementDamage,
            onSelected = { onDataChange(data.copy(hasSettlementDamage = it, settlementDescription = null)) }
        )
        if (data.hasSettlementDamage == true) {
            Spacer(Modifier.height(16.dp))
            StyledTextField(
                value = data.settlementDescription ?: "",
                onValueChange = { onDataChange(data.copy(settlementDescription = it)) },
                label = "Beskriv sætningsskader"
            )
        }
    }
}