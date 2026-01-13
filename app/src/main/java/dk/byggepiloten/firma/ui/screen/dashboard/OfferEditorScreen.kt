// app/src/main/java/dk/byggepiloten/firma/ui/screen/OfferEditorScreen.kt
// OPDATERET: Rettet deprecation – brug Icons.AutoMirrored.Default.Send.
// Beholdt alt andet 100% uændret.

package dk.byggepiloten.firma.ui.screen.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send  // RETTET: Brug AutoMirrored – løser deprecation.
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import dk.byggepiloten.firma.ui.theme.ByggePilotenTheme
import dk.byggepiloten.firma.ui.viewmodel.dashboard.OfferViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OfferEditorScreen(
    navController: NavController,
    taskId: String
) {
    val viewModel: OfferViewModel = hiltViewModel()
    val offers by viewModel.offers.collectAsStateWithLifecycle()
    var priceText by remember { mutableStateOf("") }
    var comment by remember { mutableStateOf("") }
    val price = priceText.toFloatOrNull() ?: 0f
    val isValid = price > 0f
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val existingOffersForTask = offers.filter { it.taskId == taskId }

    ByggePilotenTheme {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("Giv tilbud på opgave #$taskId") },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Tilbage")
                        }
                    }
                )
            },
            snackbarHost = { SnackbarHost(snackbarHostState) }
        ) { padding ->
            LazyColumn(
                modifier = Modifier
                    .padding(padding)
                    .padding(16.dp)
            ) {
                item {
                    OutlinedTextField(
                        value = priceText,
                        onValueChange = {
                            priceText = it.filter { char -> char.isDigit() || char == ',' || char == '.' }
                        },
                        label = { Text("Din pris (kr inkl. moms)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item { Spacer(Modifier.height(16.dp)) }
                item {
                    OutlinedTextField(
                        value = comment,
                        onValueChange = { comment = it },
                        label = { Text("Kommentar til kunden (valgfri)") },
                        minLines = 4,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    Button(
                        onClick = {
                            viewModel.saveOffer(taskId, price, comment)
                            priceText = ""
                            comment = ""
                            Timber.d("Tilbud sendt: $price kr på opgave $taskId")
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar(
                                    message = "Tilbud sendt! Kunden får besked med det samme.",
                                    actionLabel = "OK",
                                    duration = SnackbarDuration.Long
                                )
                                delay(1500)
                                navController.popBackStack()
                            }
                        },
                        enabled = isValid,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Send tilbud")
                    }
                }
                if (existingOffersForTask.isNotEmpty()) {
                    item { Spacer(Modifier.height(32.dp)) }
                    item { Text("Dine tidligere tilbud på denne opgave", style = MaterialTheme.typography.titleMedium) }
                    items(existingOffersForTask.reversed()) { offer ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("${offer.price} kr", style = MaterialTheme.typography.titleLarge)
                                if (offer.comment.isNotBlank()) {
                                    Text(offer.comment, style = MaterialTheme.typography.bodyMedium)
                                }
                                Text("Sendt nu", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun OfferEditorScreenPreview() {
    ByggePilotenTheme {
        OfferEditorScreen(
            navController = rememberNavController(),
            taskId = "12345"
        )
    }
}