package dk.byggepiloten.firma.ui.screen.new_task.categories.facade

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dk.byggepiloten.firma.data.model.task.FacadeData
import dk.byggepiloten.firma.ui.theme.ByggePilotenBlue

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FacadeVejrStep(
    data: FacadeData,
    onUpdate: (FacadeData) -> Unit
) {
    Column {
        Text("Vejrforhold", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
        Spacer(Modifier.height(24.dp))

        Text("Vejretidspunkt (frostpåvirkning)", color = Color.White, fontSize = 18.sp)
        Spacer(Modifier.height(16.dp))

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            listOf("Sommer", "Vinter", "Forår/Efterår").forEach { option ->
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (data.vejretidspunkt == option) ByggePilotenBlue else Color.White)
                        .clickable { onUpdate(data.copy(vejretidspunkt = option)) }
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Text(
                        text = option,
                        color = if (data.vejretidspunkt == option) Color.White else Color.Black,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}
