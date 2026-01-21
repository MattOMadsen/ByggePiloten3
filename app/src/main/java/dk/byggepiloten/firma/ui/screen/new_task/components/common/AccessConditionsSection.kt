// Fil: app/src/main/java/dk/byggepiloten/firma/ui/screen/new_task/components/common/AccessConditionsSection.kt
// NY FIL – Reusable composable til adgangsforhold / stilladsbehov
// Genbruges på tværs af kategorier (Puds, Opmuring, Fliser osv.)
// Parametre gør den fleksibel: custom overskrifter, requiredPhotos ved problemer
// Callback opdaterer ViewModel-data via key/value

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

/**
 * Reusable sektion til stillads- og adgangsforhold.
 * Viser:
 * - Valg af stillads nødvendigt (Ja/Nej)
 * - Ved "Ja": Tekstfelter til adgang og trapper/elevator
 * - PhotoUploadSection (required hvis requiredPhotos = true)
 *
 * @param title Overskrift (f.eks. "Stillads og adgang")
 * @param stilladsNoedvendigt Nuværende værdi ("Ja"/"Nej"/null)
 * @param adgang Beskrivelse af adgang
 * @param trapper Beskrivelse af trapper/elevator
 * @param photoKey Key til stepPhotos (f.eks. "stillads")
 * @param currentPhotos Nuværende billeder for denne sektion
 * @param requiredPhotos Hvis true: rød advarsel + validering ved "Ja"
 * @param onDataChanged Callback til ViewModel (key, value)
 * @param onPhotosChanged Callback til stepPhotos
 */
@Composable
fun AccessConditionsSection(
    title: String = "Stillads og adgangsforhold",
    stilladsNoedvendigt: String?,
    adgang: String?,
    trapper: String?,
    photoKey: String,
    currentPhotos: List<android.net.Uri>,
    requiredPhotos: Boolean = false,
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
                    label = "Er der behov for stillads?",
                    options = listOf("Ja", "Nej"),
                    selectedOption = stilladsNoedvendigt ?: "",
                    onOptionSelected = { onDataChanged("stilladsNoedvendigt", it) }
                )

                if (stilladsNoedvendigt == "Ja") {
                    Spacer(Modifier.height(16.dp))
                    StyledTextField(
                        value = adgang ?: "",
                        onValueChange = { onDataChanged("stilladsAdgang", it) },
                        label = "Beskriv adgangsforhold (f.eks. via gård, altan osv.)"
                    )
                    Spacer(Modifier.height(12.dp))
                    StyledTextField(
                        value = trapper ?: "",
                        onValueChange = { onDataChanged("stilladsTrapper", it) },
                        label = "Beskriv trapper/elevator (hvis relevant)"
                    )
                }
            }
        }

        PhotoUploadSection(
            label = if (requiredPhotos && stilladsNoedvendigt == "Ja") "Billeder af adgang/stilladsbehov (obligatorisk)" else "Billeder af adgang/stilladsbehov (anbefalet)",
            isRequired = requiredPhotos && stilladsNoedvendigt == "Ja",
            currentUris = currentPhotos,
            onUrisChange = onPhotosChanged
        )
    }
}