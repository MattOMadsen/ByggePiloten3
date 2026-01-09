// app/src/main/java/dk/byggepiloten/firma/ui/screen/ContractorBidsScreen.kt
// OPDATERET: Rettet deprecation – brug Locale.forLanguageTag("da-DK").
// Beholdt alt andet 100% uændret.

package dk.byggepiloten.firma.ui.screen.dashboard

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import dk.byggepiloten.firma.ui.viewmodel.ContractorBidViewModel
import java.text.NumberFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContractorBidsScreen(
    navController: NavController,
    viewModel: ContractorBidViewModel = hiltViewModel()
) {
    val requests by viewModel.requests.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val currencyFormat = remember { NumberFormat.getCurrencyInstance(Locale.forLanguageTag("da-DK")) }  // RETTET: Brug forLanguageTag – løser deprecation.

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Tilgængelige opgaver") },
                actions = {
                    IconButton(onClick = { viewModel.refresh() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Opdater liste")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->

        if (isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (requests.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Ingen opgaver lige nu", style = MaterialTheme.typography.headlineSmall)
                    Spacer(Modifier.height(8.dp))
                    Text("Prøv at oprette test-opgaver i Admin Mode", style = MaterialTheme.typography.bodyMedium)
                }
            }
        } else {
            LazyColumn(contentPadding = padding) {
                items(requests, key = { it.id }) { request ->
                    ElevatedCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                            .clickable { navController.navigate("bid_dialog/${request.id}") }
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = request.category,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(Modifier.height(4.dp))
                            Text("${request.areaM2} m² • ${request.roomType}")
                            Spacer(Modifier.height(8.dp))
                            Text(
                                "AI-estimat: ${currencyFormat.format(request.aiPrice)}",
                                color = MaterialTheme.colorScheme.primary,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Spacer(Modifier.height(12.dp))

                            Button(
                                onClick = { navController.navigate("bid_dialog/${request.id}") },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Giv dit bud")
                            }
                        }
                    }
                }
            }
        }
    }
}