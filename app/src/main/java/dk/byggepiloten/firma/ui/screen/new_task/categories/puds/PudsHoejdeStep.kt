// Fil: app/src/main/java/dk/byggepiloten/firma/ui/screen/new_task/categories/puds/PudsHoejdeStep.kt
// RETTET – stepPhotos-type tvunget + keyboardOptions korrekt import

package dk.byggepiloten.firma.ui.screen.new_task.categories.puds

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dk.byggepiloten.firma.ui.screen.new_task.components.PhotoUploadSection
import dk.byggepiloten.firma.ui.screen.new_task.components.common.StyledTextField
import dk.byggepiloten.firma.ui.viewmodel.task.PudsTaskViewModel

@Composable
fun PudsHoejdeStep(
    viewModel: PudsTaskViewModel
) {
    val pudsData by viewModel.pudsData.collectAsStateWithLifecycle()
    val stepPhotos by viewModel.stepPhotos.collectAsStateWithLifecycle(emptyMap<String, List<android.net.Uri>>())

    var heightText by remember { mutableStateOf(pudsData.hojde?.toString() ?: "") }

    LaunchedEffect(heightText) {
        val parsed = heightText.toFloatOrNull()
        if (parsed != pudsData.hojde) {
            viewModel.updatePudsData(pudsData.copy(hojde = parsed))
        }
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text(
            "Hvad er bygningens højde?",
            style = MaterialTheme.typography.titleLarge,
            color = Color.Black
        )

        Text(
            "Dette bruges til at vurdere om stillads er nødvendigt.",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Black.copy(alpha = 0.7f)
        )

        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.95f)),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                StyledTextField(
                    value = heightText,
                    onValueChange = { if (it.isEmpty() || it.toFloatOrNull() != null) heightText = it },
                    label = "Højde i meter",
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }
        }

        PhotoUploadSection(
            label = "Billeder af facadehøjde (valgfrit)",
            isRequired = false,
            currentUris = stepPhotos["hoejde"] ?: emptyList(),
            onUrisChange = { viewModel.updateStepPhotos("hoejde", it) }
        )
    }
}