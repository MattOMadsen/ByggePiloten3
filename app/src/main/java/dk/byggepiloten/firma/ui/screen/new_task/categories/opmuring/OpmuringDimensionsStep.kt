// Fil: app/src/main/java/dk/byggepiloten/firma/ui/screen/OpmuringDimensionsStep.kt
// OPDATERET: Tilføjet import androidx.compose.foundation.clickable + rettet clickable i Box
// Ny logik: Antal vægge + valg samlet areal eller individuelle mål pr. væg

package dk.byggepiloten.firma.ui.screen.new_task.categories.opmuring

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dk.byggepiloten.firma.ui.theme.ByggePilotenBlue

@Composable
fun OpmuringDimensionsStep(
    isRepair: Boolean?,
    wallCount: String,
    onWallCountChange: (String) -> Unit,
    wallMode: String?,
    onWallModeChange: (String) -> Unit,
    wallTotalAreaM2: String,
    onWallTotalAreaChange: (String) -> Unit,
    individualWalls: SnapshotStateList<Pair<String, String>>, // Pair<længde, højde>
    totalWallArea: Float
) {
    Text(
        if (isRepair == true) "Område der skal repareres" else "Dimensioner",
        color = Color.White,
        fontWeight = FontWeight.Medium,
        fontSize = 20.sp
    )
    Spacer(Modifier.height(24.dp))

    Text("Antal vægge", color = Color.White, fontSize = 16.sp)
    OutlinedTextField(
        value = wallCount,
        onValueChange = { if (it.all { c -> c.isDigit() } || it.isBlank()) onWallCountChange(it) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        modifier = Modifier.fillMaxWidth(),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            focusedTextColor = Color.Black,
            unfocusedTextColor = Color.Black,
            cursorColor = ByggePilotenBlue
        )
    )

    val count = wallCount.toIntOrNull() ?: 0
    if (count > 0) {
        Spacer(Modifier.height(24.dp))
        Text("Hvordan vil du angive vægge?", color = Color.White, fontSize = 16.sp)
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            listOf("Samlet areal (m²)", "Individuelle mål pr. væg").forEach { modeText ->
                val mode = if (modeText.contains("samlet")) "samlet" else "individuel"
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onWallModeChange(mode) }
                        .background(if (wallMode == mode) ByggePilotenBlue else Color.White, RoundedCornerShape(8.dp))
                        .padding(vertical = 16.dp)
                ) {
                    Text(
                        modeText,
                        color = if (wallMode == mode) Color.White else Color.Black,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }
        }

        if (wallMode == "samlet") {
            Spacer(Modifier.height(16.dp))
            OutlinedTextField(
                value = wallTotalAreaM2,
                onValueChange = { if (it.all { c -> c.isDigit() || c == '.' }) onWallTotalAreaChange(it) },
                label = { Text("Samlet areal (m²)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
                    cursorColor = ByggePilotenBlue
                )
            )
        } else if (wallMode == "individuel") {
            Spacer(Modifier.height(16.dp))
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                repeat(count) { index ->
                    Column {
                        Text("Væg ${index + 1}", color = Color.White, fontWeight = FontWeight.Medium, fontSize = 16.sp)
                        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            OutlinedTextField(
                                value = individualWalls.getOrElse(index) { Pair("", "") }.first,
                                onValueChange = { newL ->
                                    val current = individualWalls.getOrElse(index) { Pair("", "") }
                                    individualWalls[index] = Pair(newL.filter { it.isDigit() || it == '.' }, current.second)
                                },
                                label = { Text("Længde (meter)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                modifier = Modifier.weight(1f),
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = Color.White,
                                    unfocusedContainerColor = Color.White,
                                    focusedTextColor = Color.Black,
                                    unfocusedTextColor = Color.Black,
                                    cursorColor = ByggePilotenBlue
                                )
                            )
                            OutlinedTextField(
                                value = individualWalls.getOrElse(index) { Pair("", "") }.second,
                                onValueChange = { newH ->
                                    val current = individualWalls.getOrElse(index) { Pair("", "") }
                                    individualWalls[index] = Pair(current.first, newH.filter { it.isDigit() || it == '.' })
                                },
                                label = { Text("Højde (meter)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                modifier = Modifier.weight(1f),
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
            }
        }

        if (totalWallArea > 0f) {
            Spacer(Modifier.height(24.dp))
            Text(
                "Brutto areal: ${"%.1f".format(totalWallArea)} m²",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        }
    }
}