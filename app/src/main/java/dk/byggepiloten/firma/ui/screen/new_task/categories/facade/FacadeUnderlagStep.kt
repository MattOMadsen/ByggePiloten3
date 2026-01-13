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
fun FacadeUnderlagStep(
    data: FacadeData,
    onUpdate: (FacadeData) -> Unit
) {
    Column {
        Text("Underlagstilstand", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
        Spacer(Modifier.height(24.dp))

        val questions = listOf(
            Triple("Revner i underlag", data.underlagRevner) { value: String -> onUpdate(data.copy(underlagRevner = value)) },
            Triple("Fugt i underlag", data.underlagFugt) { value: String -> onUpdate(data.copy(underlagFugt = value)) },
            Triple("Gammel puds skal fjernes", data.underlagGammelPuds) { value: String -> onUpdate(data.copy(underlagGammelPuds = value)) }
        )

        questions.forEach { question ->
            val label = question.first
            val current = question.second
            val setter = question.third

            Text(label, color = Color.White, fontSize = 18.sp)
            Spacer(Modifier.height(8.dp))

            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("Ja", "Nej").forEach { option ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (current == option) ByggePilotenBlue else Color.White)
                            .clickable { setter(option) }
                            .padding(16.dp)
                    ) {
                        Text(option, color = if (current == option) Color.White else Color.Black)
                    }
                }
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}
