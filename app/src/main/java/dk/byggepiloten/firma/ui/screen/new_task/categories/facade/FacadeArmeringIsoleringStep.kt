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
fun FacadeArmeringIsoleringStep(
    data: FacadeData,
    onUpdate: (FacadeData) -> Unit
) {
    Column {
        Text("Facadeisolering", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
        Spacer(Modifier.height(24.dp))

        Text("Ønskes udvendig facadeisolering (ETICS)?", color = Color.White, fontSize = 18.sp)
        Spacer(Modifier.height(8.dp))

        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("Ja", "Nej").forEach { option ->
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (data.isolering == option) ByggePilotenBlue else Color.White)
                        .clickable {
                            onUpdate(
                                data.copy(
                                    isolering = option,
                                    isoleringType = if (option == "Nej") null else data.isoleringType
                                )
                            )
                        }
                        .padding(16.dp)
                ) {
                    Text(option, color = if (data.isolering == option) Color.White else Color.Black)
                }
            }
        }

        if (data.isolering == "Ja") {
            Spacer(Modifier.height(32.dp))
            Text("Vælg isoleringstype", color = Color.White, fontSize = 18.sp)
            Spacer(Modifier.height(8.dp))

            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("Mineraluld (anbefales – bedst åndbarhed)", "EPS (billigere, højere fugtrisiko)").forEach { type ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (data.isoleringType == type) ByggePilotenBlue else Color.White)
                            .clickable { onUpdate(data.copy(isoleringType = type)) }
                            .padding(16.dp)
                    ) {
                        Text(type, color = if (data.isoleringType == type) Color.White else Color.Black, fontSize = 14.sp)
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
            Text("Advarsel: Kræver certificeret udførelse – risiko for fugtproblemer hvis fejl (kilde: Bolius).", color = Color(0xFFFFA500), fontSize = 14.sp)
        }
    }
}
