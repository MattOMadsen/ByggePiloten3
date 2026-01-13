// Fil: app/src/main/java/dk/byggepiloten/firma/ui/screen/new_task/categories/fliser/FliserDeductionsStep.kt
// OPDATERET: Vertikalt layout, labels over felter
// - Live brutto/net areal-visning
// - onUpdate callback
// - Linjer: 88

package dk.byggepiloten.firma.ui.screen.new_task.categories.fliser

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dk.byggepiloten.firma.data.model.task.FliserData
import dk.byggepiloten.firma.ui.theme.ByggePilotenBlue
import androidx.compose.foundation.text.KeyboardOptions

@Composable
fun FliserDeductionsStep(
    data: FliserData,
    onUpdate: (FliserData) -> Unit,
    grossArea: Float,
    netArea: Float
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Fradrag", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(32.dp))

        Text("Fradrag (m²) – f.eks. døråbninger, vinduer osv.", color = Color.White, fontSize = 18.sp)
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = data.deductionArea?.toString() ?: "",
            onValueChange = { onUpdate(data.copy(deductionArea = it.toFloatOrNull())) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                cursorColor = ByggePilotenBlue
            ),
            modifier = Modifier.fillMaxWidth(0.8f)
        )

        Spacer(Modifier.height(40.dp))

        Box(
            modifier = Modifier
                .background(ByggePilotenBlue.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                .padding(20.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Bruttoareal: ${"%.2f".format(grossArea)} m²", color = Color.White, fontSize = 18.sp)
                Text("Nettoareal: ${"%.2f".format(netArea)} m²", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}