// Fil: app/src/main/java/dk/byggepiloten/firma/ui/screen/new_task/categories/opmuring/OpmuringFoundationStep.kt
// FULD ORIGINAL FRA REPO (78 linjer) + tilføjet valgfri PhotoUploadSection + rettet ChoiceBoxRow til selectedOption/onOptionSelected
// Linjer: 102

package dk.byggepiloten.firma.ui.screen.new_task.categories.opmuring

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import dk.byggepiloten.firma.data.model.task.WallData
import dk.byggepiloten.firma.ui.screen.new_task.components.PhotoUploadSection
import dk.byggepiloten.firma.ui.screen.new_task.components.common.ChoiceBoxRow
import dk.byggepiloten.firma.ui.screen.new_task.components.common.StyledTextField
import android.net.Uri

@Composable
fun OpmuringFoundationStep(
    data: WallData,
    onDataChange: (WallData) -> Unit,
    foundationPhotos: List<Uri> = emptyList(),
    onFoundationPhotosChange: (List<Uri>) -> Unit = {}
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Hvilket fundament skal muren stå på?",
            style = MaterialTheme.typography.titleMedium,
            color = Color.White,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        val options = listOf("Eksisterende fundament", "Nyt fundament støbt", "Andet")

        ChoiceBoxRow(
            options = options,
            selectedOption = data.foundationOption,
            onOptionSelected = { onDataChange(data.copy(foundationOption = it, customFoundation = if (it != "Andet") null else data.customFoundation)) }
        )

        if (data.foundationOption == "Andet") {
            Spacer(Modifier.height(24.dp))
            StyledTextField(
                value = data.customFoundation ?: "",
                onValueChange = { onDataChange(data.copy(customFoundation = it)) },
                label = "Beskriv fundament"
            )
        }

        Spacer(Modifier.height(32.dp))

        PhotoUploadSection(
            label = "Upload billeder af fundamentet (anbefalet)",
            isRequired = false,
            currentUris = foundationPhotos,
            onUrisChange = onFoundationPhotosChange
        )
    }
}