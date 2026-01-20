// Fil: app/src/main/java/dk/byggepiloten/firma/ui/screen/new_task/categories/opmuring/OpmuringSummaryStep.kt
// FIX: Rød card med manglende trin hvis validation fejler

package dk.byggepiloten.firma.ui.screen.new_task.categories.opmuring

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import dk.byggepiloten.firma.ui.screen.photos.components.AiEstimateSection
import dk.byggepiloten.firma.ui.theme.ByggePilotenBlue
import dk.byggepiloten.firma.ui.viewmodel.task.OpmuringTaskViewModel

@Composable
fun OpmuringSummaryStep(
    viewModel: OpmuringTaskViewModel,
    isSending: Boolean
) {
    val data by viewModel.wallData.collectAsStateWithLifecycle()
    val aiPriceEstimate by viewModel.aiPriceEstimate.collectAsStateWithLifecycle()
    val isGeneratingEstimate by viewModel.isGeneratingEstimate.collectAsStateWithLifecycle()
    val description by viewModel.description.collectAsStateWithLifecycle()
    val imageUris by viewModel.imageUris.collectAsStateWithLifecycle()
    val stepPhotos by viewModel.stepPhotos.collectAsStateWithLifecycle()
    val error by viewModel.error.collectAsStateWithLifecycle()

    val totalImageCount = remember(imageUris, stepPhotos) {
        imageUris.size + stepPhotos.values.sumOf { it.size }
    }

    val warningNote = error?.takeIf {
        it.contains("groft", ignoreCase = true) ||
                it.contains("kontakte AI", ignoreCase = true) ||
                it.contains("overloaded", ignoreCase = true) ||
                it.contains("unavailable", ignoreCase = true) ||
                it.contains("503", ignoreCase = true)
    }

    val criticalError = error?.takeIf { warningNote == null }

    val missingSteps = viewModel.validateBeforeSend()

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

        criticalError?.let {
            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)) {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }

        if (missingSteps.isNotEmpty()) {
            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Du mangler at udfylde følgende trin:",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                    Spacer(Modifier.height(8.dp))
                    LazyColumn {
                        items(missingSteps) { step ->
                            Text(
                                text = "• Trin $step",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "Tryk 'Send opgave' igen for at se dialog med clickable trin.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }
        }

        val totalWallArea = remember(data.wallMode, data.wallTotalAreaM2, data.wallMeasurements) {
            if (data.wallMode == "Samlet areal") data.wallTotalAreaM2 ?: 0f
            else data.wallMeasurements.sumOf { ((it.length ?: 0f) * (it.height ?: 0f)).toDouble() }.toFloat()
        }
        val totalOpeningArea = remember(data.openingMeasurements) {
            data.openingMeasurements.sumOf { ((it.widthCm ?: 0f) / 100f * (it.heightCm ?: 0f) / 100f).toDouble() }.toFloat()
        }
        val nettoArea = (totalWallArea - totalOpeningArea).coerceAtLeast(0f)

        if (nettoArea <= 0f) {
            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Advarsel: Areal beregnet til 0 m²",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "Gå tilbage og tjek målinger eller samlet areal.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }
        }

        Card(colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.95f))) {
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
                SummaryRow("Netto areal til opmuring", "%.2f m²".format(nettoArea), weight = FontWeight.Bold)
            }
        }

        Text(
            text = "Dit AI-estimat",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        if (isGeneratingEstimate) {
            AiEstimateSection(isGeneratingEstimate = true, aiPriceEstimate = null)
        } else if (aiPriceEstimate != null) {
            AiEstimateSection(
                isGeneratingEstimate = false,
                aiPriceEstimate = aiPriceEstimate,
                warningNote = warningNote
            )
        } else {
            Card(colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.95f))) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Kunne ikke hente AI-estimat lige nu",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFF8A65)
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "Gemini-serveren er midlertidigt overbelastet (503-fejl). Prøv igen om et øjeblik – estimatet kommer automatisk.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    warningNote?.let {
                        Spacer(Modifier.height(8.dp))
                        Text(text = it, color = Color(0xFFFF8A65), style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }

        if (totalImageCount == 0) {
            Text(
                text = "Tip: Tilføj billeder for bedre AI-estimat og hurtigere tilbud",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFFFFD700).copy(alpha = 0.8f)
            )
        }

        // Resten af summary uændret ...
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

        if ((data.hasCracks ?: false) || (data.hasMoistureDamage ?: false) || (data.hasSettlementDamage ?: false)) {
            SummaryRow("Skader rapporteret", "Ja")
            data.cracksDescription?.let { SummaryRow("Revner", it) }
            data.moistureDescription?.let { SummaryRow("Fugt", it) }
            data.settlementDescription?.let { SummaryRow("Sætningsskader", it) }
        }

        if (data.goodAccess == true) {
            SummaryRow("God adgang", "Ja")
        } else if (data.goodAccess == false) {
            SummaryRow("God adgang", "Nej")
        }

        if (data.accessProblems.isNotEmpty()) {
            SummaryRow("Adgangsproblemer", "")
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                data.accessProblems.forEach { problem ->
                    Text(text = "• $problem", style = MaterialTheme.typography.bodyLarge, color = Color.White.copy(alpha = 0.9f))
                }
                data.accessCustomDescription?.let {
                    Text(text = it, style = MaterialTheme.typography.bodyMedium, color = Color.White.copy(alpha = 0.8f))
                }
            }
        }

        if (description.isNotBlank()) SummaryRow("Din beskrivelse", description)

        Text(
            text = "Uploadede billeder: ${imageUris.size} generelle + ${stepPhotos.values.sumOf { it.size }} trin-specifikke",
            style = MaterialTheme.typography.bodyLarge,
            color = Color.White.copy(alpha = 0.9f)
        )

        if (imageUris.isNotEmpty() || stepPhotos.values.any { it.isNotEmpty() }) {
            val allUris = remember(imageUris, stepPhotos) { imageUris + stepPhotos.values.flatten() }
            Text(text = "Dine billeder", style = MaterialTheme.typography.titleLarge, color = Color.White, modifier = Modifier.padding(top = 8.dp))
            val chunks = allUris.chunked(3)
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                chunks.forEach { rowUris ->
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        rowUris.forEach { uri ->
                            AsyncImage(
                                model = uri,
                                contentDescription = "Uploadet billede",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.weight(1f).aspectRatio(1f).clip(RoundedCornerShape(12.dp))
                            )
                        }
                        repeat(3 - rowUris.size) { Spacer(modifier = Modifier.weight(1f)) }
                    }
                }
            }
        }

        Spacer(Modifier.height(120.dp))
    }
}

@Composable
private fun SummaryRow(
    label: String,
    value: String,
    weight: FontWeight = FontWeight.Normal
) {
    Row(modifier = Modifier.fillMaxWidth().semantics { contentDescription = "$label: $value" }) {
        Text(text = "$label:", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color.White, modifier = Modifier.weight(1f))
        Text(text = value, style = MaterialTheme.typography.bodyLarge.copy(fontWeight = weight), color = Color.White.copy(alpha = 0.9f))
    }
}