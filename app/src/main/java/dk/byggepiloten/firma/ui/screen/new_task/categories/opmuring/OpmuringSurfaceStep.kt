// Fil: app/src/main/java/dk/byggepiloten/firma/ui/screen/new_task/categories/opmuring/OpmuringSurfaceStep.kt
// FULD RETTET – Tastefejl rettet (ldesluminance → luminance)
// Tilføjet import androidx.compose.ui.graphics.luminance
// Farve-tekst nu auto sort/hvid baseret på baggrundsfarve
// Commit: Fix luminance typo + import – farve-swatches fungerer nu uden build-fejl

package dk.byggepiloten.firma.ui.screen.new_task.categories.opmuring

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance  // FIX: Import til luminance extension
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dk.byggepiloten.firma.data.misc.getDurapudsSwatchColor
import dk.byggepiloten.firma.data.misc.getSkalcemSwatchColor
import dk.byggepiloten.firma.ui.screen.new_task.components.common.ChoiceBox
import dk.byggepiloten.firma.ui.screen.new_task.components.common.ConditionalContent
import dk.byggepiloten.firma.ui.screen.new_task.components.common.StyledTextField
import dk.byggepiloten.firma.ui.screen.new_task.components.common.WizardStepContainer
import dk.byggepiloten.firma.ui.viewmodel.task.OpmuringTaskViewModel

private val surfaceOptions = listOf(
    "Hæftemørtel (DuraPuds 615)",
    "Skalcem S2000",
    "Vandskuring",
    "Andet"
)

private val durapudsFarver = listOf("Cementgrå", "Hvid")

private val skalcemFarver = listOf(
    "Hvid", "S 0505-Y20R", "S 1005-Y30R", "S 1005-Y50R", "S 1010-Y20R", "S 1010-Y50R",
    "S 1020-Y20R", "S 1040-Y20R", "S 1500-N", "S 2005-R80B", "S 2005-Y", "S 2010-G30Y",
    "S 2010-Y30R", "S 2030-Y80R", "S 2040-Y30R", "S 2502-Y", "S 3005-Y20R", "S 3040-Y50R",
    "S 3040-Y80R", "S 4000-N", "S 4010-B90G", "S 5020-B", "S 6000-N", "S 1002-Y"
)

@Composable
fun OpmuringSurfaceStep(
    viewModel: OpmuringTaskViewModel
) {
    val data by viewModel.wallData.collectAsStateWithLifecycle()

    WizardStepContainer(
        title = "Hvilken overfladebehandling ønskes?"
    ) {
        ChoiceBox(
            options = surfaceOptions,
            selectedOption = data.surfaceFinish,
            onOptionSelected = { option ->
                viewModel.updateWallData(
                    data.copy(
                        surfaceFinish = option,
                        customSurface = if (option == "Andet") data.customSurface else null,
                        haeftemoertelFarve = if (option == "Hæftemørtel (DuraPuds 615)") data.haeftemoertelFarve else null,
                        skalcemFarve = if (option == "Skalcem S2000") data.skalcemFarve else null
                    )
                )
            }
        )

        ConditionalContent(visible = data.surfaceFinish == "Hæftemørtel (DuraPuds 615)") {
            Text(
                text = "Vælg farve til DuraPuds 615",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White
            )

            Spacer(Modifier.height(16.dp))

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                durapudsFarver.forEach { farve ->
                    val color = getDurapudsSwatchColor(farve)
                    val isSelected = data.haeftemoertelFarve == farve

                    Box(
                        modifier = Modifier
                            .size(90.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(color)
                            .border(
                                width = if (isSelected) 4.dp else 2.dp,
                                color = if (isSelected) Color.White else Color.Transparent,
                                shape = RoundedCornerShape(16.dp)
                            )
                            .clickable { viewModel.updateWallData(data.copy(haeftemoertelFarve = farve)) },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = farve,
                            color = if (color.luminance() > 0.5f) Color.Black else Color.White,  // FIX: luminance() + auto tekstfarve
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }

        ConditionalContent(visible = data.surfaceFinish == "Skalcem S2000") {
            Text(
                text = "Vælg farve til Skalcem S2000",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White
            )

            Spacer(Modifier.height(16.dp))

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                skalcemFarver.forEach { farve ->
                    val color = getSkalcemSwatchColor(farve)
                    val isSelected = data.skalcemFarve == farve

                    Box(
                        modifier = Modifier
                            .size(90.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(color)
                            .border(
                                width = if (isSelected) 4.dp else 2.dp,
                                color = if (isSelected) Color.White else Color.Transparent,
                                shape = RoundedCornerShape(16.dp)
                            )
                            .clickable { viewModel.updateWallData(data.copy(skalcemFarve = farve)) },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = farve,
                            color = if (color.luminance() > 0.5f) Color.Black else Color.White,  // FIX: luminance() + auto tekstfarve
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }

        ConditionalContent(visible = data.surfaceFinish == "Andet") {
            StyledTextField(
                value = data.customSurface ?: "",
                onValueChange = { viewModel.updateWallData(data.copy(customSurface = it)) },
                label = "Beskriv ønsket overfladebehandling",
                singleLine = false
            )
        }

        Spacer(Modifier.height(16.dp))

        Text(
            text = "Farve er valgfri – håndværkeren kan rådgive om muligheder.",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White.copy(alpha = 0.8f)
        )
    }
}