// Fil: app/src/main/java/dk/byggepiloten/firma/ui/screen/dashboard/TaskDetailScreen.kt
// FULD OPDATERET – matcher MainActivity routes
// + Reel slet med dialog + progress
// + Titel-fix + bidsCount + conditional knapper
// + Teaser fallback hvis ingen billeder
// + Knapper: "Vis alle billeder" → task_images/{taskId}
// + "Vis fulde detaljer" → task_full_details/{taskId}
// Ca. 320 linjer

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
    val state by viewModel.state.collectAsStateWithLifecycle()
    val darkTheme = isSystemInDarkTheme()

    var showDeleteDialog by remember { mutableStateOf(false) }

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
                    val request: Request = state.request!!

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
                                        request.roomType?.let {
                                            if (it.isNotBlank() && it.lowercase() != request.category.lowercase()) {
                                                append(" – $it")
                                            }
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

                                request.description?.let { desc ->
                                    if (desc.isNotBlank()) {
                                        Text(
                                            text = desc,
                                            style = MaterialTheme.typography.bodyLarge,
                                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f)
                                        )
                                        Spacer(Modifier.height(20.dp))
                                    }
                                }

                                // Teaser billede med fallback
                                val firstImage = request.images.firstOrNull()
                                if (firstImage != null) {
                                    AsyncImage(
                                        model = firstImage,
                                        contentDescription = "Teaser billede",
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(240.dp)
                                            .clip(RoundedCornerShape(12.dp)),
                                        contentScale = ContentScale.Crop
                                    )
                                } else {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(240.dp)
                                            .background(Color.LightGray.copy(alpha = 0.3f), RoundedCornerShape(12.dp)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("Ingen billeder uploadet", color = Color.Gray, fontSize = 18.sp)
                                    }
                                }

                                Spacer(Modifier.height(32.dp))

                                // Separate screen knapper (øverst i row)
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

                                Spacer(Modifier.height(16.dp))

                                // Andre knapper
                                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                    if (state.isContractor && request.status == "new") {
                                        Button(
                                            onClick = { /* TODO: Naviger til byd-flow */ },
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

                                    if (state.isOwner) {
                                        Button(
                                            onClick = { showDeleteDialog = true },
                                            colors = ButtonDefaults.buttonColors(containerColor = Color.Red.copy(alpha = 0.8f)),
                                            modifier = Modifier.fillMaxWidth(),
                                            enabled = !state.isDeleting
                                        ) {
                                            if (state.isDeleting) {
                                                CircularProgressIndicator(
                                                    color = Color.White,
                                                    modifier = Modifier.size(20.dp),
                                                    strokeWidth = 2.dp
                                                )
                                                Spacer(Modifier.width(8.dp))
                                            }
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

        // Delete confirm dialog
        if (showDeleteDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                title = { Text("Slet opgave?") },
                text = { Text("Denne handling kan ikke fortrydes.") },
                confirmButton = {
                    TextButton(onClick = {
                        viewModel.deleteTask(taskId,
                            onSuccess = { navController.popBackStack() },
                            onError = { }
                        )
                        showDeleteDialog = false
                    }) {
                        Text("Slet", color = Color.Red)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteDialog = false }) {
                        Text("Annuller")
                    }
                }
            )
        }
    }
}