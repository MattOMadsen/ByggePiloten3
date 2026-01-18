// Fil: app/src/main/java/dk/byggepiloten/firma/ui/screen/new_task/categories/opmuring/OpmuringDamageStep.kt
// FULD OPDATERET – Ændret til viewModel-parameter
// Bind direkte til viewModel.updateWallData og updateStepPhotos("damage")
// MultiChoiceBox + conditional beskrivelser + PhotoUploadSection (obligatorisk ved skader)
// Layout uændret

package dk.byggepiloten.firma.ui.screen.new_task.categories.opmuring

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dk.byggepiloten.firma.data.model.task.WallData
import dk.byggepiloten.firma.ui.screen.new_task.components.PhotoUploadSection
import dk.byggepiloten.firma.ui.screen.new_task.components.common.MultiChoiceBox
import dk.byggepiloten.firma.ui.screen.new_task.components.common.StyledTextField
import dk.byggepiloten.firma.ui.viewmodel.task.OpmuringTaskViewModel

private val damageOptions = listOf("Revner", "Fugt/mug", "Sætningsskader")

@Composable
fun OpmuringDamageStep(
    viewModel: OpmuringTaskViewModel
) {
    val data by viewModel.wallData.collectAsStateWithLifecycle()
    val damagePhotos = viewModel.stepPhotos.collectAsState().value["damage"] ?: emptyList()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(40.dp)
    ) {
        Text(
            text = "Er der synlige skader på eksisterende mur?",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        MultiChoiceBox(
            options = damageOptions,
            selectedOptions = listOfNotNull(
                "Revner".takeIf { data.hasCracks == true },
                "Fugt/mug".takeIf { data.hasMoistureDamage == true },
                "Sætningsskader".takeIf { data.hasSettlementDamage == true }
            ),
            onOptionsChange = { selected ->
                viewModel.updateWallData(
                    data.copy(
                        hasCracks = selected.contains("Revner"),
                        hasMoistureDamage = selected.contains("Fugt/mug"),
                        hasSettlementDamage = selected.contains("Sætningsskader")
                    )
                )
            }
        )

        if (data.hasCracks == true) {
            StyledTextField(
                value = data.cracksDescription ?: "",
                onValueChange = { viewModel.updateWallData(data.copy(cracksDescription = it)) },
                label = "Beskriv revner",
                singleLine = false
            )
        }
        if (data.hasMoistureDamage == true) {
            StyledTextField(
                value = data.moistureDescription ?: "",
                onValueChange = { viewModel.updateWallData(data.copy(moistureDescription = it)) },
                label = "Beskriv fugt/mug",
                singleLine = false
            )
        }
        if (data.hasSettlementDamage == true) {
            StyledTextField(
                value = data.settlementDescription ?: "",
                onValueChange = { viewModel.updateWallData(data.copy(settlementDescription = it)) },
                label = "Beskriv sætningsskader",
                singleLine = false
            )
        }

        val hasDamage = data.hasCracks == true || data.hasMoistureDamage == true || data.hasSettlementDamage == true

        PhotoUploadSection(
            label = "Upload billeder af skader",
            isRequired = hasDamage,
            currentUris = damagePhotos,
            onUrisChange = { viewModel.updateStepPhotos("damage", it) }
        )
    }
}