// Fil: app/src/main/java/dk/byggepiloten/firma/ui/screen/dashboard/TaskDetailScreen.kt
// FULD RETTET VERSION – alle knapper tilbage med korrekt visibility
// + Bruger state.bidsCount, state.isContractor, state.isOwner
// + Titel uden duplikering
// + Description + teaser billede + areal + pris
// + Knapper: Vis billeder/detaljer, Byd (contractor + new), Se bud (bids > 0), Slet (owner)
// + Placeholder for Chat (kommende)
// + Alle imports + clip + sp
// ca. 650 linjer

package dk.byggepiloten.firma.ui.screen.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import dk.byggepiloten.firma.data.model.task.Request
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
    val darkTheme = isSystemInDarkTheme()

    LaunchedEffect(taskId) {
        viewModel.loadTask(taskId)
    }

    val gradientColors = if (darkTheme) {
        listOf(Color(0xFF0D47A1), Color(0xFF1976D2), Color(0xFF42A5F5))
    } else {
        listOf(ByggePilotenBlue, Color(0xFF42A5F5), Color(0xFF90CAF9))
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(gradientColors))
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
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent)
                )
            }
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                if (state.isLoading) {
                    items(4) { TaskSkeleton() }
                } else if (state.request != null) {
                    val request = state.request

                    item {
                        Card(
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)),
                            elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(24.dp)) {
                                // Titel uden duplikering
                                Text(
                                    text = buildString {
                                        append(request.category.replaceFirstChar { it.uppercase() })
                                        if (!request.roomType.isNullOrBlank() && request.roomType.lowercase() != request.category.lowercase()) {
                                            append(" – ${request.roomType}")
                                        }
                                    },
                                    fontSize = 26.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )

                                Spacer(Modifier.height(16.dp))

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    if (request.areaM2 > 0f) {
                                        Text(
                                            "${request.areaM2.toInt()} m²",
                                            fontSize = 20.sp,
                                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                                        )
                                        Spacer(Modifier.width(24.dp))
                                    }
                                    StatusBadge(status = request.status ?: "new")
                                    if (state.bidsCount > 0) {
                                        Spacer(Modifier.width(16.dp))
                                        Text(
                                            "${state.bidsCount} bud",
                                            fontSize = 18.sp,
                                            color = MaterialTheme.colorScheme.primary,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }

                                Spacer(Modifier.height(20.dp))

                                if (request.aiPrice > 0f) {
                                    Text(
                                        text = "Ca. pris: ${request.aiPrice.toInt()}–${(request.aiPrice * 1.3f).toInt()} kr.",
                                        fontSize = 20.sp,
                                        color = MaterialTheme.colorScheme.primary,
                                        fontWeight = FontWeight.Medium
                                    )
                                }

                                Spacer(Modifier.height(20.dp))

                                // Description
                                val descriptionText = request.description ?: ""
                                if (descriptionText.isNotBlank()) {
                                    Text(
                                        text = descriptionText,
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f)
                                    )
                                    Spacer(Modifier.height(20.dp))
                                }

                                // Teaser billede
                                val firstImage = request.images.firstOrNull()
                                if (firstImage != null) {
                                    AsyncImage(
                                        model = firstImage,
                                        contentDescription = "Teaser billede",
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(220.dp)
                                            .clip(RoundedCornerShape(12.dp)),
                                        contentScale = ContentScale.Crop
                                    )
                                } else {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(220.dp)
                                            .background(Color.LightGray.copy(alpha = 0.3f))
                                            .clip(RoundedCornerShape(12.dp)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("Ingen billeder", color = Color.Gray, fontSize = 18.sp)
                                    }
                                }

                                Spacer(Modifier.height(32.dp))

                                // Knapper
                                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                        Button(
                                            onClick = { navController.navigate("task_images/$taskId") },
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Text("Vis alle billeder")
                                        }
                                        Button(
                                            onClick = { navController.navigate("task_full_details/$taskId") },
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Text("Vis fulde detaljer")
                                        }
                                    }

                                    if (state.isContractor && request.status == "new") {
                                        Button(
                                            onClick = { /* TODO: Naviger til byd-flow / OfferEditorScreen */ },
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Text("Byd på opgave")
                                        }
                                    }

                                    if (state.bidsCount > 0) {
                                        Button(
                                            onClick = { navController.navigate("bids/$taskId") },
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Text("Se bud")
                                        }
                                    }

                                    // Placeholder for chat (kommende)
                                    // Button(onClick = { /* Chat */ }, modifier = Modifier.fillMaxWidth()) { Text("Chat") }

                                    if (state.isOwner) {
                                        Button(
                                            onClick = { /* TODO: Slet opgave + undo snackbar */ },
                                            colors = ButtonDefaults.buttonColors(containerColor = Color.Red.copy(alpha = 0.8f)),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Text("Slet opgave")
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else {
                    item {
                        Box(modifier = Modifier.fillMaxWidth().height(500.dp), contentAlignment = Alignment.Center) {
                            Text("Opgave ikke fundet", fontSize = 22.sp, color = Color.White)
                        }
                    }
                }
            }
        }
    }
}