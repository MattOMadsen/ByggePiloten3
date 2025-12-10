// app/src/main/java/dk/byggepiloten/firma/ui/screen/TaskPhotosDescriptionScreen.kt
// OPDATERET: Rettet deprecation – brug Locale.forLanguageTag("da-DK").
// Beholdt alt andet 100% uændret.

package dk.byggepiloten.firma.ui.screen

import android.net.Uri
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
import java.text.NumberFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskPhotosDescriptionScreen(
    navController: NavController,
    viewModel: TaskViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Pris-format med danske tusindseparatorer
    val priceFormatter = remember { NumberFormat.getNumberInstance(Locale.forLanguageTag("da-DK")) }  // RETTET: Brug forLanguageTag – løser deprecation.

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