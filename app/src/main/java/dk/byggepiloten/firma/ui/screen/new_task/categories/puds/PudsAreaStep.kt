// Fil: app/src/main/java/dk/byggepiloten/firma/ui/screen/new_task/categories/puds/PudsAreaStep.kt
// OPDATERET: Matcher OpmuringDimensionsStep 100% i struktur og logik.
// - Fjerner "infinity height" crash ved at forenkle Column-nesting.
// - Opdaterer viewModel direkte (fjerner kompleks LaunchedEffect).

package dk.byggepiloten.firma.ui.screen.new_task.categories.puds

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dk.byggepiloten.firma.ui.screen.new_task.components.PhotoUploadSection
import dk.byggepiloten.firma.ui.screen.new_task.components.common.ChoiceBoxRow
import dk.byggepiloten.firma.ui.screen.new_task.components.common.MeasurementRow
import dk.byggepiloten.firma.ui.screen.new_task.components.common.StyledTextField
import dk.byggepiloten.firma.ui.viewmodel.task.PudsTaskViewModel
import java.util.Locale

@Composable
fun PudsAreaStep(
    viewModel: PudsTaskViewModel
) {
    val data by viewModel.pudsData.collectAsStateWithLifecycle()
    val generalImages by viewModel.imageUris.collectAsStateWithLifecycle()

    // Vi styrer mode lokalt for at matche Opmuring-stilen
    val currentMode = if (data.wallMeasurements.isNotEmpty()) "Individuelle vægge" else "Samlet areal"

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Hvor stort er arealet der skal pudses?",
            style = MaterialTheme.typography.titleMedium,
            color = Color.White,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        ChoiceBoxRow(
            options = listOf("Samlet areal", "Individuelle vægge"),
            selectedOption = currentMode,
            onOptionSelected = { selected ->
                val clearedMeasurements = if (selected == "Samlet areal") emptyList() else data.wallMeasurements
                viewModel.updatePudsData(
                    data.copy(
                        wallMeasurements = clearedMeasurements,
                        area = if (selected == "Samlet areal") data.area else null
                    )
                )
            },
            modifier = Modifier.padding(bottom = 24.dp)
        )

        if (currentMode == "Samlet areal") {
            StyledTextField(
                value = data.area?.toString() ?: "",
                onValueChange = { newValue ->
                    val cleaned = newValue.replace(',', '.')
                    viewModel.updatePudsData(data.copy(area = cleaned.toFloatOrNull()))
                },
                label = "Samlet areal (m²)",
                keyboardType = KeyboardType.Decimal,
                singleLine = true
            )
        } else {
            MeasurementRow(
                measurements = data.wallMeasurements,
                onMeasurementsChange = { newList ->
                    val calculatedArea = newList.sumOf { ((it.length ?: 0f) * (it.height ?: 0f)).toDouble() }.toFloat()
                    viewModel.updatePudsData(
                        data.copy(
                            wallMeasurements = newList,
                            area = if (calculatedArea > 0f) calculatedArea else null
                        )
                    )
                }
            )
        }

        val wallArea = if (currentMode == "Samlet areal") {
            data.area ?: 0f
        } else {
            data.wallMeasurements.sumOf { ((it.length ?: 0f) * (it.height ?: 0f)).toDouble() }.toFloat()
        }

        if (wallArea > 0f) {
            Spacer(Modifier.height(32.dp))

            Text(
                text = "Samlet areal: ${String.format(Locale.getDefault(), "%.2f", wallArea)} m²",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = Color.White
            )

            Text(
                text = "Areal til pudsning: ${String.format(Locale.getDefault(), "%.2f", wallArea)} m²",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Black,
                color = Color.White,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp)
                    .background(
                        Color.White.copy(alpha = 0.35f),
                        MaterialTheme.shapes.medium
                    )
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            )
        }

        Spacer(Modifier.height(32.dp))

        PhotoUploadSection(
            label = "Billeder af væggene",
            isRequired = false,
            currentUris = generalImages,
            onUrisChange = { viewModel.updateImages(it) }
        )
    }
}
