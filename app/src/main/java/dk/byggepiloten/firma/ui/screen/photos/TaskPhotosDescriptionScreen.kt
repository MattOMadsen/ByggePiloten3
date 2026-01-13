// Fil: app/src/main/java/dk/byggepiloten/firma/ui/screen/photos/TaskPhotosDescriptionScreen.kt
// OPDATERET – automatisk AI-start ved ændring i description/billeder
// Linjer: 192

package dk.byggepiloten.firma.ui.screen.photos

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
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

    LaunchedEffect(category) {
        if (category.isNotBlank()) {
            viewModel.setCurrentCategory(category)
        }
    }

    // Automatisk AI-start ved ændring i description eller billeder
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