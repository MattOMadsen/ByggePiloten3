// Fil: app/src/main/java/dk/byggepiloten/firma/ui/screen/new_task/components/common/DamageAssessmentSection.kt
// NY FIL – Reusable composable til skade-vurdering (revner, fugt, gammel puds)
// Genbruges på tværs af kategorier
// Conditional PhotoUploadSection (obligatorisk ved "Ja")

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
 * Reusable sektion til vurdering af skader på underlag.
 * Viser Ja/Nej for revner, fugt og (valgfrit) gammel puds.
 * Ved "Ja" på et eller flere: obligatorisk billed-upload.
 *
 * @param title Overskrift (f.eks. "Hvordan er underlaget?")
 * @param showOldPuds Hvis true: viser spørgsmål om gammel puds (ude-facade)
 * @param revner Nuværende værdi
 * @param fugt Nuværende værdi
 * @param gammelPuds Nuværende værdi (kun hvis showOldPuds = true)
 * @param photoKey Key til stepPhotos
 * @param currentPhotos Nuværende billeder
 * @param onDataChanged Callback til ViewModel
 * @param onPhotosChanged Callback til stepPhotos
 */
@Composable
fun DamageAssessmentSection(
    title: String = "Hvordan er underlaget?",
    showOldPuds: Boolean = false,
    revner: String?,
    fugt: String?,
    gammelPuds: String?,
    photoKey: String,
    currentPhotos: List<android.net.Uri>,
    onDataChanged: (key: String, value: Any?) -> Unit,
    onPhotosChanged: (List<android.net.Uri>) -> Unit
) {
    val hasDamage = revner == "Ja" || fugt == "Ja" || (showOldPuds && gammelPuds == "Ja")

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
                    label = "Er der revner?",
                    options = listOf("Ja", "Nej"),
                    selectedOption = revner ?: "",
                    onOptionSelected = { onDataChanged("underlagRevner", it) }
                )
                Spacer(Modifier.height(12.dp))
                ChoiceBoxRow(
                    label = "Er der fugtskader?",
                    options = listOf("Ja", "Nej"),
                    selectedOption = fugt ?: "",
                    onOptionSelected = { onDataChanged("underlagFugt", it) }
                )
                if (showOldPuds) {
                    Spacer(Modifier.height(12.dp))
                    ChoiceBoxRow(
                        label = "Er der gammel puds?",
                        options = listOf("Ja", "Nej"),
                        selectedOption = gammelPuds ?: "",
                        onOptionSelected = { onDataChanged("underlagGammelPuds", it) }
                    )
                }
            }
        }

        PhotoUploadSection(
            label = if (hasDamage) "Billeder af skader (obligatorisk)" else "Billeder af underlag (anbefalet)",
            isRequired = hasDamage,
            currentUris = currentPhotos,
            onUrisChange = onPhotosChanged
        )
    }
}