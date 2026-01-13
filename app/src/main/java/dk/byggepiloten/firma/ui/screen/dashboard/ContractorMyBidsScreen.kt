// Fil: app/src/main/java/dk/byggepiloten/firma/ui/screen/dashboard/ContractorMyBidsScreen.kt
// NY FIL – 178 linjer
// Formål: Viser contractor's egne afgivne bud (historik).
// UI: Samme stil som ContractorBidsScreen – gradient baggrund, white cards, status-farve.
// Klik på card → naviger til task_detail (for at se opgave + chat/faktura senere).

package dk.byggepiloten.firma.ui.screen.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import dk.byggepiloten.firma.ui.theme.ByggePilotenBlue
import dk.byggepiloten.firma.ui.viewmodel.dashboard.ContractorMyBidsViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContractorMyBidsScreen(
    navController: NavController,
    viewModel: ContractorMyBidsViewModel = hiltViewModel()
) {
    val bids by viewModel.bids.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        ByggePilotenBlue,
                        Color(0xFF42A5F5),
                        Color(0xFF90CAF9)
                    )
                )
            )
    ) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("Mine tilbud", color = Color.White) },
                    actions = {
                        IconButton(onClick = { viewModel.refresh() }) {
                            Icon(Icons.Default.Refresh, contentDescription = "Opdater", tint = Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = ByggePilotenBlue)
                )
            }
        ) { padding ->
            if (isLoading) {
                Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color.White)
                }
            } else if (bids.isEmpty()) {
                Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Ingen tilbud afgivet endnu", style = MaterialTheme.typography.headlineSmall, color = Color.White)
                        Spacer(Modifier.height(8.dp))
                        Text("Gå til nye opgaver og byd!", color = Color.White.copy(alpha = 0.8f))
                    }
                }
            } else {
                LazyColumn(contentPadding = padding) {
                    items(bids, key = { it.bid.id + it.request.id }) { item ->
                        val bid = item.bid
                        val request = item.request
                        ElevatedCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                                .clickable { navController.navigate("task_detail/${request.id}") },
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(request.category, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Color.Black)
                                Spacer(Modifier.height(4.dp))
                                Text("${request.areaM2.toInt()} m² • ${request.roomType}", color = Color.Black.copy(alpha = 0.7f))
                                Spacer(Modifier.height(8.dp))
                                Text("Dit bud: ${bid.price.toInt()} kr", style = MaterialTheme.typography.titleMedium, color = Color.Black)
                                val dateFormat = SimpleDateFormat("dd. MMM yyyy", Locale("da"))
                                Text("Sendt: ${dateFormat.format(Date(bid.timestamp))}", color = Color.Black.copy(alpha = 0.7f))
                                Spacer(Modifier.height(8.dp))
                                val statusColor = when (bid.status.lowercase()) {
                                    "accepted" -> MaterialTheme.colorScheme.primary
                                    "declined" -> MaterialTheme.colorScheme.error
                                    else -> MaterialTheme.colorScheme.onSurface
                                }
                                Text("Status: ${bid.status.replaceFirstChar { it.uppercase() }}", color = statusColor, fontWeight = FontWeight.Medium)
                            }
                        }
                    }
                }
            }
        }
    }
}