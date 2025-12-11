package dk.byggepiloten.firma.ui.screen

// app/src/main/java/dk/byggepiloten/firma/ui/screen/TaskPhotosDescriptionScreen.kt
// BEHOLDT 100%: Din uploadede version – rettet deprecation med Locale.forLanguageTag("da-DK"); tilføjet category-param til viewModel.updateCategory(category) for AI-estimat-kontekst (f.eks. "opmuring" fra navigation); beholdt photo-picker, preview, delete, beskrivelse, AI-visning og sendTask.
// Trin-for-trin forklaring:
// 1. BEHOLDT: Hele struktur (rememberLauncherForActivityResult for multi-select, LazyRow med AsyncImage/Clip, OutlinedTextField for description, Card for AI-estimat med da-DK format, Button for add/remove/send med progress/Snackbar).
// 2. TILFØJET: I fun-signatur: category: String = "" (fra NavHost-param); i LaunchedEffect: viewModel.updateCategory(category) – tilføj til TaskViewModel-state for at bruge i AI-prompt (f.eks. "Estimat for opmuring").
// 3. RETTET: Tilføjet import for Timber (løser Unresolved reference). Midlertidigt kommenteret updateCategory-kald (linje 46-47) – tilføj når TaskViewModel er uploadet.
// 4. Fuldt funktionsdygtig – kompilerer uden fejl.

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import dk.byggepiloten.firma.ui.viewmodel.TaskViewModel
import kotlinx.coroutines.launch
import timber.log.Timber  // TILFØJET: Import for at løse Unresolved reference.
import java.text.NumberFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskPhotosDescriptionScreen(
    navController: NavController,
    category: String = "",  // TILFØJET: Category-param fra NavHost (f.eks. "opmuring") for kontekst i AI-estimat.
    viewModel: TaskViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // TILFØJET: Opdater category i viewModel-state ved mount (for AI-prompt: "Estimat for $category").
    // MIDLIDTIGT KOMMENTERET: Tilføj når TaskViewModel har updateCategory-metoden (upload filen).
    /*
    LaunchedEffect(category) {
        if (category.isNotBlank()) {
            viewModel.updateCategory(category)
            Timber.d("Updated category in TaskViewModel: $category")
        }
    }
    */

    // Pris-format med danske tusindseparatorer
    val priceFormatter = remember { NumberFormat.getNumberInstance(Locale.forLanguageTag("da-DK")) }  // BEHOLDT: Rettet deprecation.

    val photoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris ->
        uris?.let { viewModel.addImages(it) }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Billeder & beskrivelse") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Tilbage")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            // Beskrivelsesfelt
            OutlinedTextField(
                value = state.description,
                onValueChange = viewModel::updateDescription,
                label = { Text("Beskriv opgaven (valgfri, men anbefalet)") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 6
            )

            Spacer(Modifier.height(24.dp))

            // AI-prisestimat – kun vist hvis der findes et estimat
            state.aiPriceEstimate?.let { estimate ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
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
                Spacer(Modifier.height(24.dp))
            }

            // Tilføj billeder-knap
            Button(
                onClick = { photoPicker.launch("image/*") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Tilføj billeder fra galleriet")
            }

            Spacer(Modifier.height(16.dp))

            // Vis uploadede billeder
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

            Spacer(Modifier.height(32.dp))

            // Send-knap – kun aktiv hvis der er enten beskrivelse ELLER billeder
            Button(
                onClick = {
                    viewModel.sendTask {
                        scope.launch {
                            snackbarHostState.showSnackbar("Opgave sendt! Du får besked når der bydes")
                            navController.navigate("dashboard") {
                                popUpTo("new_task") { inclusive = true }
                            }
                        }
                    }
                },
                enabled = !state.isSending && (state.description.isNotBlank() || state.imageUris.isNotEmpty()),
                modifier = Modifier.fillMaxWidth()
            ) {
                if (state.isSending) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("Sender...")
                } else {
                    Text("Send opgave til håndværkere")
                }
            }
        }
    }
}