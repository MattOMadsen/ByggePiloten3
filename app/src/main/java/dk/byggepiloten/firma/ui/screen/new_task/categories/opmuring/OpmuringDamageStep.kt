// Fil: app/src/main/java/dk/byggepiloten/firma/ui/screen/new_task/categories/opmuring/OpmuringDamageStep.kt
// OPDATERET TIL VIEWMODEL-STIL – 178 linjer
// + Konsistent med andre steps: tager viewModel
// + Yes/No via reusable YesNoRow (beholdt fra din version)
// + Conditional tekstfelter + påkrævet PhotoUploadSection ved "Ja"
// + Binding via updateWallData + updateStepPhotos("damage")
// + KDoc + kommentarer
// + Imports rettet (tilføjet collectAsStateWithLifecycle)

package dk.byggepiloten.firma.ui.screen.new_task.categories.opmuring

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dk.byggepiloten.firma.ui.screen.new_task.components.PhotoUploadSection
import dk.byggepiloten.firma.ui.screen.new_task.components.common.YesNoRow
import dk.byggepiloten.firma.ui.screen.new_task.components.common.StyledTextField
import dk.byggepiloten.firma.ui.viewmodel.task.OpmuringTaskViewModel

/**
 * Composable for skadetrinnet i opmuring-wizard.
 * Kun synlig hvis isRepair == true (håndteres i wizard).
 * Yes/No for revner, fugt, sætningsskader via YesNoRow.
 * Hvis "Ja" → tekstfelt + påkrævet billeder.
 */
@Composable
fun OpmuringDamageStep(
    viewModel: OpmuringTaskViewModel
) {
    val data by viewModel.wallData.collectAsStateWithLifecycle()
    val stepPhotos by viewModel.stepPhotos.collectAsStateWithLifecycle()
    val damagePhotos = stepPhotos["damage"] ?: emptyList()

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Er der synlige skader på muren?",
            style = MaterialTheme.typography.titleMedium,
            color = Color.White,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Text("Revner?", color = Color.White, modifier = Modifier.padding(top = 8.dp))
        YesNoRow(
            selected = data.hasCracks,
            onSelected = { viewModel.updateWallData(data.copy(hasCracks = it)) }
        )
        if (data.hasCracks == true) {
            Spacer(Modifier.height(16.dp))
            StyledTextField(
                value = data.cracksDescription ?: "",
                onValueChange = { viewModel.updateWallData(data.copy(cracksDescription = it)) },
                label = "Beskriv revner"
            )
        }

        Text("Fugtsskader?", color = Color.White, modifier = Modifier.padding(top = 24.dp))
        YesNoRow(
            selected = data.hasMoistureDamage,
            onSelected = { viewModel.updateWallData(data.copy(hasMoistureDamage = it)) }
        )
        if (data.hasMoistureDamage == true) {
            Spacer(Modifier.height(16.dp))
            StyledTextField(
                value = data.moistureDescription ?: "",
                onValueChange = { viewModel.updateWallData(data.copy(moistureDescription = it)) },
                label = "Beskriv fugtskader"
            )
        }

        Text("Sætningsskader?", color = Color.White, modifier = Modifier.padding(top = 24.dp))
        YesNoRow(
            selected = data.hasSettlementDamage,
            onSelected = { viewModel.updateWallData(data.copy(hasSettlementDamage = it)) }
        )
        if (data.hasSettlementDamage == true) {
            Spacer(Modifier.height(16.dp))
            StyledTextField(
                value = data.settlementDescription ?: "",
                onValueChange = { viewModel.updateWallData(data.copy(settlementDescription = it)) },
                label = "Beskriv sætningsskader"
            )
        }

        if (data.hasCracks == true || data.hasMoistureDamage == true || data.hasSettlementDamage == true) {
            Spacer(Modifier.height(32.dp))
            PhotoUploadSection(
                label = "Upload billeder af skaderne (kræves ved ja)",
                isRequired = true,
                currentUris = damagePhotos,
                onUrisChange = { viewModel.updateStepPhotos("damage", it) }
            )
        }
    }
}

// Total lines: 178