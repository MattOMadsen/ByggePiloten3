// Fil: app/src/main/java/dk/byggepiloten/firma/ui/screen/new_task/categories/opmuring/OpmuringAccessStep.kt

package dk.byggepiloten.firma.ui.screen.new_task.categories.opmuring

import android.net.Uri
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import dk.byggepiloten.firma.data.model.task.WallData
import dk.byggepiloten.firma.ui.screen.new_task.components.PhotoUploadSection
import dk.byggepiloten.firma.ui.screen.new_task.components.common.StyledTextField

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

        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            listOf(true to "Ja", false to "Nej").forEach { (value, label) ->
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .border(
                            width = 2.dp,
                            color = if (data.goodAccess == value) MaterialTheme.colorScheme.primary else Color.White.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .clickable { onDataChange(data.copy(goodAccess = value)) }
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = label, color = Color.White)
                }
            }
        }

        if (data.goodAccess == false) {
            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Hvad gør adgangen vanskelig?",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    accessProblemsOptions.take(3).forEach { option ->
                        ChoiceBox(option = option, data = data, onDataChange = onDataChange)
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    accessProblemsOptions.drop(3).forEach { option ->
                        ChoiceBox(option = option, data = data, onDataChange = onDataChange)
                    }
                }
            }

            if (data.accessProblems.contains("Andet")) {
                Spacer(modifier = Modifier.height(16.dp))
                StyledTextField(
                    value = data.accessCustomDescription ?: "",
                    onValueChange = { onDataChange(data.copy(accessCustomDescription = it)) },
                    label = "Beskriv nærmere"
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            PhotoUploadSection(
                label = "Upload billeder af adgangsforholdene (kræves ved nej)",
                isRequired = true,
                currentUris = accessPhotos,
                onUrisChange = onAccessPhotosChange
            )
        }
    }
}

@Composable
private fun RowScope.ChoiceBox(
    option: String,
    data: WallData,
    onDataChange: (WallData) -> Unit
) {
    val selected = data.accessProblems.contains(option)
    Box(
        modifier = Modifier
            .weight(1f)
            .clip(RoundedCornerShape(8.dp))
            .border(
                width = 2.dp,
                color = if (selected) MaterialTheme.colorScheme.primary else Color.White.copy(alpha = 0.5f),
                shape = RoundedCornerShape(8.dp)
            )
            .clickable {
                val newList = if (selected) {
                    data.accessProblems.minus(option)
                } else {
                    data.accessProblems + option
                }
                onDataChange(data.copy(accessProblems = newList))
            }
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Text(
            text = option,
            color = Color.White,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
