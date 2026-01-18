// Fil: app/src/main/java/dk/byggepiloten/firma/ui/screen/dashboard/FullDetailsScreen.kt
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
            } else {
                val request = state.request
                val detailsMap = request?.details ?: emptyMap()

                if (detailsMap.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Ingen detaljer fundet", fontSize = 20.sp, color = Color.White)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .padding(paddingValues)
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Vi viser beskrivelsen øverst hvis den findes
                        if (!request?.description.isNullOrBlank()) {
                            item {
                                DetailRow("Beskrivelse", request?.description ?: "")
                                Divider(color = Color.White.copy(alpha = 0.3f))
                            }
                        }

                        items(detailsMap.toList().sortedBy { it.first }) { (rawKey, value) ->
                            val label = when (rawKey) {
                                "murType" -> "Murtype"
                                "isRepair" -> "Ny/Reparation"
                                "bearingWall" -> "Bærende væg"
                                "wallTotalAreaM2" -> "Areal (m²)"
                                "thicknessOption" -> "Tykkelse"
                                "stoneType" -> "Stentype"
                                "foundationOption" -> "Fundament"
                                "netArea" -> "Netto areal"
                                else -> rawKey.replaceFirstChar { it.uppercase() }
                            }

                            val formattedValue = when {
                                value is Boolean -> if (value) "Ja" else "Nej"
                                value is Number -> String.format("%.2f", value.toDouble()).trimEnd('0').trimEnd('.')
                                else -> value.toString()
                            }

                            // Vi viser kun rækken hvis den ikke er tom
                            if (formattedValue.isNotBlank() && formattedValue != "null") {
                                DetailRow(label, formattedValue)
                                Divider(color = Color.White.copy(alpha = 0.3f))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "$label:",
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            color = Color.White.copy(alpha = 0.9f),
            modifier = Modifier.weight(1.5f)
        )
    }
}
