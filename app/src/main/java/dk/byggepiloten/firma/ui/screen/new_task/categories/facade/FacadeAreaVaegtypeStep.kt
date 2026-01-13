package dk.byggepiloten.firma.ui.screen.new_task.categories.facade

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dk.byggepiloten.firma.data.model.task.FacadeData
import dk.byggepiloten.firma.ui.theme.ByggePilotenBlue

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FacadeAreaVaegtypeStep(
    data: FacadeData,
    onUpdate: (FacadeData) -> Unit
) {
    Column {
        Text("Areal og vægtype", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
        Spacer(Modifier.height(24.dp))

        OutlinedTextField(
            value = data.area?.toString() ?: "",
            onValueChange = { str -> onUpdate(data.copy(area = str.toFloatOrNull())) },
            label = { Text("Areal i m²") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                cursorColor = ByggePilotenBlue
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(32.dp))

        Text("Vægtype", color = Color.White, fontSize = 18.sp)
        Spacer(Modifier.height(8.dp))

        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("Mursten", "Gasbeton", "Letbeton", "Anden").forEach { type ->
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (data.vaegtype == type) ByggePilotenBlue else Color.White)
                        .clickable {
                            onUpdate(
                                data.copy(
                                    vaegtype = type,
                                    andenVaegtype = if (type == "Anden") data.andenVaegtype else null
                                )
                            )
                        }
                        .padding(16.dp)
                ) {
                    Text(
                        type,
                        color = if (data.vaegtype == type) Color.White else Color.Black
                    )
                }
            }
        }

        if (data.vaegtype == "Anden") {
            Spacer(Modifier.height(16.dp))
            OutlinedTextField(
                value = data.andenVaegtype ?: "",
                onValueChange = { onUpdate(data.copy(andenVaegtype = it)) },
                label = { Text("Beskriv vægtype") },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    cursorColor = ByggePilotenBlue
                ),
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(Modifier.height(32.dp))

        Text("Bygningshøjde", color = Color.White, fontSize = 18.sp)
        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = data.hojde?.toString() ?: "",
            onValueChange = { str -> onUpdate(data.copy(hojde = str.toFloatOrNull())) },
            label = { Text("Højde i meter") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                cursorColor = ByggePilotenBlue
            ),
            modifier = Modifier.fillMaxWidth()
        )
    }
}
