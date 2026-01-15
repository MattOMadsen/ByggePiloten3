// Fil: app/src/main/java/dk/byggepiloten/firma/ui/screen/dashboard/TasksScreen.kt
// RETTET VERSION – FJERNET pull-to-refresh midlertidigt (pga. unresolved reference)
// + Beholdt manuel refresh i top bar
// + Rettet TaskSection kald (onLongClick virker nu)
// + Alle andre imports ok
// + Snackbar undo virker (local + TODO server delete)
// Vi tilføjer pull-to-refresh igen når du har opdateret Compose Material3 til 1.2.1+

package dk.byggepiloten.firma.ui.screen.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import dk.byggepiloten.firma.ui.screen.dashboard.components.TaskCard
import dk.byggepiloten.firma.ui.screen.dashboard.components.TaskSection
import dk.byggepiloten.firma.ui.screen.dashboard.components.TaskSkeleton
import dk.byggepiloten.firma.ui.theme.ByggePilotenBlue
import dk.byggepiloten.firma.ui.viewmodel.dashboard.TasksViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TasksScreen(
    navController: NavController,
    viewModel: TasksViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val localTasks = remember { mutableStateListOf<Request>() }
    LaunchedEffect(state.requests) {
        localTasks.clear()
        localTasks.addAll(state.requests)
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
            containerColor = Color.Transparent,
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("Mine opgaver", color = Color.White) },
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
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = ByggePilotenBlue)
                )
            },
            snackbarHost = { SnackbarHost(snackbarHostState) }
        ) { paddingValues ->
            if (state.isLoading && localTasks.isEmpty()) {
                LazyColumn(
                    modifier = Modifier
                        .padding(paddingValues)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(5) { TaskSkeleton() }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .padding(paddingValues)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    if (localTasks.isEmpty()) {
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White)
                            ) {
                                Column(
                                    modifier = Modifier.padding(32.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text("Ingen opgaver endnu", fontSize = 18.sp, color = Color.Black)
                                    Spacer(Modifier.height(8.dp))
                                    Text(
                                        "Når du opretter din første opgave, vil den vises her",
                                        color = Color.Black.copy(alpha = 0.8f),
                                        textAlign = TextAlign.Center
                                    )
                                    Spacer(Modifier.height(16.dp))
                                    Button(onClick = { navController.navigate("new_task") }) {
                                        Text("Opret ny opgave")
                                    }
                                }
                            }
                        }
                    } else {
                        val withBids = localTasks.filter { it.bids.size > 0 }
                        val withoutBids = localTasks.filter { it.bids.size == 0 }

                        if (withBids.isNotEmpty()) {
                            item {
                                TaskSection(
                                    title = "Håndværkere har budt på",
                                    tasks = withBids,
                                    onTaskClick = { navController.navigate("task_detail/${it.id}") },
                                    onLongClick = { request ->
                                        val index = localTasks.indexOf(request)
                                        localTasks.remove(request)
                                        // TODO: viewModel.deleteTask(request.id)
                                        coroutineScope.launch {
                                            val result = snackbarHostState.showSnackbar(
                                                "Opgave slettet",
                                                actionLabel = "Fortryd",
                                                duration = SnackbarDuration.Short
                                            )
                                            if (result == SnackbarResult.ActionPerformed) {
                                                localTasks.add(index, request)
                                            }
                                        }
                                    }
                                )
                            }
                        }

                        if (withoutBids.isNotEmpty()) {
                            item {
                                TaskSection(
                                    title = "Ingen bud endnu",
                                    tasks = withoutBids,
                                    onTaskClick = { navController.navigate("task_detail/${it.id}") },
                                    onLongClick = { request ->
                                        val index = localTasks.indexOf(request)
                                        localTasks.remove(request)
                                        coroutineScope.launch {
                                            val result = snackbarHostState.showSnackbar(
                                                "Opgave slettet",
                                                actionLabel = "Fortryd",
                                                duration = SnackbarDuration.Short
                                            )
                                            if (result == SnackbarResult.ActionPerformed) {
                                                localTasks.add(index, request)
                                            }
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}