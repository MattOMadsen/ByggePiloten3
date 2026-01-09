// Fil: app/src/main/java/dk/byggepiloten/firma/ui/screen/OpmuringOpeningsStep.kt
// RETTET: Erstattet LazyColumn med Column (fjerner crash fra nested scroll)
// Resten uændret – individuelt per åbning fungerer stadig

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
fun OpmuringOpeningsStep(
    openingsCount: String,
    onOpeningsCountChange: (String) -> Unit,
    openingMode: String?,
    onOpeningModeChange: (String) -> Unit,
    openingTotalAreaM2: String,
    onOpeningTotalAreaChange: (String) -> Unit,
    individualOpenings: SnapshotStateList<Pair<String, String>>,
    totalWallArea: Float,
    openingsArea: Float,
    nettoArea: Float
) {
    Text("Åbninger (vinduer/døre)", color = Color.White, fontWeight = FontWeight.Medium, fontSize = 20.sp)
    Spacer(Modifier.height(24.dp))

    Text("Antal åbninger", color = Color.White, fontSize = 16.sp)
    OutlinedTextField(
        value = openingsCount,
        onValueChange = { if (it.all { c -> c.isDigit() } || it.isBlank()) onOpeningsCountChange(it) },
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

    val count = openingsCount.toIntOrNull() ?: 0
    if (count > 0) {
        Spacer(Modifier.height(24.dp))
        Text("Hvordan vil du angive åbninger?", color = Color.White, fontSize = 16.sp)
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            listOf("Samlet fradragsareal (m²)", "Individuelle mål (cm)").forEach { modeText ->
                val mode = if (modeText.contains("samlet")) "samlet" else "individuel"
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onOpeningModeChange(mode) }
                        .background(if (openingMode == mode) ByggePilotenBlue else Color.White, RoundedCornerShape(8.dp))
                        .padding(vertical = 16.dp)
                ) {
                    Text(
                        modeText,
                        color = if (openingMode == mode) Color.White else Color.Black,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }
        }

        if (openingMode == "samlet") {
            Spacer(Modifier.height(16.dp))
            OutlinedTextField(
                value = openingTotalAreaM2,
                onValueChange = { if (it.all { c -> c.isDigit() || c == '.' }) onOpeningTotalAreaChange(it) },
                label = { Text("Samlet fradragsareal (m²)") },
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
        } else if (openingMode == "individuel") {
            Spacer(Modifier.height(16.dp))
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                repeat(count) { index ->
                    Column {
                        Text("Åbning ${index + 1}", color = Color.White, fontWeight = FontWeight.Medium, fontSize = 16.sp)
                        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            OutlinedTextField(
                                value = individualOpenings.getOrElse(index) { Pair("", "") }.first,
                                onValueChange = { newW ->
                                    val current = individualOpenings.getOrElse(index) { Pair("", "") }
                                    individualOpenings[index] = Pair(newW.filter { it.isDigit() }, current.second)
                                },
                                label = { Text("Bredde (cm)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
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
                                value = individualOpenings.getOrElse(index) { Pair("", "") }.second,
                                onValueChange = { newH ->
                                    val current = individualOpenings.getOrElse(index) { Pair("", "") }
                                    individualOpenings[index] = Pair(current.first, newH.filter { it.isDigit() })
                                },
                                label = { Text("Højde (cm)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
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

        if (openingsArea > 0f) {
            Spacer(Modifier.height(24.dp))
            Text("Brutto areal: ${"%.1f".format(totalWallArea)} m²", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Text("Åbningsfradrag: ${"%.1f".format(openingsArea)} m²", color = Color.White)
            Text("Netto murareal: ${"%.1f".format(nettoArea)} m²", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
        }
    }
}