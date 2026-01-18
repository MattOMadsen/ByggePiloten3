// Fil: app/src/main/java/dk/byggepiloten/firma/ui/screen/new_task/categories/opmuring/OpmuringSummaryStep.kt
// FULD FIL – Opsummering (sidste step)
// Vis alle data fra WallData + AI-estimat + send-knap
// Auto-generer AI-estimat ved entry (via LaunchedEffect i WizardScreen)
// Pænt layout med sektioner og spacing
// Rettet med collectAsStateWithLifecycle + import getValue
// sumOf med explicit toDouble()
// RETTET: Brug af correct felter length og height fra WallMeasurement

package dk.byggepiloten.firma.ui.screen.new_task.categories.opmuring

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dk.byggepiloten.firma.data.model.task.WallData
import dk.byggepiloten.firma.ui.screen.photos.components.AiEstimateSection
import dk.byggepiloten.firma.ui.viewmodel.task.OpmuringTaskViewModel

@Composable
fun OpmuringSummaryStep(
    data: WallData,
    viewModel: OpmuringTaskViewModel,
    isSending: Boolean
) {
    val aiPriceEstimate by viewModel.aiPriceEstimate.collectAsStateWithLifecycle()
    val isGeneratingEstimate by viewModel.isGeneratingEstimate.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(32.dp)
    ) {
        Text(
            text = "Opsummering – tjek din opgave",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        // Murtype + ny/reparation + bærende
        data.murType?.let { SummaryRow("Type mur", it) }
        data.customMurType?.let { SummaryRow("Anden type mur", it) }
        data.isRepair?.let { SummaryRow("Ny eller reparation", if (it) "Reparation" else "Ny mur") }
        data.bearingWall?.let { SummaryRow("Bærende væg", if (it) "Ja" else "Nej") }

        // Areal-beregning
        val totalWallArea = data.wallMeasurements.sumOf { ((it.length ?: 0f) * (it.height ?: 0f)).toDouble() }.toFloat()
        val totalOpeningArea = data.openingMeasurements.sumOf { ((it.widthCm ?: 0f) / 100f * (it.heightCm ?: 0f) / 100f).toDouble() }.toFloat()
        val nettoArea = (totalWallArea - totalOpeningArea).coerceAtLeast(0f)
        SummaryRow("Samlet vægareal", "%.2f m²".format(totalWallArea))
        if (totalOpeningArea > 0f) SummaryRow("Areal af åbninger", "%.2f m²".format(totalOpeningArea))
        SummaryRow("Netto areal", "%.2f m²".format(nettoArea))

        // Resten af felter
        data.thicknessOption?.let { SummaryRow("Tykkelse", it) }
        data.customThickness?.let { SummaryRow("Anden tykkelse", "${it} mm") }
        data.stoneType?.let { SummaryRow("Sten type", it) }
        data.specialStoneName?.let { SummaryRow("Specialsten navn", it) }
        data.specialStoneLink?.let { SummaryRow("Link til specialsten", it) }
        data.mortarType?.let { SummaryRow("Mørtel", it) }
        data.customMortarType?.let { SummaryRow("Anden mørtel", it) }
        data.surfaceFinish?.let { SummaryRow("Overflade", it) }
        data.customSurface?.let { SummaryRow("Anden overflade", it) }
        data.reinforcement?.let { SummaryRow("Armering", if (it) "Ja" else "Nej") }
        data.insulationWanted?.let {
            SummaryRow("Isolering", if (it) "Ja (${data.insulationThickness ?: 0} cm)" else "Nej")
        }
        data.foundationOption?.let { SummaryRow("Fundament", it) }
        data.customFoundation?.let { SummaryRow("Andet fundament", it) }
        data.vejrTidspunkt?.let { SummaryRow("Ønsket tidspunkt", it) }

        // Skader
        if (data.hasCracks == true || data.hasMoistureDamage == true || data.hasSettlementDamage == true) {
            SummaryRow("Skader rapporteret", "Ja")
            data.cracksDescription?.let { SummaryRow("Revner", it) }
            data.moistureDescription?.let { SummaryRow("Fugt/mug", it) }
            data.settlementDescription?.let { SummaryRow("Sætningsskader", it) }
        }

        // Adgang
        data.goodAccess?.let { SummaryRow("God adgang", if (it) "Ja" else "Nej") }
        if (data.accessProblems.isNotEmpty()) {
            SummaryRow("Adgangsproblemer", data.accessProblems.joinToString(", "))
            data.accessCustomDescription?.let { SummaryRow("Yderligere adgang", it) }
        }

        // AI-estimat
        AiEstimateSection(
            isGeneratingEstimate = isGeneratingEstimate,
            aiPriceEstimate = aiPriceEstimate
        )

        // Beskrivelse
        if (viewModel.description.value.isNotBlank()) {
            SummaryRow("Din beskrivelse", viewModel.description.value)
        }

        // Billeder
        val generalCount = viewModel.imageUris.value.size
        val stepCount = viewModel.stepPhotos.value.values.sumOf { it.size }
        Text(
            text = "Du har uploadet $generalCount generelle billeder + $stepCount trin-billeder.",
            style = MaterialTheme.typography.bodyLarge,
            color = Color.White.copy(alpha = 0.9f)
        )

        Spacer(Modifier.height(32.dp))
    }
}

@Composable
private fun SummaryRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "$label:",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            color = Color.White.copy(alpha = 0.9f)
        )
    }
}