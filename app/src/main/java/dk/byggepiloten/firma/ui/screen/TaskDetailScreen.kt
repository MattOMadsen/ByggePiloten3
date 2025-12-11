package dk.byggepiloten.firma.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import dagger.hilt.android.lifecycle.HiltViewModel
import dk.byggepiloten.firma.data.model.Request
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskDetailScreen(navController: NavController, taskId: String) {
    val viewModel: TaskDetailViewModel = hiltViewModel()
    val task by viewModel.task.collectAsStateWithLifecycle()

    LaunchedEffect(taskId) {
        viewModel.loadTask(taskId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Opgavedetaljer") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Tilbage")
                    }
                }
            )
        }
    ) { padding ->
        if (task == null) {
            Box(Modifier.fillMaxSize().padding(padding), Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Opgave: ${task?.category}", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(8.dp))
                        Text("Beskrivelse: ${task?.description ?: "Ingen"}")
                        Text("Status: ${task?.status ?: "Ny"}")
                        Text("Estimeret pris: ${task?.aiPrice} kr")
                    }
                }

                Button(onClick = { navController.navigate("chat") }, modifier = Modifier.fillMaxWidth()) {
                    Text("Åbn chat")
                }

                Button(onClick = { navController.navigate("invoice") }, modifier = Modifier.fillMaxWidth()) {
                    Text("Send faktura")
                }

                Button(onClick = { /* Implement bedømmelse */ }, modifier = Modifier.fillMaxWidth()) {
                    Text("Giv bedømmelse")
                }
            }
        }
    }
}

@HiltViewModel
class TaskDetailViewModel @Inject constructor() : ViewModel() {
    private val _task = MutableStateFlow<Request?>(null)
    val task: StateFlow<Request?> = _task

    fun loadTask(taskId: String) {
        viewModelScope.launch {
            // TILFØJET: Load fra repo (eksempel) – integrer real RequestRepository.
            val loadedTask = Request(
                id = taskId,
                category = "Murerarbejde",
                description = "Beskrivelse her",
                status = "Afventer",
                aiPrice = 85000f,
                areaM2 = 50f,
                roomType = "Badeværelse",
                userId = "user_id",
                role = "private",  // TILFØJET: Matcher fejl i TaskDetailScreen.kt linje 103 (tilføjet role til Request).
                fag = "Murer"  // TILFØJET: Matcher fejl i TaskDetailScreen.kt linje 103 (tilføjet fag til Request).
            )
            _task.value = loadedTask
            Timber.d("Loaded task: $loadedTask")
        }
    }
}