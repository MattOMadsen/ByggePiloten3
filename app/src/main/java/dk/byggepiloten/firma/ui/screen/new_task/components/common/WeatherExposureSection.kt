// Fil: app/src/main/java/dk/byggepiloten/firma/ui/screen/new_task/components/common/WeatherExposureSection.kt
// NY FIL – Reusable composable til vejr-eksponering (ude-facade)

package dk.byggepiloten.firma.ui.screen.new_task.components.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import dk.byggepiloten.firma.ui.screen.new_task.components.PhotoUploadSection

/**
 * Reusable sektion til vejr-eksponering.
 * Primært til ude-facade (Puds).
 */
@Composable
fun WeatherExposureSection(
    vejretidspunkt: String?,
    photoKey: String,
    currentPhotos: List<android.net.Uri>,
    onDataChanged: (String) -> Unit,
    onPhotosChanged: (List<android.net.Uri>) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.95f)),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                ChoiceBoxRow(
                    label = null,
                    options = listOf("Forår/sommer", "Efterår/vinter", "Helårs eksponeret"),
                    selectedOption = vejretidspunkt ?: "",
                    onOptionSelected = onDataChanged
                )
            }
        }

        PhotoUploadSection(
            label = "Billeder af vejrpåvirkning (valgfrit)",
            isRequired = false,
            currentUris = currentPhotos,
            onUrisChange = onPhotosChanged
        )
    }
}