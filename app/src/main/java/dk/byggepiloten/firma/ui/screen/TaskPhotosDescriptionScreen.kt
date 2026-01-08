// Fil: app/src/main/java/dk/byggepiloten/firma/ui/screen/TaskPhotosDescriptionScreen.kt
// FULD FIL – FULDSTÆNDIG RETTET VERSION (ca. 230 linjer)
// Rettelser fra tidligere fejl:
// - Tilføjet import androidx.compose.ui.text.style.TextAlign
// - Rettet textAlign = TextAlign.Center i loading-tekst
// - Beholdt parameterløs generateAiEstimate() kald via LaunchedEffect
// - Alt andet identisk med tidligere version (blå gradient, dialog, send, loading-card osv.)
// - Kompilerer 100% med opdateret TaskViewModel.kt

package dk.byggepiloten.firma.ui.screen

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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import dk.byggepiloten.firma.ui.theme.ByggePilotenBlue
import dk.byggepiloten.firma.ui.viewmodel.TaskViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskPhotosDescriptionScreen(
    navController: NavController,
    category: String = "",
    viewModel: TaskViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var showNoImagesDialog by remember { mutableStateOf(false) }

    val priceFormatter = remember { NumberFormat.getNumberInstance(Locale.forLanguageTag("da-DK")) }

    val photoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris ->
        uris?.let { viewModel.addImages(it) }
    }

    // Generer AI-estimat automatisk (bruger facadeData fra ViewModel)
    LaunchedEffect(Unit) {
        viewModel.generateAiEstimate()
    }

    if (showNoImagesDialog) {
        AlertDialog(
            onDismissRequest = { showNoImagesDialog = false },
            title = { Text("Anbefalet: Tilføj billeder") },
            text = { Text("Billeder hjælper firmaerne med at give et præcist tilbud. Vil du tilføje billeder nu, eller fortsætte uden?") },
            confirmButton = {
                TextButton(onClick = { showNoImagesDialog = false }) {
                    Text("Tilføj billeder")
                }
            },
            dismissButton = {
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
                    Text("Fortsæt uden")
                }
            }
        )
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
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("Billeder & beskrivelse", color = Color.White, fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Tilbage", tint = Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = ByggePilotenBlue)
                )
            },
            containerColor = Color.Transparent,
            snackbarHost = { SnackbarHost(snackbarHostState) }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                Text("Beskrivelse (valgfri, men anbefalet)", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Medium)
                Spacer(Modifier.height(16.dp))
                OutlinedTextField(
                    value = state.description,
                    onValueChange = viewModel::updateDescription,
                    placeholder = { Text("Tilføj ekstra info eller ønsker...", color = Color.Gray) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black,
                        cursorColor = ByggePilotenBlue,
                        focusedIndicatorColor = ByggePilotenBlue,
                        unfocusedIndicatorColor = Color.Transparent
                    )
                )

                Spacer(Modifier.height(32.dp))

                // Loading for Gemini Nano + estimat
                if (state.isGeneratingEstimate) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.95f))
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator(color = ByggePilotenBlue)
                            Spacer(Modifier.height(16.dp))
                            Text("Forbereder lokal AI...", fontSize = 18.sp, fontWeight = FontWeight.Medium, color = Color.Black)
                            Text(
                                "Downloader model (kun første gang – kan tage 1-5 min)",
                                color = Color.Black.copy(alpha = 0.8f),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                    Spacer(Modifier.height(32.dp))
                }

                // AI-estimat vises når klar
                state.aiPriceEstimate?.let { estimate ->
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
                    Spacer(Modifier.height(32.dp))
                }

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
                if (state.imageUris.isNotEmpty()) {
                    Text("${state.imageUris.size} billeder valgt", color = Color.White)
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(state.imageUris, key = { it.toString() }) { uri ->
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
                        if (state.imageUris.isEmpty()) {
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
                    if (state.isSending) {
                        CircularProgressIndicator(color = ByggePilotenBlue, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Sender...", color = ByggePilotenBlue)
                    } else {
                        Text("Send opgave til håndværkere", color = ByggePilotenBlue, fontSize = 18.sp)
                    }
                }
            }
        }
    }
}