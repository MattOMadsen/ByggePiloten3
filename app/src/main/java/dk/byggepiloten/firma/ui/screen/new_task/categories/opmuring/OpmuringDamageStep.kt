// Fil: app/src/main/java/dk/byggepiloten/firma/ui/screen/OpmuringDamageStep.kt
// SEPARAT STEP 8: Skader (kun ved reparation)

package dk.byggepiloten.firma.ui.screen.new_task.categories.opmuring

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dk.byggepiloten.firma.ui.theme.ByggePilotenBlue

@Composable
fun OpmuringDamageStep(
    hasCracks: Boolean?,
    onHasCracksChange: (Boolean) -> Unit,
    cracksDescription: String?,
    onCracksDescriptionChange: (String) -> Unit,
    hasMoistureDamage: Boolean?,
    onHasMoistureDamageChange: (Boolean) -> Unit,
    moistureDescription: String?,
    onMoistureDescriptionChange: (String) -> Unit,
    hasSettlementDamage: Boolean?,
    onHasSettlementDamageChange: (Boolean) -> Unit,
    settlementDescription: String?,
    onSettlementDescriptionChange: (String) -> Unit
) {
    Text("Beskriv skader", color = Color.White, fontWeight = FontWeight.Medium, fontSize = 20.sp)
    Spacer(Modifier.height(24.dp))

    Text("Revner i muren?", color = Color.White, fontSize = 16.sp)
    Spacer(Modifier.height(8.dp))
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        listOf("Ja", "Nej").forEach { opt ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onHasCracksChange(opt == "Ja") }
                    .background(if (hasCracks == (opt == "Ja")) ByggePilotenBlue else Color.White, RoundedCornerShape(8.dp))
                    .padding(vertical = 16.dp)
            ) {
                Text(opt, color = if (hasCracks == (opt == "Ja")) Color.White else Color.Black, modifier = Modifier.padding(horizontal = 16.dp))
            }
        }
    }
    if (hasCracks == true) {
        Spacer(Modifier.height(16.dp))
        OutlinedTextField(
            value = cracksDescription ?: "",
            onValueChange = onCracksDescriptionChange,
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

    Spacer(Modifier.height(32.dp))
    Text("Fugt eller andre skader?", color = Color.White, fontSize = 16.sp)
    Spacer(Modifier.height(8.dp))
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        listOf("Ja", "Nej").forEach { opt ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onHasMoistureDamageChange(opt == "Ja") }
                    .background(if (hasMoistureDamage == (opt == "Ja")) ByggePilotenBlue else Color.White, RoundedCornerShape(8.dp))
                    .padding(vertical = 16.dp)
            ) {
                Text(opt, color = if (hasMoistureDamage == (opt == "Ja")) Color.White else Color.Black, modifier = Modifier.padding(horizontal = 16.dp))
            }
        }
    }
    if (hasMoistureDamage == true) {
        Spacer(Modifier.height(16.dp))
        OutlinedTextField(
            value = moistureDescription ?: "",
            onValueChange = onMoistureDescriptionChange,
            label = { Text("Beskriv skader") },
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

    Spacer(Modifier.height(32.dp))
    Text("Sætningsskader eller løse sten?", color = Color.White, fontSize = 16.sp)
    Spacer(Modifier.height(8.dp))
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        listOf("Ja", "Nej").forEach { opt ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onHasSettlementDamageChange(opt == "Ja") }
                    .background(if (hasSettlementDamage == (opt == "Ja")) ByggePilotenBlue else Color.White, RoundedCornerShape(8.dp))
                    .padding(vertical = 16.dp)
            ) {
                Text(opt, color = if (hasSettlementDamage == (opt == "Ja")) Color.White else Color.Black, modifier = Modifier.padding(horizontal = 16.dp))
            }
        }
    }
    if (hasSettlementDamage == true) {
        Spacer(Modifier.height(16.dp))
        OutlinedTextField(
            value = settlementDescription ?: "",
            onValueChange = onSettlementDescriptionChange,
            label = { Text("Beskriv skader") },
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