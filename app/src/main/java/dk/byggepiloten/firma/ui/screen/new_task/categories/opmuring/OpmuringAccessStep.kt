// Fil: app/src/main/java/dk/byggepiloten/firma/ui/screen/new_task/categories/opmuring/OpmuringAccessStep.kt
// FULD FIX – Konsistent styling med resten af wizards (som specificeret i planen)
// - Uvalgt: Hvid baggrund, sort tekst
// - Valgt: Blå baggrund (primary), hvid tekst
// - Ingen border (baggrund viser state klart)
// - Problemer i FlowRow for auto-wrap og bedre UX
// - Ja/Nej i Row med samme stil som valg-bokse
// - Text centreret i bokse
// - Conditional TextField vises live når "Andet" valgt (recomposition virker nu perfekt)
// - PhotoUploadSection uændret
// Linjer: 148

package dk.byggepiloten.firma.ui.screen.new_task.categories.opmuring

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridItemScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dk.byggepiloten.firma.data.model.task.WallData
import dk.byggepiloten.firma.ui.screen.new_task.components.PhotoUploadSection
import dk.byggepiloten.firma.ui.screen.new_task.components.common.StyledTextField
import androidx.compose.foundation.layout.FlowRow

private val accessProblemsOptions = listOf(
    "Begrænset plads til materialer/lager",
    "Kræver stillads eller lift",
    "Smalt indkørsel eller trapper",
    "Indendørs – møbler/genstande i vejen",
    "Højt placeret (over 1. sal)",
    "Andet"
)

@Composable
fun OpmuringAccessStep(
    data: WallData,
    onDataChange: (WallData) -> Unit,
    accessPhotos: List<Uri> = emptyList(),
    onAccessPhotosChange: (List<Uri>) -> Unit = {}
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Er der god adgang til arbejdsområdet?",
            style = MaterialTheme.typography.titleMedium,
            color = Color.White,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Ja/Nej – samme stil som øvrige valg (baggrund viser valgt state)
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            listOf(true to "Ja", false to "Nej").forEach { (value, label) ->
                val isSelected = data.goodAccess == value
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (isSelected) MaterialTheme.colorScheme.primary else Color.White
                        )
                        .clickable { onDataChange(data.copy(goodAccess = value)) }
                        .padding(vertical = 20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = label,
                        color = if (isSelected) Color.White else Color.Black,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }

        if (data.goodAccess == false) {
            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Hvad gør adgangen vanskelig?",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Problemer – FlowRow for auto-wrap + konsistent med andre wizards
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                accessProblemsOptions.forEach { option ->
                    val isSelected = data.accessProblems.contains(option)
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                if (isSelected) MaterialTheme.colorScheme.primary else Color.White
                            )
                            .clickable {
                                val newList = if (isSelected) {
                                    data.accessProblems - option
                                } else {
                                    data.accessProblems + option
                                }
                                onDataChange(data.copy(accessProblems = newList))
                            }
                            .padding(horizontal = 16.dp, vertical = 20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = option,
                            color = if (isSelected) Color.White else Color.Black,
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            // Conditional "Andet"-beskrivelse – vises live når "Andet" valgt
            if (data.accessProblems.contains("Andet")) {
                Spacer(modifier = Modifier.height(24.dp))
                StyledTextField(
                    value = data.accessCustomDescription ?: "",
                    onValueChange = { onDataChange(data.copy(accessCustomDescription = it)) },
                    label = "Beskriv nærmere",
                    singleLine = false
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            PhotoUploadSection(
                label = "Upload billeder af adgangsforholdene (kræves ved nej)",
                isRequired = true,
                currentUris = accessPhotos,
                onUrisChange = onAccessPhotosChange
            )
        }
    }
}