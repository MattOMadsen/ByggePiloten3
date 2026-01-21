// Fil: app/src/main/java/dk/byggepiloten/firma/ui/screen/new_task/categories/puds/PudsSummaryStep.kt
// NY FIL – conditional opsummering (viser kun relevante felter baseret på inde/ude)

package dk.byggepiloten.firma.ui.screen.new_task.categories.puds

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dk.byggepiloten.firma.ui.viewmodel.task.PudsTaskViewModel

@Composable
fun PudsSummaryStep(
    viewModel: PudsTaskViewModel,
    isSending: Boolean
) {
    val data by viewModel.pudsData.collectAsStateWithLifecycle()
    val aiEstimate by viewModel.aiPriceEstimate.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            "Opsummering – Pudsning",
            style = MaterialTheme.typography.headlineLarge
        )

        // Blå teaser-kort øverst
        Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    "Areal: ${data.area?.let { "%.2f".format(it) } ?: "Ikke angivet"} m²",
                    style = MaterialTheme.typography.headlineMedium
                )
                Text(
                    "AI-estimeret pris: Ca. ${aiEstimate?.toString() ?: "Beregner..."} kr.",
                    style = MaterialTheme.typography.headlineMedium
                )
            }
        }

        // Hvid Card med detaljer
        Card {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("Pudsning: ${data.indeUde ?: "Ikke angivet"}", style = MaterialTheme.typography.bodyLarge)
                Text("Vægtype: ${data.vaegtype ?: "Ikke angivet"} ${if (data.vaegtype == "Anden") "– ${data.andenVaegtype ?: ""}" else ""}", style = MaterialTheme.typography.bodyLarge)

                if (data.indeUde == "Ude") {
                    Text("Bygningshøjde: ${data.hojde?.toString() ?: "Ikke angivet"} m", style = MaterialTheme.typography.bodyLarge)
                    Text("Stillads nødvendigt: ${data.stilladsNoedvendigt ?: "Ikke angivet"} ${if (data.stilladsNoedvendigt == "Ja") "– Adgang: ${data.stilladsAdgang ?: ""}, Trapper: ${data.stilladsTrapper ?: ""}" else ""}", style = MaterialTheme.typography.bodyLarge)
                    Text("Vejretidspunkt: ${data.vejretidspunkt ?: "Ikke angivet"}", style = MaterialTheme.typography.bodyLarge)
                    Text("Armeringsnet: ${data.armeringsnet ?: "Ikke angivet"}", style = MaterialTheme.typography.bodyLarge)
                    Text("Isolering: ${data.isolering ?: "Ikke angivet"} ${if (data.isolering == "Ja") "– Type: ${data.isoleringType ?: "Ikke angivet"}" else ""}", style = MaterialTheme.typography.bodyLarge)
                }

                Text("Underlag – Revner: ${data.underlagRevner ?: "Ikke angivet"}, Fugt: ${data.underlagFugt ?: "Ikke angivet"} ${if (data.indeUde == "Ude") ", Gammel puds: ${data.underlagGammelPuds ?: "Ikke angivet"}" else ""}", style = MaterialTheme.typography.bodyLarge)

                Text("Hæftemørtel: ${data.haeftemoertelType ?: "Ikke angivet"} " +
                        "${if (data.haeftemoertelType == "DuraPuds 615") "– Farve: ${data.durapudsFarve ?: "Ikke angivet"}" else ""} " +
                        "${if (data.haeftemoertelType == "Skalcem S2000") "– Farve: ${data.skalcemFarve ?: "Ikke angivet"}" else ""} " +
                        "${if (data.haeftemoertelType == "Anden") "– ${data.andenHaeftemoertel ?: "Ikke angivet"}" else ""}",
                    style = MaterialTheme.typography.bodyLarge
                )
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
    }
}