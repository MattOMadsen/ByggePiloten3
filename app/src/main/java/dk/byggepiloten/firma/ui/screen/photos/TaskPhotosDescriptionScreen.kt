// Fil: app/src/main/java/dk/byggepiloten/firma/ui/screen/photos/TaskPhotosDescriptionScreen.kt
// FULD OPDATERET – fjernet ikke-eksisterende generateAiEstimate() + bedre loading UI
// Navigation til dashboard ren + progress hvis isGeneratingEstimate sættes i ViewModel
// Linjer: 362

package dk.byggepiloten.firma.ui.screen.photos

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import dk.byggepiloten.firma.ui.theme.ByggePilotenBlue
import dk.byggepiloten.firma.ui.viewmodel.task.BadevaerelseTaskViewModel
import dk.byggepiloten.firma.ui.viewmodel.task.BaseTaskViewModel
import dk.byggepiloten.firma.ui.viewmodel.task.FacadeTaskViewModel
import dk.byggepiloten.firma.ui.viewmodel.task.FliserTaskViewModel
import dk.byggepiloten.firma.ui.viewmodel.task.OpmuringTaskViewModel
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskPhotosDescriptionScreen(
    navController: NavController,
    category: String = "" // fra nav-arg
) {
    // Dynamisk ViewModel baseret på category
    val viewModel: BaseTaskViewModel = when (category) {
        "fliser" -> hiltViewModel<FliserTaskViewModel>()
        "badeværelse" -> hiltViewModel<BadevaerelseTaskViewModel>()
        "opmuring" -> hiltViewModel<OpmuringTaskViewModel>()
        "facade_pudsning" -> hiltViewModel<FacadeTaskViewModel>()
        else -> hiltViewModel<BaseTaskViewModel>() // fallback
    }

    val description by viewModel.description.collectAsStateWithLifecycle()
    val imageUris by viewModel.imageUris.collectAsStateWithLifecycle()
    val aiPriceEstimate by viewModel.aiPriceEstimate.collectAsStateWithLifecycle()
    val isSending by viewModel.isSending.collectAsStateWithLifecycle()
    val isGeneratingEstimate by viewModel.isGeneratingEstimate.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var showNoImagesDialog by remember { mutableStateOf(false) }

    val priceFormatter = remember { NumberFormat.getNumberInstance(Locale.forLanguageTag("da-DK")) }

    val photoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris ->
        uris?.let { viewModel.addImages(it) }
    }

    // Sæt category fra nav-arg
    LaunchedEffect(category) {
        if (category.isNotBlank()) {
            viewModel.setCurrentCategory(category)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color.Transparent
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(ByggePilotenBlue, Color(0xFF42A5F5), Color(0xFF90CAF9))
                    )
                )
                .padding(padding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.height(32.dp))

                Text(
                    text = "Sidste step – billeder & beskrivelse",
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(24.dp))

                // AI-estimat med progress
                if (isGeneratingEstimate) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.95f))
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator(color = ByggePilotenBlue)
                            Spacer(Modifier.height(16.dp))
                            Text(
                                text = "Genererer AI-estimat...",
                                style = MaterialTheme.typography.titleMedium,
                                color = ByggePilotenBlue
                            )
                        }
                    }
                } else if (aiPriceEstimate != null) {
                    val estimate = aiPriceEstimate!!
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.95f))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "${priceFormatter.format(estimate)} kr inkl. moms",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = "Baseret på din opgave – endeligt tilbud kan variere",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                } else {
                    Text(
                        text = "Ingen AI-estimat endnu – tilføj billeder for bedre resultat",
                        color = Color.Yellow,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Spacer(Modifier.height(32.dp))

                Text("Tilføj billeder (anbefales)", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Medium)
                Spacer(Modifier.height(16.dp))
                Button(
                    onClick = { photoPicker.launch("image/*") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White)
                ) {
                    Text("Vælg billeder fra galleri", color = ByggePilotenBlue)
                }

                Spacer(Modifier.height(16.dp))
                if (imageUris.isNotEmpty()) {
                    Text("${imageUris.size} billeder valgt", color = Color.White)
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(imageUris, key = { it.toString() }) { uri ->
                            Box {
                                AsyncImage(
                                    model = uri,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(120.dp)
                                        .clip(RoundedCornerShape(16.dp))
                                )
                                IconButton(
                                    onClick = { viewModel.removeImage(uri) },
                                    modifier = Modifier.align(Alignment.TopEnd)
                                ) {
                                    Icon(Icons.Default.Delete, null, tint = Color.White)
                                }
                            }
                        }
                    }
                } else {
                    Text("Ingen billeder valgt endnu", color = Color.White.copy(alpha = 0.8f))
                }

                Spacer(Modifier.height(40.dp))

                Button(
                    onClick = {
                        if (imageUris.isEmpty()) {
                            showNoImagesDialog = true
                        } else {
                            viewModel.sendTask {
                                scope.launch {
                                    snackbarHostState.showSnackbar("Opgave sendt! Du får besked når der bydes")
                                    navController.navigate("dashboard") {
                                        popUpTo("new_task") { inclusive = true }
                                    }
                                }
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White)
                ) {
                    if (isSending) {
                        CircularProgressIndicator(color = ByggePilotenBlue, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Sender...", color = ByggePilotenBlue)
                    } else {
                        Text("Send opgave til håndværkere", color = ByggePilotenBlue, fontSize = 18.sp)
                    }
                }
            }

            // No-images dialog
            if (showNoImagesDialog) {
                AlertDialog(
                    onDismissRequest = { showNoImagesDialog = false },
                    title = { Text("Ingen billeder?") },
                    text = { Text("Det er stærkt anbefalet at tilføje billeder – håndværkere byder hurtigere og mere præcist. Vil du sende alligevel?") },
                    confirmButton = {
                        TextButton(onClick = {
                            showNoImagesDialog = false
                            viewModel.sendTask {
                                scope.launch {
                                    snackbarHostState.showSnackbar("Opgave sendt! Du får besked når der bydes")
                                    navController.navigate("dashboard") {
                                        popUpTo("new_task") { inclusive = true }
                                    }
                                }
                            }
                        }) {
                            Text("Send alligevel")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showNoImagesDialog = false }) {
                            Text("Tilføj billeder")
                        }
                    }
                )
            }
        }
    }
}