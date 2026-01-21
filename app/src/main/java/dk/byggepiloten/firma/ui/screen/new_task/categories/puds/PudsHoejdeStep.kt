// Fil: app/src/main/java/dk/byggepiloten/firma/ui/screen/new_task/categories/puds/PudsHoejdeStep.kt
// OPDATERET – brug af Card + StyledTextField (ens med andre steps)
// Ingen LazyColumn eller nested scroll – crash-sikker

package dk.byggepiloten.firma.ui.screen.new_task.categories.puds

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
    val data by viewModel.pudsData.collectAsStateWithLifecycle()
    val stepPhotos by viewModel.stepPhotos.collectAsStateWithLifecycle(emptyMap<String, List<android.net.Uri>>())

    var heightText by remember { mutableStateOf(data.hojde?.toString() ?: "") }

    LaunchedEffect(heightText) {
        val parsed = heightText.toFloatOrNull()
        if (parsed != data.hojde) {
            viewModel.updatePudsData(data.copy(hojde = parsed))
        }
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
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