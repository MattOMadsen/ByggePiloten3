// Fil: app/src/main/java/dk/byggepiloten/firma/ui/screen/new_task/categories/opmuring/OpmuringOpeningsStep.kt
// FULD ORIGINAL FRA REPO (hentet verbatim – 142 linjer) + tilføjet valgfri PhotoUploadSection i bunden
// Rettet: Tilføjet manglende onMeasurementsChange-parameter i OpeningMeasurementRow-kald
// Beholdt al original logik (localMeasurements, LaunchedEffect, ChoiceBoxRow med selectedOption/onOptionSelected)
// Linjer: 172

package dk.byggepiloten.firma.ui.screen.new_task.categories.opmuring

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import dk.byggepiloten.firma.data.model.task.OpeningMeasurement
import dk.byggepiloten.firma.data.model.task.WallData
import dk.byggepiloten.firma.ui.screen.new_task.components.PhotoUploadSection
import dk.byggepiloten.firma.ui.screen.new_task.components.common.ChoiceBoxRow
import dk.byggepiloten.firma.ui.screen.new_task.components.common.OpeningMeasurementRow
import dk.byggepiloten.firma.ui.screen.new_task.components.common.StyledTextField
import android.net.Uri

@Composable
fun OpmuringOpeningsStep(
    data: WallData,
    onDataChange: (WallData) -> Unit,
    openingsPhotos: List<Uri> = emptyList(),
    onOpeningsPhotosChange: (List<Uri>) -> Unit = {}
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Skal der være åbninger (døre/vinduer)?",
            style = MaterialTheme.typography.titleMedium,
            color = Color.White,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        val modes = listOf("Ingen åbninger", "Samlet areal", "Individuelle åbninger")

        ChoiceBoxRow(
            options = modes,
            selectedOption = when (data.openingMode) {
                null -> "Ingen åbninger"
                "samlet" -> "Samlet areal"
                "individuel" -> "Individuelle åbninger"
                else -> null
            },
            onOptionSelected = { selected ->
                when (selected) {
                    "Ingen åbninger" -> onDataChange(data.copy(openingMode = null, openingTotalAreaM2 = null, openingMeasurements = emptyList()))
                    "Samlet areal" -> onDataChange(data.copy(openingMode = "samlet", openingMeasurements = emptyList()))
                    "Individuelle åbninger" -> onDataChange(data.copy(openingMode = "individuel"))
                }
            },
            modifier = Modifier.padding(bottom = 24.dp)
        )

        if (data.openingMode == "samlet") {
            StyledTextField(
                value = data.openingTotalAreaM2?.toString() ?: "",
                onValueChange = { if (it.isEmpty() || it.toFloatOrNull() != null) onDataChange(data.copy(openingTotalAreaM2 = it.toFloatOrNull())) },
                label = "Samlet areal af åbninger (m²)",
                keyboardType = KeyboardType.Decimal
            )
        } else if (data.openingMode == "individuel") {
            var localMeasurements by remember { mutableStateOf(data.openingMeasurements.toMutableList()) }

            LaunchedEffect(localMeasurements) {
                onDataChange(data.copy(openingMeasurements = localMeasurements.toList()))
            }

            if (localMeasurements.isEmpty()) {
                localMeasurements = mutableListOf(OpeningMeasurement())
            }

            OpeningMeasurementRow(
                measurements = localMeasurements,
                onMeasurementsChange = { localMeasurements = it.toMutableList() }
            )
        }

        Spacer(Modifier.height(32.dp))

        PhotoUploadSection(
            label = "Upload billeder af åbninger (valgfrit)",
            isRequired = false,
            currentUris = openingsPhotos,
            onUrisChange = onOpeningsPhotosChange
        )
    }
}