// Fil: app/src/main/java/dk/byggepiloten/firma/ui/screen/new_task/categories/opmuring/OpmuringAccessStep.kt
// OPDATERET: Skiftet til viewModel-signatur
// Photos via viewModel.stepPhotos / updateStepPhotos("access", ...)
// Beholdt FlowRow + conditional "Andet" + PhotoUploadSection
// Commit: Konsistent viewModel-signatur for AccessStep
// Linjer: ca. 180

package dk.byggepiloten.firma.ui.screen.new_task.categories.opmuring

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dk.byggepiloten.firma.ui.screen.new_task.components.PhotoUploadSection
import dk.byggepiloten.firma.ui.screen.new_task.components.common.StyledTextField
import dk.byggepiloten.firma.ui.viewmodel.task.OpmuringTaskViewModel

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
    viewModel: OpmuringTaskViewModel
) {
    val data by viewModel.wallData.collectAsStateWithLifecycle()
    val stepPhotos by viewModel.stepPhotos.collectAsStateWithLifecycle()
    val accessPhotos = stepPhotos["access"] ?: emptyList()

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Er der god adgang til arbejdsområdet?",
            style = MaterialTheme.typography.titleMedium,
            color = Color.White,
            modifier = Modifier.padding(bottom = 16.dp)
        )

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
                        .clickable { viewModel.updateWallData(data.copy(goodAccess = value)) }
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

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                accessProblemsOptions.forEach { option ->
                    val isSelected = data.accessProblems.contains(option)
                    Box(
                        modifier = Modifier
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
                                viewModel.updateWallData(data.copy(accessProblems = newList))
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

            if (data.accessProblems.contains("Andet")) {
                Spacer(modifier = Modifier.height(24.dp))
                StyledTextField(
                    value = data.accessCustomDescription ?: "",
                    onValueChange = { viewModel.updateWallData(data.copy(accessCustomDescription = it)) },
                    label = "Beskriv nærmere",
                    singleLine = false
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            PhotoUploadSection(
                label = "Upload billeder af adgangsforholdene (kræves ved nej)",
                isRequired = true,
                currentUris = accessPhotos,
                onUrisChange = { viewModel.updateStepPhotos("access", it) }
            )
        }
    }
}