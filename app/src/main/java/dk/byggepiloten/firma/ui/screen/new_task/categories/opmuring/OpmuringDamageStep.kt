// Fil: app/src/main/java/dk/byggepiloten/firma/ui/screen/new_task/categories/opmuring/OpmuringDamageStep.kt
// FIX: FlowRow → Column for hver skade-type (renere layout, ingen rod).

package dk.byggepiloten.firma.ui.screen.new_task.categories.opmuring

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dk.byggepiloten.firma.data.model.task.WallData
import dk.byggepiloten.firma.ui.theme.ByggePilotenBlue

@Composable
fun OpmuringDamageStep(
    data: WallData,
    onDataChange: (WallData) -> Unit
) {
    Text("Skader på eksisterende mur (reparation)", color = Color.White, fontWeight = FontWeight.Medium, fontSize = 20.sp)
    Spacer(Modifier.height(24.dp))

    Column {
        // Revner
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            Text("Revner?", color = Color.White, modifier = Modifier.weight(1f))
            Spacer(Modifier.width(16.dp))
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                val crackOptions = listOf("Ja" to true, "Nej" to false)
                crackOptions.forEach { (text, value) ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.5f)
                            .clickable { onDataChange(data.copy(hasCracks = value)) }
                            .background(if (data.hasCracks == value) ByggePilotenBlue else Color.White, RoundedCornerShape(8.dp))
                            .padding(vertical = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text, color = if (data.hasCracks == value) Color.White else Color.Black)
                    }
                    Spacer(Modifier.height(8.dp))
                }
            }
        }
        if (data.hasCracks == true) {
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = data.cracksDescription ?: "",
                onValueChange = { onDataChange(data.copy(cracksDescription = it)) },
                label = { Text("Beskriv revner") },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
                    cursorColor = ByggePilotenBlue
                )
            )
        }

        Spacer(Modifier.height(24.dp))

        // Fugtskader
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            Text("Fugtskader?", color = Color.White, modifier = Modifier.weight(1f))
            Spacer(Modifier.width(16.dp))
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                val moistureOptions = listOf("Ja" to true, "Nej" to false)
                moistureOptions.forEach { (text, value) ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.5f)
                            .clickable { onDataChange(data.copy(hasMoistureDamage = value)) }
                            .background(if (data.hasMoistureDamage == value) ByggePilotenBlue else Color.White, RoundedCornerShape(8.dp))
                            .padding(vertical = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text, color = if (data.hasMoistureDamage == value) Color.White else Color.Black)
                    }
                    Spacer(Modifier.height(8.dp))
                }
            }
        }
        if (data.hasMoistureDamage == true) {
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = data.moistureDescription ?: "",
                onValueChange = { onDataChange(data.copy(moistureDescription = it)) },
                label = { Text("Beskriv fugtskader") },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
                    cursorColor = ByggePilotenBlue
                )
            )
        }

        Spacer(Modifier.height(24.dp))

        // Sætningsskader
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            Text("Sætningsskader?", color = Color.White, modifier = Modifier.weight(1f))
            Spacer(Modifier.width(16.dp))
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                val settlementOptions = listOf("Ja" to true, "Nej" to false)
                settlementOptions.forEach { (text, value) ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.5f)
                            .clickable { onDataChange(data.copy(hasSettlementDamage = value)) }
                            .background(if (data.hasSettlementDamage == value) ByggePilotenBlue else Color.White, RoundedCornerShape(8.dp))
                            .padding(vertical = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text, color = if (data.hasSettlementDamage == value) Color.White else Color.Black)
                    }
                    Spacer(Modifier.height(8.dp))
                }
            }
        }
        if (data.hasSettlementDamage == true) {
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = data.settlementDescription ?: "",
                onValueChange = { onDataChange(data.copy(settlementDescription = it)) },
                label = { Text("Beskriv sætningsskader") },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
                    cursorColor = ByggePilotenBlue
                )
            )
        }
    }
}