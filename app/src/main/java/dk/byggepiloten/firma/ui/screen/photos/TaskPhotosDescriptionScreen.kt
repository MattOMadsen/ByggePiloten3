// Fil: app/src/main/java/dk/byggepiloten/firma/ui/screen/photos/TaskPhotosDescriptionScreen.kt
// OPDATERET – Tilføjet bounded height (heightIn(max = 400.dp)) på LazyVerticalGrid for step-billeder for at undgå infinite height constraints
// Beholdt al original struktur + imports
// Linjer: 252

package dk.byggepiloten.firma.ui.screen.photos

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import kotlinx.coroutines.launch
import android.net.Uri

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskPhotosDescriptionScreen(
    navController: NavController,
    category: String = ""
) {
    val viewModel: BaseTaskViewModel = when (category) {
        "fliser" -> hiltViewModel<FliserTaskViewModel>()
        "badeværelse" -> hiltViewModel<BadevaerelseTaskViewModel>()
        "opmuring" -> hiltViewModel<OpmuringTaskViewModel>()
        "facade_pudsning" -> hiltViewModel<FacadeTaskViewModel>()
        else -> hiltViewModel<BaseTaskViewModel>()
    }

    val description by viewModel.description.collectAsStateWithLifecycle()
    val imageUris by viewModel.imageUris.collectAsStateWithLifecycle()
    val aiPriceEstimate by viewModel.aiPriceEstimate.collectAsStateWithLifecycle()
    val isGeneratingEstimate by viewModel.isGeneratingEstimate.collectAsStateWithLifecycle()
    val isSending by viewModel.isSending.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val stepPhotos by if (viewModel is OpmuringTaskViewModel) {
        viewModel.stepPhotos.collectAsStateWithLifecycle()
    } else {
        remember { mutableStateOf(emptyMap<String, List<Uri>>()) }
    }

    LaunchedEffect(category) {
        if (category.isNotBlank()) {
            viewModel.setCurrentCategory(category)
        }
    }

    LaunchedEffect(description, imageUris) {
        viewModel.generateAiEstimate()
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color.Transparent
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(ByggePilotenBlue, Color(0xFF42A5F5), Color(0xFF90CAF9))
                    )
                )
                .padding(padding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.height(32.dp))

                Text(
                    text = "Sidste step – billeder & beskrivelse",
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(24.dp))

                AiEstimateSection(
                    isGeneratingEstimate = isGeneratingEstimate,
                    aiPriceEstimate = aiPriceEstimate
                )

                Spacer(Modifier.height(32.dp))

                // Grupperede step-billeder (bounded height for at undgå infinite constraints)
                for ((stepId, uris) in stepPhotos) {
                    if (uris.isNotEmpty()) {
                        Text(
                            text = when (stepId) {
                                "damage" -> "Billeder af skader"
                                "access" -> "Billeder af adgangsforhold"
                                "openings" -> "Billeder af åbninger"
                                "foundation" -> "Billeder af fundament"
                                else -> "Billeder fra $stepId"
                            },
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White,
                            modifier = Modifier.padding(vertical = 16.dp)
                        )

                        LazyVerticalGrid(
                            columns = GridCells.Fixed(3),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.heightIn(max = 400.dp) // Bounded height – fixer infinite constraints
                        ) {
                            items(uris.size) { index ->
                                val uri = uris[index]
                                AsyncImage(
                                    model = uri,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .aspectRatio(1f)
                                        .clip(RoundedCornerShape(8.dp)),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }

                        Spacer(Modifier.height(24.dp))
                    }
                }

                DescriptionSection(
                    description = description,
                    onDescriptionChange = { viewModel.updateDescription(it) }
                )

                Spacer(Modifier.height(32.dp))

                ImageSelectionSection(
                    viewModel = viewModel,
                    imageUris = imageUris
                )

                SendTaskSection(
                    viewModel = viewModel,
                    imageUris = imageUris,
                    navController = navController,
                    snackbarHostState = snackbarHostState,
                    scope = scope
                )
            }
        }
    }
}