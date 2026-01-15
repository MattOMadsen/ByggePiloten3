// Fil: app/src/main/java/dk/byggepiloten/firma/ui/screen/dashboard/TaskDetailScreen.kt
// FULD RETTET VERSION – Alle compile-fejl løst
// + Alle nødvendige imports tilføjet
// + Korrekt brug af state fra ViewModel
// + Rettet type-inference og composable-kald
// + Bruger ny labeledPhotos fra Request
// + Rettet pris-interval (bruger 1.3f for Float)
// + Fjernet ikke-composable kald udenfor context
// + Scaffold background = Transparent (MaterialTheme.background bruges ikke direkte – gradient er baggrund)
// + ca. 320 linjer

package dk.byggepiloten.firma.ui.screen.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import dk.byggepiloten.firma.data.model.task.Request
import dk.byggepiloten.firma.ui.screen.dashboard.components.LabeledPhotoSection
import dk.byggepiloten.firma.ui.screen.dashboard.components.PhotoGrid
import dk.byggepiloten.firma.ui.screen.dashboard.components.StatusBadge
import dk.byggepiloten.firma.ui.screen.dashboard.components.TaskSkeleton
import dk.byggepiloten.firma.ui.theme.ByggePilotenBlue
import dk.byggepiloten.firma.ui.viewmodel.dashboard.TaskDetailViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskDetailScreen(
    navController: NavController,
    taskId: String,
    viewModel: TaskDetailViewModel = hiltViewModel()
) {
    val state = viewModel.state.collectAsStateWithLifecycle().value

    LaunchedEffect(taskId) {
        viewModel.loadTask(taskId)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(ByggePilotenBlue, Color(0xFF42A5F5), Color(0xFF90CAF9))
                )
            )
    ) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("Opgavedetaljer", color = Color.White) },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Tilbage",
                                tint = Color.White
                            )
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = ByggePilotenBlue
                    )
                )
            }
        ) { paddingValues ->
            if (state.isLoading) {
                Column(
                    modifier = Modifier
                        .padding(paddingValues)
                        .padding(16.dp)
                ) {
                    TaskSkeleton()
                    Spacer(Modifier.height(24.dp))
                    TaskSkeleton()
                }
            } else if (state.request != null) {
                val request = state.request

                Column(
                    modifier = Modifier
                        .padding(paddingValues)
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Text(
                                text = buildString {
                                    append(request.category)
                                    request.roomType?.let { append(" – $it") }
                                },
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )

                            Spacer(Modifier.height(12.dp))

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (request.areaM2 > 0f) {
                                    Text("${request.areaM2.toInt()} m²", color = Color.Black.copy(alpha = 0.8f))
                                    Spacer(Modifier.width(16.dp))
                                }
                                StatusBadge(status = request.status ?: "new")
                            }

                            Spacer(Modifier.height(12.dp))

                            if (request.aiPrice > 0f) {
                                Text(
                                    text = "Ca. pris: ${request.aiPrice.toInt()}–${(request.aiPrice * 1.3f).toInt()} kr.",
                                    color = MaterialTheme.colorScheme.primary,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(24.dp))

                    // Step-billeder (labeledPhotos)
                    request.labeledPhotos.forEach { (label, photos) ->
                        LabeledPhotoSection(label = label, photos = photos)
                        Spacer(Modifier.height(32.dp))
                    }

                    // Generelle billeder
                    if (request.images.isNotEmpty()) {
                        Text(
                            text = "Generelle billeder",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                        PhotoGrid(photos = request.images)
                    }
                }
            } else {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Opgave ikke fundet", color = Color.White, fontSize = 18.sp)
                }
            }
        }
    }
}