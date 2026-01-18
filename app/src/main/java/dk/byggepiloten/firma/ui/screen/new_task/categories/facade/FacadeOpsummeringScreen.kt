package dk.byggepiloten.firma.ui.screen.new_task.categories.facade

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import dk.byggepiloten.firma.ui.screen.dashboard.components.PhotoGrid
import dk.byggepiloten.firma.ui.screen.new_task.components.WizardScaffold
import dk.byggepiloten.firma.ui.viewmodel.task.FacadeTaskViewModel
import java.text.NumberFormat
import java.util.Locale

@Composable
fun FacadeOpsummeringScreen(
    navController: NavController,
    viewModel: FacadeTaskViewModel = hiltViewModel()
) {
    val facadeData by viewModel.facadeData.collectAsStateWithLifecycle()
    val generalImages by viewModel.imageUris.collectAsStateWithLifecycle()
    val aiEstimate by viewModel.aiPriceEstimate.collectAsStateWithLifecycle()

    WizardScaffold(
        title = "Facadepudsning – Opsummering",
        progress = 9f / 9f,
        onNavigationBack = { navController.popBackStack() },
        onPrevious = { navController.popBackStack() },
        onNext = {
            navController.navigate("task_photos_description/facade_pudsning") {
                popUpTo("facade_pudsning") { inclusive = true }
            }
        },
        isNextEnabled = true,
        nextButtonText = "Til billeder & beskrivelse"
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxWidth().heightIn(max = 1000.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.95f)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            "Opsummering af dine valg",
                            style = MaterialTheme.typography.titleLarge,
                            color = Color.Black
                        )
                        Spacer(Modifier.height(16.dp))

                        facadeData.area?.let {
                            val formatted = NumberFormat.getInstance(Locale("da", "DK")).format(it)
                            DetailRow("Areal", "$formatted m²")
                        }
                        facadeData.vaegtype?.let { DetailRow("Vægtype", it) }
                        facadeData.andenVaegtype?.let { DetailRow("Anden vægtype", it) }
                        facadeData.hojde?.let {
                            val formatted = NumberFormat.getInstance(Locale("da", "DK")).format(it)
                            DetailRow("Bygningshøjde", "$formatted m")
                        }
                        facadeData.stilladsNoedvendigt?.let { DetailRow("Stillads nødvendigt", it) }
                        facadeData.stilladsAdgang?.let { DetailRow("Adgang til stillads", it) }
                        facadeData.stilladsTrapper?.let { DetailRow("Trapper/adgangsveje", it) }
                        facadeData.armeringsnet?.let { DetailRow("Armeringsnet", it) }
                        facadeData.isolering?.let { DetailRow("Isolering", it) }
                        facadeData.isoleringType?.let { DetailRow("Isoleringstype", it) }
                        facadeData.underlagRevner?.let { DetailRow("Revner i underlag", it) }
                        facadeData.underlagFugt?.let { DetailRow("Fugt i underlag", it) }
                        facadeData.underlagGammelPuds?.let { DetailRow("Gammel puds", it) }
                        facadeData.vejretidspunkt?.let { DetailRow("Udførelsestidspunkt", it) }
                        facadeData.haeftemoertelType?.let { DetailRow("Hæftemørtel", it) }
                        facadeData.andenHaeftemoertel?.let { DetailRow("Anden hæftemørtel", it) }
                        facadeData.durapudsFarve?.let { DetailRow("DuraPuds farve", it) }
                        facadeData.skalcemFarve?.let { DetailRow("Skalcem farve", it) }

                        Spacer(Modifier.height(16.dp))

                        aiEstimate?.let {
                            val low = it.toInt()
                            val high = (it * 1.3f).toInt()
                            val formattedLow = NumberFormat.getInstance(Locale("da", "DK")).format(low)
                            val formattedHigh = NumberFormat.getInstance(Locale("da", "DK")).format(high)
                            Text(
                                "Estimeret pris: $formattedLow–$formattedHigh kr.",
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.Black
                            )
                        }
                    }
                }
            }

            if (generalImages.isNotEmpty()) {
                item {
                    PhotoGrid(
                        photos = generalImages.map { it.toString() },
                        title = "Dine billeder af facaden"
                    )
                }
            }
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "$label:",
            style = MaterialTheme.typography.bodyLarge,
            color = Color.Black
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            color = Color.Black.copy(alpha = 0.8f)
        )
    }
}
