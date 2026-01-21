// Fil: app/src/main/java/dk/byggepiloten/firma/ui/screen/new_task/components/common/InsulationArmeringSection.kt
// NY FIL – Reusable composable til armering + isolering
// Genbruges i Puds og evt. andre kategorier

package dk.byggepiloten.firma.ui.screen.new_task.components.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import dk.byggepiloten.firma.ui.screen.new_task.components.PhotoUploadSection

/**
 * Reusable sektion til armeringsnet og isolering.
 * Viser:
 * - Armeringsnet Ja/Nej + note ved mursten
 * - Isolering Ja/Nej + conditional isoleringstype (med "Anden" tekstfelt)
 * - PhotoUploadSection (valgfri)
 */
@Composable
fun InsulationArmeringSection(
    vaegtype: String?, // For at vise note ved "Mursten"
    armeringsnet: String?,
    isolering: String?,
    isoleringType: String?,
    customIsolering: String?,
    photoKey: String,
    currentPhotos: List<android.net.Uri>,
    onDataChanged: (key: String, value: Any?) -> Unit,
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
                    label = "Skal der armeringsnet?",
                    options = listOf("Ja", "Nej"),
                    selectedOption = armeringsnet ?: "",
                    onOptionSelected = { onDataChanged("armeringsnet", it) }
                )

                if (vaegtype == "Mursten") {
                    Text(
                        "Ved mursten anbefales armeringsnet",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Black.copy(alpha = 0.7f),
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                Spacer(Modifier.height(16.dp))

                ChoiceBoxRow(
                    label = "Skal der isoleres?",
                    options = listOf("Ja", "Nej"),
                    selectedOption = isolering ?: "",
                    onOptionSelected = { onDataChanged("isolering", it) }
                )

                if (isolering == "Ja") {
                    Spacer(Modifier.height(16.dp))
                    ChoiceBoxRow(
                        label = "Vælg isoleringstype",
                        options = listOf("Mineraluld", "EPS", "Anden"),
                        selectedOption = isoleringType ?: "",
                        onOptionSelected = { onDataChanged("isoleringType", it) }
                    )

                    if (isoleringType == "Anden") {
                        Spacer(Modifier.height(12.dp))
                        StyledTextField(
                            value = customIsolering ?: "",
                            onValueChange = { onDataChanged("customIsolering", it) },
                            label = "Beskriv isoleringstype"
                        )
                    }
                }
            }
        }

        PhotoUploadSection(
            label = "Billeder af væg/underlag (valgfrit)",
            isRequired = false,
            currentUris = currentPhotos,
            onUrisChange = onPhotosChanged
        )
    }
}