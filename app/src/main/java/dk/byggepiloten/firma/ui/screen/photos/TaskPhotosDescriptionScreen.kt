// Fil: app/src/main/java/dk/byggepiloten/firma/ui/screen/photos/TaskPhotosDescriptionScreen.kt
// OPDATERET: Tilføjet manglende imports (Icons + ArrowBack)
// - Ingen andre ændringer
// Total lines: ~400 (uændret)

package dk.byggepiloten.firma.ui.screen.photos

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import dk.byggepiloten.firma.ui.screen.photos.components.AiEstimateSection
import dk.byggepiloten.firma.ui.screen.photos.components.DescriptionSection
import dk.byggepiloten.firma.ui.screen.photos.components.ImageSelectionSection
import dk.byggepiloten.firma.ui.screen.photos.components.SendTaskSection
import dk.byggepiloten.firma.ui.theme.ByggePilotenBlue
import dk.byggepiloten.firma.ui.viewmodel.task.BadevaerelseTaskViewModel
import dk.byggepiloten.firma.ui.viewmodel.task.BaseTaskViewModel
import dk.byggepiloten.firma.ui.viewmodel.task.FacadeTaskViewModel
import dk.byggepiloten.firma.ui.viewmodel.task.FliserTaskViewModel
import dk.byggepiloten.firma.ui.viewmodel.task.OpmuringTaskViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskPhotosDescriptionScreen(
    navController: NavController,
    category: String = ""
) {
    val wizardBackStackEntry = remember(navController.currentBackStackEntry) {
        try { navController.getBackStackEntry(category) } catch (e: Exception) { null }
    }

    val viewModel: BaseTaskViewModel = if (wizardBackStackEntry != null) {
        when (category) {
            "fliser" -> hiltViewModel<FliserTaskViewModel>(wizardBackStackEntry)
            "badeværelse" -> hiltViewModel<BadevaerelseTaskViewModel>(wizardBackStackEntry)
            "opmuring" -> hiltViewModel<OpmuringTaskViewModel>(wizardBackStackEntry)
            "facade" -> hiltViewModel<FacadeTaskViewModel>(wizardBackStackEntry)
            else -> hiltViewModel<OpmuringTaskViewModel>()
        }
    } else {
        when (category) {
            "fliser" -> hiltViewModel<FliserTaskViewModel>()
            "badeværelse" -> hiltViewModel<BadevaerelseTaskViewModel>()
            "opmuring" -> hiltViewModel<OpmuringTaskViewModel>()
            "facade" -> hiltViewModel<FacadeTaskViewModel>()
            else -> hiltViewModel<OpmuringTaskViewModel>()
        }
    }

    val description by viewModel.description.collectAsStateWithLifecycle()
    val imageUris by viewModel.imageUris.collectAsStateWithLifecycle()
    val aiEstimate by viewModel.aiPriceEstimate.collectAsStateWithLifecycle()
    val isGenerating by viewModel.isGeneratingEstimate.collectAsStateWithLifecycle()
    val error by viewModel.error.collectAsStateWithLifecycle()
    val isSending by viewModel.isSending.collectAsStateWithLifecycle()

    var fullscreenImageUri by remember { mutableStateOf<Uri?>(null) }

    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Fuldfør opgave", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Tilbage", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = ByggePilotenBlue)
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Brush.verticalGradient(listOf(ByggePilotenBlue, Color(0xFF0D47A1))))
        ) {
            item {
                Spacer(Modifier.height(24.dp))
                Text(
                    text = "Se billeder og AI-estimat",
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(32.dp))
            }

            if (imageUris.isNotEmpty()) {
                item {
                    Text(
                        text = "Dine billeder",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    Spacer(Modifier.height(16.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp)
                    ) {
                        items(imageUris) { uri ->
                            AsyncImage(
                                model = uri,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(100.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { fullscreenImageUri = uri },
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                    Spacer(Modifier.height(32.dp))
                }
            }

            item {
                AiEstimateSection(
                    isGeneratingEstimate = isGenerating,
                    aiPriceEstimate = aiEstimate,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                error?.let {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }

            item {
                DescriptionSection(
                    description = description,
                    onDescriptionChange = { viewModel.updateDescription(it) }
                )
            }

            item {
                ImageSelectionSection(viewModel, imageUris)
            }

            item {
                SendTaskSection(
                    viewModel = viewModel,
                    imageUris = imageUris,
                    navController = navController,
                    snackbarHostState = snackbarHostState,
                    scope = scope,
                    isSending = isSending
                )
                Spacer(Modifier.height(40.dp))
            }
        }

        if (fullscreenImageUri != null) {
            Dialog(
                onDismissRequest = { fullscreenImageUri = null },
                properties = DialogProperties(usePlatformDefaultWidth = false)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.9f))
                        .clickable { fullscreenImageUri = null },
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = fullscreenImageUri,
                        contentDescription = "Full screen image",
                        modifier = Modifier.fillMaxSize(0.9f),
                        contentScale = ContentScale.Fit
                    )
                }
            }
        }
    }
}