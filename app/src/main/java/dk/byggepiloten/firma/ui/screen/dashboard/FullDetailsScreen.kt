// Fil: app/src/main/java/dk/byggepiloten/firma/ui/screen/dashboard/FullDetailsScreen.kt
// OPDATERET – NU VISER ALLE DETALJER FRA REQUEST (inkl. generelle felter + fuld detailsMap)
// + Danske labels for ALLE kendte keys fra wizard-kategorier (facade, murer, fliser, badeværelse osv.)
// + Sektioner: Generelt, Beskrivelse, Detaljer (sorteret)
// + Formatering: Boolean → Ja/Nej, Number → dansk format (f.eks. 1234,56 m²)
// + Area + AI-pris vist prominente
// + Fuld imports + kommentarer
// Ca. 280 linjer – fuldt funktionsdygtig

package dk.byggepiloten.firma.ui.screen.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import dk.byggepiloten.firma.ui.theme.ByggePilotenBlue
import dk.byggepiloten.firma.ui.viewmodel.dashboard.TaskDetailViewModel
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FullDetailsScreen(
    navController: NavController,
    taskId: String,
    viewModel: TaskDetailViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(taskId) {
        viewModel.loadTask(taskId)
    }

    val gradientColors = listOf(ByggePilotenBlue, Color(0xFF42A5F5), Color(0xFF90CAF9))

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(gradientColors))
    ) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("Fuldstændige detaljer", color = Color.White) },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Tilbage",
                                tint = Color.White
                            )
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent)
                )
            }
        ) { paddingValues ->
            if (state.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color.White)
                }
            } else {
                val request = state.request

                if (request == null) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Opgave ikke fundet", fontSize = 20.sp, color = Color.White)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .padding(paddingValues)
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(24.dp)
                    ) {
                        // Generelt afsnit
                        item {
                            Text(
                                "Generelt",
                                style = MaterialTheme.typography.headlineMedium,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(Modifier.height(12.dp))

                            DetailRow("Kategori", request.category.replaceFirstChar { it.uppercase() })
                            request.roomType?.let { if (it.isNotBlank()) DetailRow("Rumtype", it) }

                            if (request.areaM2 > 0f) {
                                val formattedArea = NumberFormat.getInstance(Locale("da", "DK")).format(request.areaM2)
                                DetailRow("Areal", "$formattedArea m²")
                            }

                            if (request.aiPrice > 0f) {
                                val low = request.aiPrice.toInt()
                                val high = (request.aiPrice * 1.3f).toInt()
                                val formattedLow = NumberFormat.getInstance(Locale("da", "DK")).format(low)
                                val formattedHigh = NumberFormat.getInstance(Locale("da", "DK")).format(high)
                                DetailRow("Estimeret pris", "$formattedLow–$formattedHigh kr.")
                            }
                            Divider(color = Color.White.copy(alpha = 0.3f))
                        }

                        // Beskrivelse
                        request.description?.let { desc ->
                            if (desc.isNotBlank()) {
                                item {
                                    Text(
                                        "Beskrivelse",
                                        style = MaterialTheme.typography.headlineMedium,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(Modifier.height(12.dp))
                                    Text(
                                        text = desc,
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = Color.White.copy(alpha = 0.9f)
                                    )
                                    Divider(color = Color.White.copy(alpha = 0.3f))
                                }
                            }
                        }

                        // Alle wizard-detaljer
                        val detailsMap = request.details
                        if (detailsMap.isNotEmpty()) {
                            item {
                                Text(
                                    "Detaljer",
                                    style = MaterialTheme.typography.headlineMedium,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(Modifier.height(12.dp))
                            }

                            items(detailsMap.toList().sortedBy { it.first }) { (rawKey, value) ->
                                val formattedValue = when (value) {
                                    is Boolean -> if (value) "Ja" else "Nej"
                                    is Number -> NumberFormat.getInstance(Locale("da", "DK")).format(value)
                                    is List<*> -> value.filterIsInstance<String>().joinToString(", ")
                                    else -> value.toString()
                                }

                                if (formattedValue.isNotBlank() && formattedValue != "null") {
                                    val label = when (rawKey) {
                                        "murType" -> "Murtype"
                                        "isRepair" -> "Nybyg eller reparation"
                                        "bearingWall" -> "Bærende væg"
                                        "wallTotalAreaM2" -> "Samlet vægareal (m²)"
                                        "thicknessOption" -> "Murtykkelse"
                                        "stoneType" -> "Stentype"
                                        "foundationOption" -> "Fundament"
                                        "netArea" -> "Netto areal (m²)"
                                        "facadeType" -> "Facadetype"
                                        "currentCondition" -> "Nuværende tilstand"
                                        "desiredFinish" -> "Ønsket finish"
                                        "scaffoldNeeded" -> "Stillads nødvendigt"
                                        "windowCount" -> "Antal vinduer"
                                        "doorCount" -> "Antal døre"
                                        "tileType" -> "Flisetype"
                                        "tileSize" -> "Flisestørrelse"
                                        "groutColor" -> "Fugefarve"
                                        "underfloorHeating" -> "Gulvvarme"
                                        "waterproofingNeeded" -> "Vandtætning nødvendig"
                                        "bathroomSizeM2" -> "Badeværelsesstørrelse (m²)"
                                        "showerArea" -> "Bruseniche areal"
                                        "bathtub" -> "Badekar"
                                        else -> rawKey.replaceFirstChar { it.uppercase() }.replace(Regex("([A-Z])")) { " ${it.value.lowercase()}" }
                                    }

                                    DetailRow(label, formattedValue)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "$label:",
            fontWeight = FontWeight.Bold,
            color = Color.White,
            fontSize = 18.sp
        )
        Text(
            text = value,
            color = Color.White.copy(alpha = 0.9f),
            fontSize = 18.sp,
            modifier = Modifier.padding(start = 16.dp)
        )
    }
}