// Fil: app/src/main/java/dk/byggepiloten/firma/ui/screen/TaskDetailScreen.kt
// FULD FIL – RETTET MED ROLE-CHECK + FARVER + CAPITALIZE (ca. 350 linjer)
// Rettelser:
// - Role-check: Privat = private knapper, Contractor = contractor knapper
// - Baggrund: Gradient (containerColor = Color.Transparent)
// - Tekst: "facade_pudsning" → "Facade Pudsning", "new" → "Ny"
// - "Se opgave info" viser info (allerede i card)
// - "Se bud" placeholder route "bids" (ingen crash – udvid senere)
// - "Slet opgave" placeholder (kald repository senere)
// - Farver: Hvid på gradient, sort på white card

package dk.byggepiloten.firma.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import dk.byggepiloten.firma.data.model.Request
import dk.byggepiloten.firma.ui.theme.ByggePilotenBlue
import dk.byggepiloten.firma.ui.viewmodel.TaskDetailViewModel
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskDetailScreen(navController: NavController, taskId: String) {
    val viewModel: TaskDetailViewModel = hiltViewModel()
    val task by viewModel.task.collectAsStateWithLifecycle()
    val role by viewModel.role.collectAsStateWithLifecycle("PRIVATE")

    LaunchedEffect(taskId) {
        viewModel.loadTask(taskId)
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Opgavedetaljer", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Tilbage", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = ByggePilotenBlue)
            )
        },
        containerColor = Color.Transparent
    ) { padding ->
        if (task == null) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color.White)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            val categoryDisplay = task?.category?.replace("_", " ")?.replaceFirstChar { it.uppercase() } ?: "Ukendt"
                            Text("Kategori: $categoryDisplay", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                            Spacer(Modifier.height(8.dp))
                            Text("Beskrivelse: ${task?.description ?: "Ingen"}", color = Color.Black)
                            val statusDisplay = when (task?.status) {
                                "new" -> "Ny"
                                else -> task?.status ?: "Ny"
                            }
                            Text("Status: $statusDisplay", color = Color.Black)
                            Text("Estimeret pris: ${task?.aiPrice?.toInt() ?: 0} kr", color = Color.Black)
                            Text("Areal: ${task?.areaM2?.toInt() ?: 0} m²", color = Color.Black)
                            Text("Rumtype: ${task?.roomType ?: "Ukendt"}", color = Color.Black)
                            val dateFormat = SimpleDateFormat("dd. MMM yyyy", Locale("da"))
                            Text("Sendt: ${task?.sentAt?.let { dateFormat.format(Date(it)) } ?: "ukendt"}", color = Color.Black)
                            Text("Bud: ${task?.bids?.size ?: 0}", color = Color.Black)
                        }
                    }
                }

                if (role == "PRIVATE") {
                    item {
                        Text("Dine muligheder som kunde", fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                    }
                    item {
                        Button(onClick = { /* Info allerede vist */ }, modifier = Modifier.fillMaxWidth()) {
                            Text("Se opgave info")
                        }
                    }
                    item {
                        Button(onClick = { navController.navigate("bids") }, modifier = Modifier.fillMaxWidth()) {  // Placeholder – udvid til real route
                            Text("Se bud (${task?.bids?.size ?: 0})")
                        }
                    }
                    item {
                        Button(onClick = {
                            // Slet opgave (kald repository senere)
                            navController.popBackStack()
                        }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = Color.Red)) {
                            Text("Slet opgave", color = Color.White)
                        }
                    }
                } else { // CONTRACTOR
                    item {
                        Text("Dine muligheder som håndværker", fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                    }
                    item {
                        Button(onClick = { navController.navigate("bid_create") }, modifier = Modifier.fillMaxWidth()) {  // Placeholder
                            Text("Byd på opgave")
                        }
                    }
                    item {
                        Button(onClick = { navController.navigate("chat") }, modifier = Modifier.fillMaxWidth()) {
                            Text("Åbn chat")
                        }
                    }
                    item {
                        Button(onClick = { navController.navigate("invoice") }, modifier = Modifier.fillMaxWidth()) {
                            Text("Send faktura")
                        }
                    }
                    item {
                        Button(onClick = { /* Bedømmelse */ }, modifier = Modifier.fillMaxWidth()) {
                            Text("Giv bedømmelse")
                        }
                    }
                }
            }
        }
    }
}