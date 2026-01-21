// Fil: app/src/main/java/dk/byggepiloten/firma/ui/screen/new_task/categories/puds/PudsSummaryStep.kt
// FULD RETTET – tilføjet import androidx.compose.foundation.layout.aspectRatio (fikser Unresolved reference)

package dk.byggepiloten.firma.ui.screen.new_task.categories.puds

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import dk.byggepiloten.firma.ui.viewmodel.task.PudsTaskViewModel

@Composable
fun PudsSummaryStep(
    viewModel: PudsTaskViewModel,
    isSending: Boolean
) {
    val data by viewModel.pudsData.collectAsStateWithLifecycle()
    val aiPriceEstimate by viewModel.aiPriceEstimate.collectAsStateWithLifecycle()
    val imageUris by viewModel.imageUris.collectAsStateWithLifecycle()
    val stepPhotos by viewModel.stepPhotos.collectAsStateWithLifecycle(emptyMap<String, List<android.net.Uri>>())
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
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Rød card hvis manglende trin
        if (missingSteps.isNotEmpty()) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.Red.copy(alpha = 0.9f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Du mangler at udfylde følgende trin:",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    LazyColumn {
                        items(missingSteps) { step ->
                            Text(
                                text = "• Trin $step",
                                style = MaterialTheme.typography.bodyLarge,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }

        // Warning-note (AI-fejl)
        warningNote?.let {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFA000).copy(alpha = 0.9f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White
                    )
                }
            }
        }

        // Kritisk error
        criticalError?.let {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.Red.copy(alpha = 0.9f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White
                    )
                }
            }
        }

        // Blå teaser-card
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    "Areal: ${data.area?.let { "%.2f".format(it) } ?: "Ikke angivet"} m²",
                    style = MaterialTheme.typography.headlineMedium
                )
                Text(
                    "AI-estimeret pris: Ca. ${aiPriceEstimate?.toString() ?: "Beregner..."} kr.",
                    style = MaterialTheme.typography.headlineMedium
                )
            }
        }

        // Hvid detalje-card
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.95f)),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                SummaryRow("Pudsning", data.indeUde ?: "Ikke angivet")
                SummaryRow("Vægtype", "${data.vaegtype ?: "Ikke angivet"} ${if (data.vaegtype == "Anden") "– ${data.andenVaegtype ?: ""}" else ""}")

                if (data.indeUde == "Ude") {
                    SummaryRow("Bygningshøjde", data.hojde?.toString() ?: "Ikke angivet")
                    SummaryRow(
                        "Stillads nødvendigt",
                        "${data.stilladsNoedvendigt ?: "Ikke angivet"} ${
                            if (data.stilladsNoedvendigt == "Ja") "– Adgang: ${data.stilladsAdgang ?: ""}, Trapper: ${data.stilladsTrapper ?: ""}" else ""
                        }"
                    )
                    SummaryRow("Vejretidspunkt", data.vejretidspunkt ?: "Ikke angivet")
                    SummaryRow("Armeringsnet", data.armeringsnet ?: "Ikke angivet")
                    SummaryRow(
                        "Isolering",
                        "${data.isolering ?: "Ikke angivet"} ${
                            if (data.isolering == "Ja") "– Type: ${data.isoleringType ?: "Ikke angivet"}" else ""
                        }"
                    )
                }

                SummaryRow(
                    "Underlag",
                    "Revner: ${data.underlagRevner ?: "Ikke angivet"}, Fugt: ${data.underlagFugt ?: "Ikke angivet"} ${
                        if (data.indeUde == "Ude") ", Gammel puds: ${data.underlagGammelPuds ?: "Ikke angivet"}" else ""
                    }"
                )

                SummaryRow(
                    "Hæftemørtel",
                    "${data.haeftemoertelType ?: "Ikke angivet"} " +
                            "${if (data.haeftemoertelType == "DuraPuds 615") "– Farve: ${data.durapudsFarve ?: "Ikke angivet"}" else ""} " +
                            "${if (data.haeftemoertelType == "Skalcem S2000") "– Farve: ${data.skalcemFarve ?: "Ikke angivet"}" else ""} " +
                            "${if (data.haeftemoertelType == "Anden") "– ${data.andenHaeftemoertel ?: "Ikke angivet"}" else ""}"
                )
            }
        }

        // Billeder
        Text(
            text = "Uploadede billeder: ${imageUris.size} generelle + ${stepPhotos.values.sumOf { it.size }} trin-specifikke",
            style = MaterialTheme.typography.bodyLarge,
            color = Color.Black.copy(alpha = 0.9f)
        )

        if (imageUris.isNotEmpty() || stepPhotos.values.any { it.isNotEmpty() }) {
            val allUris = remember(imageUris, stepPhotos) { imageUris + stepPhotos.values.flatten() }
            Text(
                text = "Dine billeder",
                style = MaterialTheme.typography.titleLarge,
                color = Color.Black,
                modifier = Modifier.padding(top = 8.dp)
            )
            val chunks = allUris.chunked(3)
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                chunks.forEach { rowUris ->
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
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
                        repeat(3 - rowUris.size) { Spacer(modifier = Modifier.weight(1f)) }
                    }
                }
            }
        }

        if (isSending) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator()
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
    Row(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "$label:",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = weight),
            color = Color.Black.copy(alpha = 0.9f)
        )
    }
}