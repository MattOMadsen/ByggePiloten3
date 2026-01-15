package dk.byggepiloten.firma.ui.screen.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import dk.byggepiloten.firma.data.model.task.Request
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
    val darkTheme = isSystemInDarkTheme()

    val localTasks = remember { mutableStateListOf<Request>() }
    LaunchedEffect(state.requests) {
        localTasks.clear()
        localTasks.addAll(state.requests)
    }

    // Material 3 Pull to Refresh state
    val pullToRefreshState = rememberPullToRefreshState()

    val gradientColors = if (darkTheme) {
        listOf(Color(0xFF0D47A1), Color(0xFF1976D2), Color(0xFF42A5F5))
    } else {
        listOf(ByggePilotenBlue, Color(0xFF42A5F5), Color(0xFF90CAF9))
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(gradientColors))
    ) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("Mine opgaver", color = MaterialTheme.colorScheme.onBackground) },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Tilbage",
                                tint = MaterialTheme.colorScheme.onBackground
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = { viewModel.refreshTasks() }) {
                            Icon(Icons.Default.Refresh, contentDescription = "Opdater", tint = MaterialTheme.colorScheme.onBackground)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
            },
            snackbarHost = { SnackbarHost(snackbarHostState) }
        ) { paddingValues ->
            PullToRefreshBox(
                state = pullToRefreshState,
                isRefreshing = state.isLoading,
                onRefresh = { viewModel.refreshTasks() },
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                if (state.isLoading && localTasks.isEmpty()) {
                    LazyColumn(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(5) { TaskSkeleton() }
                    }
                } else {
                    LazyColumn(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(24.dp)) {
                        if (localTasks.isEmpty()) {
                            item {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(32.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text("Ingen opgaver endnu", fontSize = 18.sp, color = MaterialTheme.colorScheme.onSurface)
                                        Spacer(Modifier.height(8.dp))
                                        Text(
                                            "Når du opretter din første opgave, vil den vises her",
                                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
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
                            val withBids = localTasks.filter { it.bids.isNotEmpty() }
                            val withoutBids = localTasks.filter { it.bids.isEmpty() }

                            if (withBids.isNotEmpty()) {
                                item {
                                    TaskSection(
                                        title = "Håndværkere har budt på",
                                        tasks = withBids,
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
}
