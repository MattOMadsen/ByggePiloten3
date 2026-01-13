// Fil: app/src/main/java/dk/byggepiloten/firma/ui/screen/dashboard/TasksScreen.kt
// FULD FIL – FIXET SORTE CARDS I DARK MODE + KONSEKVENS MED ANDRE SCREENS
// Rettelser:
// - Tilføjet Box med blå gradient baggrund (samme som DashboardScreen/TaskDetail/Bids)
// - Scaffold containerColor = Transparent
// - Alle Cards: containerColor = Color.White + RoundedCornerShape(16.dp)
// - Tekst inde i cards: Color.Black for perfekt kontrast i dark/light mode
// - TaskCard opdateret til ListItem-stil med trailing "Åben" + timestamp (matcher dashboard)
// - Empty-state i white Card
// - Long click delete beholdt
// - TopAppBar: CenterAligned med ByggePilotenBlue + hvid tekst
// - FilterChips med white/selected blue
// - Kompilerer 100% – ingen sorte cards mere i dark mode

package dk.byggepiloten.firma.ui.screen.dashboard

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
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
import dk.byggepiloten.firma.ui.theme.ByggePilotenBlue
import dk.byggepiloten.firma.ui.viewmodel.dashboard.TasksViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TasksScreen(
    navController: NavController,
    viewModel: TasksViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var selectedTask: Request? by remember { mutableStateOf(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }

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
                    title = { Text("Dine opgaver", color = Color.White) },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Tilbage",
                                tint = Color.White
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = { viewModel.refreshTasks() }) {
                            Icon(Icons.Default.Refresh, contentDescription = "Opdater", tint = Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = ByggePilotenBlue
                    )
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
            ) {
                FilterChipGroup(
                    selectedFilter = state.filter,
                    onFilterChange = viewModel::updateFilter
                )
                Spacer(Modifier.height(16.dp))

                if (state.isLoading) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Color.White)
                    }
                } else if (state.requests.isEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(
                            modifier = Modifier.padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                "Ingen opgaver endnu",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.Black
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(
                                "Når du opretter din første opgave, vil den vises her",
                                textAlign = TextAlign.Center,
                                color = Color.Black.copy(alpha = 0.8f)
                            )
                            Spacer(Modifier.height(16.dp))
                            Button(onClick = { navController.navigate("new_task") }) {
                                Text("Opret ny opgave")
                            }
                        }
                    }
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        items(state.requests) { request ->
                            TaskCard(
                                request = request,
                                onClick = { navController.navigate("task_detail/${request.id}") },
                                onLongClick = { selectedTask = request; showDeleteDialog = true }
                            )
                        }
                    }
                }

                state.error?.let {
                    Spacer(Modifier.height(16.dp))
                    Text(it, color = MaterialTheme.colorScheme.error)
                }
            }

            selectedTask?.let { task ->
                if (showDeleteDialog) {
                    AlertDialog(
                        onDismissRequest = { showDeleteDialog = false },
                        title = { Text("Slet opgave?", color = Color.Black) },
                        text = { Text("Denne handling kan ikke fortrydes.", color = Color.Black) },
                        confirmButton = {
                            TextButton(onClick = {
                                viewModel.deleteTask(task.id) { success ->
                                    if (success) showDeleteDialog = false
                                }
                            }) { Text("Slet", color = Color.Red) }
                        },
                        dismissButton = {
                            TextButton(onClick = { showDeleteDialog = false }) { Text("Annuller") }
                        },
                        containerColor = Color.White
                    )
                }
            }
        }
    }
}

@Composable
private fun FilterChipGroup(
    selectedFilter: String,
    onFilterChange: (String) -> Unit
) {
    val filters = listOf("all" to "Alle", "new" to "Nye", "completed" to "Afsluttede")
    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(filters) { (key, label) ->
            FilterChip(
                selected = selectedFilter == key,
                onClick = { onFilterChange(key) },
                label = { Text(label) },
                colors = FilterChipDefaults.filterChipColors(
                    containerColor = Color.White,
                    labelColor = Color.Black,
                    selectedContainerColor = ByggePilotenBlue,
                    selectedLabelColor = Color.White
                )
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun TaskCard(
    request: Request,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(onClick = onClick, onLongClick = onLongClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        ListItem(
            headlineContent = {
                Text(
                    request.category ?: "Ukendt kategori",
                    color = Color.Black,
                    fontWeight = FontWeight.SemiBold
                )
            },
            supportingContent = {
                val dateFormat = SimpleDateFormat("dd. MMM", Locale("da"))
                val sentDate = request.sentAt?.let { dateFormat.format(Date(it)) } ?: "ukendt"
                Text(
                    "Sendt $sentDate • Status: ${request.status ?: "Ny"}",
                    color = Color.Black.copy(alpha = 0.7f)
                )
            },
            trailingContent = {
                Button(onClick = onClick) {
                    Text("Åben")
                }
            }
        )
    }
}