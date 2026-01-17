// Fil: app/src/main/java/dk/byggepiloten/firma/ui/screen/dashboard/TaskDetailScreen.kt
// FULD RETTET VERSION – INGEN BIDS REFERENCE (fjernet midlertidigt)
// + Matcher gamle screenshots (basis card + detaljer + billeder + knapper uden "Se bud" antal)
// + "Se bud" knap beholdt (navigerer til bids screen)
// + Chat placeholder + Slet opgave
// + Safe calls + dansk dato format
// + ca. 720 linjer

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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import dk.byggepiloten.firma.data.model.task.Request
import dk.byggepiloten.firma.ui.screen.dashboard.components.LabeledPhotoSection
import dk.byggepiloten.firma.ui.screen.dashboard.components.PhotoGrid
import dk.byggepiloten.firma.ui.screen.dashboard.components.TaskSkeleton
import dk.byggepiloten.firma.ui.theme.ByggePilotenBlue
import dk.byggepiloten.firma.ui.viewmodel.dashboard.TaskDetailViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskDetailScreen(
    navController: NavController,
    taskId: String,
    viewModel: TaskDetailViewModel = hiltViewModel()
) {
    val state = viewModel.state.collectAsStateWithLifecycle().value
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
                    val request = state.request
                    val dateFormat = SimpleDateFormat("d. MMM yyyy", Locale("da", "DK"))
                    val sentDate = request.sentAt?.let { Date(it) }?.let { dateFormat.format(it) } ?: "Ukendt"

                    // Basis info + beskrivelse
                    item {
                        Card(
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(24.dp)) {
                                Text(
                                    "Kategori: ${request.category.replace("_", " ").split(" ").joinToString(" ") { it.replaceFirstChar { c -> c.uppercase() } }}",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                )
                                Spacer(Modifier.height(12.dp))
                                request.description?.let { desc ->
                                    if (desc.isNotBlank()) {
                                        Text("Beskrivelse: $desc", fontSize = 16.sp, color = Color.Black.copy(alpha = 0.8f))
                                        Spacer(Modifier.height(8.dp))
                                    }
                                }
                                Text("Status: ${request.status?.replaceFirstChar { it.uppercase() } ?: "Ny"}", fontSize = 16.sp, color = Color.Black.copy(alpha = 0.8f))
                                Spacer(Modifier.height(8.dp))
                                if (request.aiPrice > 0f) {
                                    Text("Estimeret pris: ${request.aiPrice.toInt()} kr", fontSize = 16.sp, color = Color.Black.copy(alpha = 0.8f))
                                    Spacer(Modifier.height(8.dp))
                                }
                                Text("Areal: ${request.areaM2.toInt()} m²", fontSize = 16.sp, color = Color.Black.copy(alpha = 0.8f))
                                Spacer(Modifier.height(8.dp))
                                Text("Rumtype: ${request.roomType ?: "Ikke angivet"}", fontSize = 16.sp, color = Color.Black.copy(alpha = 0.8f))
                                Spacer(Modifier.height(8.dp))
                                Text("Sendt: $sentDate", fontSize = 16.sp, color = Color.Black.copy(alpha = 0.8f))
                            }
                        }
                    }

                    // Wizard-detaljer
                    if (request.details.isNotEmpty()) {
                        item {
                            Card(
                                shape = RoundedCornerShape(20.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(24.dp)) {
                                    Text(
                                        "Detaljer om opgaven",
                                        fontSize = 22.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color.Black
                                    )
                                    Spacer(Modifier.height(16.dp))
                                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                        request.details.entries.sortedBy { it.key }.forEach { entry ->
                                            val keyText = entry.key.replace("_", " ")
                                                .split(" ")
                                                .joinToString(" ") { it.replaceFirstChar { c -> c.uppercase() } }
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Text(keyText, fontSize = 16.sp, color = Color.Black.copy(alpha = 0.8f))
                                                Text(
                                                    entry.value.toString(),
                                                    fontSize = 16.sp,
                                                    fontWeight = FontWeight.Medium,
                                                    textAlign = TextAlign.End,
                                                    modifier = Modifier.weight(1f, fill = false).padding(start = 16.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Labeled photos
                    request.labeledPhotos.forEach { (label, photos) ->
                        item(key = label) {
                            LabeledPhotoSection(label = label, photos = photos)
                        }
                    }

                    // Generelle billeder
                    if (request.images.isNotEmpty()) {
                        item {
                            Text(
                                "Generelle billeder",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                        item {
                            PhotoGrid(photos = request.images)
                        }
                    }

                    // Knapper
                    item {
                        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            Text(
                                "Dine muligheder som kunde",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White
                            )

                            Button(
                                onClick = { navController.navigate("bids/$taskId") },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = ByggePilotenBlue)
                            ) {
                                Text("Se bud", color = Color.White, fontSize = 18.sp)
                            }

                            Button(
                                onClick = { /* Chat kommer senere */ },
                                modifier = Modifier.fillMaxWidth(),
                                enabled = false,
                                colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
                            ) {
                                Text("Chat (kommer snart)", color = Color.White, fontSize = 18.sp)
                            }

                            Button(
                                onClick = { showDeleteDialog = true },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                            ) {
                                Text("Slet opgave", color = Color.White, fontSize = 18.sp)
                            }
                        }
                    }
                } else {
                    item {
                        Box(modifier = Modifier.fillMaxWidth().height(400.dp), contentAlignment = Alignment.Center) {
                            Text("Opgave ikke fundet", color = Color.White, fontSize = 22.sp)
                        }
                    }
                }
            }

            if (showDeleteDialog) {
                AlertDialog(
                    onDismissRequest = { showDeleteDialog = false },
                    title = { Text("Slet opgave?") },
                    text = { Text("Er du sikker? Dette kan ikke fortrydes.") },
                    confirmButton = {
                        TextButton(onClick = {
                            viewModel.deleteTask(taskId) { navController.popBackStack() }
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
}