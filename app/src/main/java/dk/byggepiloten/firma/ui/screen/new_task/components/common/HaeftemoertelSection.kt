// Fil: app/src/main/java/dk/byggepiloten/firma/ui/screen/new_task/components/common/HaeftemoertelSection.kt
// NY FIL – Reusable composable til hæftemørtel-valg (specifik for Puds, men kan genbruges)

package dk.byggepiloten.firma.ui.screen.new_task.components.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import dk.byggepiloten.firma.ui.screen.new_task.components.PhotoUploadSection

@Composable
fun HaeftemoertelSection(
    haeftemoertelType: String?,
    andenHaeftemoertel: String?,
    durapudsFarve: String?,
    skalcemFarve: String?,
    photoKey: String,
    currentPhotos: List<android.net.Uri>,
    onDataChanged: (key: String, value: String?) -> Unit,
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
                    options = listOf("DuraPuds 615", "Skalcem S2000", "Anden"),
                    selectedOption = haeftemoertelType ?: "",
                    onOptionSelected = { onDataChanged("haeftemoertelType", it) }
                )

                if (haeftemoertelType == "Anden") {
                    Spacer(Modifier.height(8.dp))
                    StyledTextField(
                        value = andenHaeftemoertel ?: "",
                        onValueChange = { onDataChanged("andenHaeftemoertel", it) },
                        label = "Beskriv hæftemørtel"
                    )
                }

                if (haeftemoertelType == "DuraPuds 615") {
                    Spacer(Modifier.height(8.dp))
                    StyledTextField(
                        value = durapudsFarve ?: "",
                        onValueChange = { onDataChanged("durapudsFarve", it) },
                        label = "Ønsket farve (DuraPuds)"
                    )
                }

                if (haeftemoertelType == "Skalcem S2000") {
                    Spacer(Modifier.height(8.dp))
                    StyledTextField(
                        value = skalcemFarve ?: "",
                        onValueChange = { onDataChanged("skalcemFarve", it) },
                        label = "Ønsket farve (Skalcem)"
                    )
                }
            }
        }

        PhotoUploadSection(
            label = "Billeder af eksisterende puds (valgfrit)",
            isRequired = false,
            currentUris = currentPhotos,
            onUrisChange = onPhotosChanged
        )
    }
}