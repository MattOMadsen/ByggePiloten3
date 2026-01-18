// Fil: app/src/main/java/dk/byggepiloten/firma/ui/screen/new_task/categories/opmuring/OpmuringDimensionsStep.kt
// FULD RETTET – copy() rettet med korrekte parameter-navne (length, height)
// sumOf rettet med toDouble()

package dk.byggepiloten.firma.ui.screen.new_task.categories.opmuring

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dk.byggepiloten.firma.data.model.task.WallMeasurement
import dk.byggepiloten.firma.ui.screen.new_task.components.common.StyledTextField
import dk.byggepiloten.firma.ui.theme.ByggePilotenBlue
import dk.byggepiloten.firma.ui.viewmodel.task.OpmuringTaskViewModel

@Composable
fun OpmuringDimensionsStep(
    viewModel: OpmuringTaskViewModel
) {
    val data by viewModel.wallData.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(40.dp)
    ) {
        Text(
            text = "Angiv mål på væggene",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        Text(
            text = "Tilføj én eller flere vægge – samlet areal beregnes automatisk",
            style = MaterialTheme.typography.bodyLarge,
            color = Color.White.copy(alpha = 0.9f)
        )

        data.wallMeasurements.forEachIndexed { index, measurement ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                StyledTextField(
                    value = measurement.length?.toString() ?: "",
                    onValueChange = { value ->
                        val newList = data.wallMeasurements.toMutableList()
                        newList[index] = measurement.copy(length = value.toFloatOrNull())
                        viewModel.updateWallData(data.copy(wallMeasurements = newList))
                    },
                    label = "Længde (m)",
                    keyboardType = KeyboardType.Decimal,
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )

                Spacer(Modifier.width(16.dp))

                StyledTextField(
                    value = measurement.height?.toString() ?: "",
                    onValueChange = { value ->
                        val newList = data.wallMeasurements.toMutableList()
                        newList[index] = measurement.copy(height = value.toFloatOrNull())
                        viewModel.updateWallData(data.copy(wallMeasurements = newList))
                    },
                    label = "Højde (m)",
                    keyboardType = KeyboardType.Decimal,
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )

                IconButton(
                    onClick = {
                        val newList = data.wallMeasurements.toMutableList()
                        if (newList.size > 1) newList.removeAt(index)
                        viewModel.updateWallData(data.copy(wallMeasurements = newList))
                    },
                    enabled = data.wallMeasurements.size > 1
                ) {
                    Icon(Icons.Filled.Close, contentDescription = "Fjern væg", tint = Color.White)
                }
            }
        }

        FilledTonalButton(
            onClick = {
                viewModel.updateWallData(
                    data.copy(
                        wallMeasurements = data.wallMeasurements + WallMeasurement()
                    )
                )
            },
            colors = ButtonDefaults.filledTonalButtonColors(
                containerColor = Color.White,
                contentColor = ByggePilotenBlue
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Tilføj væg", fontWeight = FontWeight.Medium)
        }

        val totalArea = data.wallMeasurements.sumOf { ((it.length ?: 0f) * (it.height ?: 0f)).toDouble() }.toFloat()
        if (totalArea > 0f) {
            Text(
                text = "Samlet vægareal: %.2f m²".format(totalArea),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 24.dp)
            )
        }
    }
}