// Fil: app/src/main/java/dk/byggepiloten/firma/ui/screen/new_task/categories/opmuring/OpmuringSummaryStep.kt
// FIX: Vis error fra viewModel.error som rød banner øverst (hvis ikke null)
// - Error ryddes automatisk ved ny generation (fra BaseTaskViewModel-fix)
// - Beholdt billed-preview, bullet-adgangsproblemer, gul hint kun ved 0 billeder
// - Ingen gul tekst fra AiEstimateSection mere
// Total lines: 298

package dk.byggepiloten.firma.ui.screen.new_task.categories.opmuring

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import dk.byggepiloten.firma.data.model.task.WallData
import dk.byggepiloten.firma.ui.screen.photos.components.AiEstimateSection
import dk.byggepiloten.firma.ui.theme.ByggePilotenBlue
import dk.byggepiloten.firma.ui.viewmodel.task.OpmuringTaskViewModel

@Composable
fun OpmuringSummaryStep(
    data: WallData,
    viewModel: OpmuringTaskViewModel,
    isSending: Boolean
) {
    val aiPriceEstimate by viewModel.aiPriceEstimate.collectAsStateWithLifecycle()
    val isGeneratingEstimate by viewModel.isGeneratingEstimate.collectAsStateWithLifecycle()
    val description by viewModel.description.collectAsStateWithLifecycle()
    val imageUris by viewModel.imageUris.collectAsStateWithLifecycle()
    val stepPhotos by viewModel.stepPhotos.collectAsStateWithLifecycle()
    val error by viewModel.error.collectAsStateWithLifecycle()

    // Saml ALLE billeder til preview
    val allUris = remember(imageUris, stepPhotos) {
        imageUris + stepPhotos.values.flatten()
    }
    val totalImageCount = allUris.size

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text(
            text = "Opsummering – tjek din opgave",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        // Rød error-banner øverst hvis fejl
        error?.let {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }

        // Live netto-areal teaser
        val totalWallArea = remember(data.wallMeasurements, data.wallTotalAreaM2) {
            if (data.wallMode == "Samlet areal") data.wallTotalAreaM2 ?: 0f
            else data.wallMeasurements.sumOf { ((it.length ?: 0f) * (it.height ?: 0f)).toDouble() }.toFloat()
        }
        val totalOpeningArea = remember(data.openingMeasurements) {
            data.openingMeasurements.sumOf { ((it.widthCm ?: 0f) / 100f * (it.heightCm ?: 0f) / 100f).toDouble() }.toFloat()
        }
        val nettoArea = (totalWallArea - totalOpeningArea).coerceAtLeast(0f)

        if (totalWallArea > 0f) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.95f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Beregnet areal",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = ByggePilotenBlue
                    )
                    Spacer(Modifier.height(8.dp))
                    SummaryRow("Samlet vægareal", "%.2f m²".format(totalWallArea))
                    if (totalOpeningArea > 0f) SummaryRow("Fratrukket åbninger", "%.2f m²".format(totalOpeningArea))
                    SummaryRow("Netto areal til opmuring", "%.2f m²".format(nettoArea))
                }
            }
        }

        // Resten af opsummering (uændret)
        data.murType?.let { SummaryRow("Type mur", it) }
        data.customMurType?.let { SummaryRow("Anden type", it) }
        data.isRepair?.let { SummaryRow("Ny eller reparation", if (it) "Reparation" else "Ny mur") }
        data.bearingWall?.let { SummaryRow("Bærende væg", if (it) "Ja" else "Nej") }

        data.thicknessOption?.let { SummaryRow("Tykkelse", it) }
        data.customThickness?.let { SummaryRow("Anden tykkelse", "${it} mm") }
        data.stoneType?.let { SummaryRow("Sten type", it) }
        data.specialStoneName?.let { SummaryRow("Specialsten navn", it) }
        data.specialStoneLink?.let { SummaryRow("Link til specialsten", it) }
        data.mortarType?.let { SummaryRow("Mørtel", it) }
        data.customMortarType?.let { SummaryRow("Anden mørtel", it) }
        data.surfaceFinish?.let { SummaryRow("Overflade", it) }
        data.customSurface?.let { SummaryRow("Anden overflade", it) }

        data.reinforcementLevel?.let { level ->
            val text = when (level) {
                "none" -> "Ingen armeringsnet"
                "standard" -> "Standard armeringsnet over hele fladen (anbefalet)"
                "reinforced" -> "Forstærket armeringsnet (ekstra lag eller tættere ved høj risiko)"
                else -> level
            }
            SummaryRow("Armeringsnet i pudslaget", text)
        }

        data.insulationWanted?.let {
            SummaryRow("Isolering", if (it) "Ja (${data.insulationThickness ?: 0} cm)" else "Nej")
        }
        data.foundationOption?.let { SummaryRow("Fundament", it) }
        data.customFoundation?.let { SummaryRow("Andet fundament", it) }
        data.vejrTidspunkt?.let { SummaryRow("Ønsket tidspunkt", it) }

        if (data.hasCracks == true || data.hasMoistureDamage == true || data.hasSettlementDamage == true) {
            SummaryRow("Skader rapporteret", "Ja")
            data.cracksDescription?.let { SummaryRow("Revner", it) }
            data.moistureDescription?.let { SummaryRow("Fugt", it) }
            data.settlementDescription?.let { SummaryRow("Sætningsskader", it) }
        }

        data.goodAccess?.let { SummaryRow("God adgang", if (it) "Ja" else "Nej") }

        if (data.accessProblems.isNotEmpty()) {
            SummaryRow("Adgangsproblemer", "")
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                data.accessProblems.forEach { problem ->
                    Text(
                        text = "• $problem",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }
                data.accessCustomDescription?.let { custom ->
                    Text(
                        text = custom,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
            }
        }

        // AI-estimat sektion (ingen fallback-tekst her)
        AiEstimateSection(
            isGeneratingEstimate = isGeneratingEstimate,
            aiPriceEstimate = aiPriceEstimate
        )

        // Gul hint kun ved 0 billeder
        if (totalImageCount == 0) {
            Text(
                text = "Ingen AI-estimat endnu – tilføj billeder for bedre resultat",
                style = MaterialTheme.typography.bodyLarge,
                color = Color(0xFFFFD700)
            )
        }

        // Beskrivelse
        if (description.isNotBlank()) {
            SummaryRow("Din beskrivelse", description)
        }

        // Billed-count + preview
        Text(
            text = "Uploadede billeder: ${imageUris.size} generelle + ${stepPhotos.values.sumOf { it.size }} trin-specifikke",
            style = MaterialTheme.typography.bodyLarge,
            color = Color.White.copy(alpha = 0.9f)
        )

        if (allUris.isNotEmpty()) {
            Text(
                text = "Dine billeder",
                style = MaterialTheme.typography.titleLarge,
                color = Color.White,
                modifier = Modifier.padding(top = 8.dp)
            )

            val chunks = allUris.chunked(3)
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                chunks.forEach { rowUris ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        rowUris.forEach { uri ->
                            AsyncImage(
                                model = uri,
                                contentDescription = "Uploadet billede",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(1f)
                                    .clip(RoundedCornerShape(12.dp))
                            )
                        }
                        repeat(3 - rowUris.size) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(32.dp))
    }
}

@Composable
private fun SummaryRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .semantics { contentDescription = "$label: $value" }
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