// Fil: app/src/main/java/dk/byggepiloten/firma/ui/screen/dashboard/FullDetailsScreen.kt
// FULD RETTET VERSION – compile-fejl løst
// + Bruger toList() + destructuring for at undgå Map.Entry type-konflikter
// + When-expression fuldt rettet (alle cases med quotes + ->)
// + Filtrering + danske labels + formatering beholdt
// + Empty/loading states
// Ca. 320 linjer

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
            } else if (state.request?.details.isNullOrEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Ingen detaljer udfyldt", fontSize = 20.sp, color = Color.White)
                }
            } else {
                val request = state.request!!
                val detailsMap = request.details!!

                // Konverter til List<Pair<String, Any>> + filtrer + sortér
                val filteredPairs = detailsMap.toList()
                    .sortedBy { it.first }
                    .filter { (_, value) ->
                        when (value) {
                            is Boolean -> value // Vis kun true → bliver "Ja"
                            is Number -> value.toFloat() > 0f
                            is String -> value.isNotBlank()
                            is List<*> -> value.isNotEmpty()
                            else -> true
                        }
                    }

                if (filteredPairs.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Ingen detaljer udfyldt", fontSize = 20.sp, color = Color.White)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .padding(paddingValues)
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(filteredPairs) { (rawKey, value) ->
                            // Dansk label override
                            val label = when (rawKey) {
                                "murType" -> "Murtype"
                                "customMurType" -> "Anden murtype"
                                "isRepair" -> "Er det en reparation?"
                                "bearingWall" -> "Bærende væg?"
                                "wallMode" -> "Væg areal måling"
                                "wallTotalAreaM2" -> "Samlet væg areal (m²)"
                                "thicknessOption" -> "Vægtykkelse"
                                "customThickness" -> "Anden tykkelse (mm)"
                                "stoneType" -> "Stentype"
                                "customStoneType" -> "Anden stentype"
                                "mortarType" -> "Mørteltype"
                                "customMortarType" -> "Anden mørteltype"
                                "hasCracks" -> "Revner?"
                                "cracksDescription" -> "Beskriv revner"
                                "hasMoistureDamage" -> "Fugtskader?"
                                "moistureDescription" -> "Beskriv fugtskader"
                                "hasSettlementDamage" -> "Sætningsskader?"
                                "settlementDescription" -> "Beskriv sætningsskader"
                                "openingMode" -> "Åbninger måling"
                                "openingTotalAreaM2" -> "Samlet åbninger areal (m²)"
                                "reinforcement" -> "Armering ønskes?"
                                "surfaceFinish" -> "Overfladebehandling"
                                "customSurface" -> "Anden overflade"
                                "insulationWanted" -> "Isolering ønskes?"
                                "insulationThickness" -> "Isolering tykkelse (mm)"
                                "foundationOption" -> "Fundament"
                                "customFoundation" -> "Andet fundament"
                                "goodAccess" -> "God adgang?"
                                "accessProblems" -> "Adgangsproblemer"
                                "accessCustomDescription" -> "Beskriv adgangsproblemer"
                                "netArea" -> "Netto areal (m²)"
                                else -> rawKey
                                    .replace("_", " ")
                                    .split(" ")
                                    .joinToString(" ") { it.replaceFirstChar { c -> c.uppercase() } }
                            }

                            // Format value
                            val formattedValue = when (value) {
                                is Boolean -> if (value) "Ja" else "Nej"
                                is List<*> -> value.filterIsInstance<String>().joinToString(", ")
                                is Number -> String.format("%.2f", value.toFloat()).trimEnd('0').trimEnd('.')
                                else -> value.toString()
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "$label:",
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = formattedValue,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = Color.White.copy(alpha = 0.9f),
                                    modifier = Modifier
                                        .weight(1f, fill = false)
                                        .padding(start = 16.dp)
                                )
                            }
                            Divider(color = Color.White.copy(alpha = 0.3f))
                        }
                    }
                }
            }
        }
    }
}