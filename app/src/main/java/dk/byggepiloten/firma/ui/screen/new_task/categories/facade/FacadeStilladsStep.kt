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
fun FacadeStilladsStep(
    data: FacadeData,
    onUpdate: (FacadeData) -> Unit
) {
    Column {
        Text("Stillads og armeringsnet", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
        Spacer(Modifier.height(24.dp))

        Text("Er stillads nødvendigt?", color = Color.White, fontSize = 18.sp)
        Spacer(Modifier.height(8.dp))

        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("Ja", "Nej").forEach { option ->
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (data.stilladsNoedvendigt == option) ByggePilotenBlue else Color.White)
                        .clickable {
                            onUpdate(
                                data.copy(
                                    stilladsNoedvendigt = option,
                                    stilladsAdgang = null,
                                    stilladsTrapper = null
                                )
                            )
                        }
                        .padding(16.dp)
                ) {
                    Text(option, color = if (data.stilladsNoedvendigt == option) Color.White else Color.Black)
                }
            }
        }

        if (data.stilladsNoedvendigt == "Ja") {
            Spacer(Modifier.height(32.dp))
            Text("Adgang til stillads", color = Color.White, fontSize = 18.sp)
            Spacer(Modifier.height(8.dp))

            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("God adgang", "Begrænset adgang").forEach { option ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (data.stilladsAdgang == option) ByggePilotenBlue else Color.White)
                            .clickable { onUpdate(data.copy(stilladsAdgang = option)) }
                            .padding(16.dp)
                    ) {
                        Text(option, color = if (data.stilladsAdgang == option) Color.White else Color.Black)
                    }
                }
            }

            Spacer(Modifier.height(32.dp))
            Text("Skal stillads bæres op ad trapper?", color = Color.White, fontSize = 18.sp)
            Spacer(Modifier.height(8.dp))

            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("Ja", "Nej").forEach { option ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (data.stilladsTrapper == option) ByggePilotenBlue else Color.White)
                            .clickable { onUpdate(data.copy(stilladsTrapper = option)) }
                            .padding(16.dp)
                    ) {
                        Text(option, color = if (data.stilladsTrapper == option) Color.White else Color.Black)
                    }
                }
            }
        }

        Spacer(Modifier.height(40.dp))

        Text("Ønskes armeringsnet i pudsen?", color = Color.White, fontSize = 18.sp)
        Spacer(Modifier.height(8.dp))

        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("Ja", "Nej").forEach { option ->
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (data.armeringsnet == option) ByggePilotenBlue else Color.White)
                        .clickable { onUpdate(data.copy(armeringsnet = option)) }
                        .padding(16.dp)
                ) {
                    Text(option, color = if (data.armeringsnet == option) Color.White else Color.Black)
                }
            }
        }
    }
}
