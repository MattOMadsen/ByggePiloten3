// Fil: app/src/main/java/dk/byggepiloten/firma/ui/screen/photos/TaskPhotosDescriptionScreen.kt
package dk.byggepiloten.firma.ui.screen.photos

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
            "facade_pudsning" -> hiltViewModel<FacadeTaskViewModel>(wizardBackStackEntry)
            else -> hiltViewModel<BaseTaskViewModel>()
        }
    } else {
        when (category) {
            "fliser" -> hiltViewModel<FliserTaskViewModel>()
            "badeværelse" -> hiltViewModel<BadevaerelseTaskViewModel>()
            "opmuring" -> hiltViewModel<OpmuringTaskViewModel>()
            "facade_pudsning" -> hiltViewModel<FacadeTaskViewModel>()
            else -> hiltViewModel<BaseTaskViewModel>()
        }
    }

    val description by viewModel.description.collectAsStateWithLifecycle()
    val imageUris by viewModel.imageUris.collectAsStateWithLifecycle()
    val aiPriceEstimate by viewModel.aiPriceEstimate.collectAsStateWithLifecycle()
    val isGeneratingEstimate by viewModel.isGeneratingEstimate.collectAsStateWithLifecycle()
    val isSending by viewModel.isSending.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val stepPhotosMap by if (viewModel is OpmuringTaskViewModel) {
        viewModel.stepPhotos.collectAsStateWithLifecycle()
    } else {
        remember { mutableStateOf(emptyMap<String, List<Uri>>()) }
    }

    // State til at styre hvilket billede der vises i fuld skærm
    var fullscreenImageUri by remember { mutableStateOf<Uri?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(ByggePilotenBlue, Color(0xFF42A5F5), Color(0xFF90CAF9))))
    ) {
        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            containerColor = Color.Transparent
        ) { padding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                item {
                    Spacer(Modifier.height(32.dp))
                    Text(
                        text = "Sidste step – billeder & beskrivelse",
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                }

                item {
                    AiEstimateSection(isGeneratingEstimate, aiPriceEstimate)
                }

                // Step-billeder grupperet i vandrette rækker
                stepPhotosMap.forEach { (stepId, uris) ->
                    if (uris.isNotEmpty()) {
                        item {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Text(
                                    text = when (stepId) {
                                        "damage" -> "Billeder af skader"
                                        "access" -> "Billeder af adgangsforhold"
                                        "openings" -> "Billeder af åbninger"
                                        else -> "Billeder fra forløbet"
                                    },
                                    color = Color.White,
                                    style = MaterialTheme.typography.titleMedium,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )
                                LazyRow(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    items(uris) { uri ->
                                        AsyncImage(
                                            model = uri,
                                            contentDescription = null,
                                            modifier = Modifier
                                                .size(100.dp) // Små thumbnails
                                                .clip(RoundedCornerShape(8.dp))
                                                .clickable { fullscreenImageUri = uri },
                                            contentScale = ContentScale.Crop
                                        )
                                    }
                                }
                            }
                        }
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
        }

        // Dialog til visning af billede i fuld skærm
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
