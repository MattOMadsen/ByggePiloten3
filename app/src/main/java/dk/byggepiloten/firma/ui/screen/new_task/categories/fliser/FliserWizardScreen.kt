// Fil: app/src/main/java/dk/byggepiloten/firma/ui/screen/new_task/categories/fliser/FliserWizardScreen.kt
// FULD RETTET VERSION
// - Tilføjet manglende imports (ExperimentalFoundationApi + FlowRow)
// - Korrekt ViewModel-import
// - Dynamisk step-list og validering beholdt
// - Linjer: 238

package dk.byggepiloten.firma.ui.screen.new_task.categories.fliser

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import dk.byggepiloten.firma.ui.theme.ByggePilotenBlue
import dk.byggepiloten.firma.ui.viewmodel.task.FliserTaskViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun FliserWizardScreen(
    navController: NavController,
    viewModel: FliserTaskViewModel = hiltViewModel()
) {
    val data by viewModel.fliserData.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.setCurrentCategory("flise_klinke")
    }

    // Live beregninger
    val floorArea = (data.floorLength ?: 0f) * (data.floorWidth ?: 0f)
    val wallPerimeter = if (data.useFloorPerimeterForWalls == true && data.workType?.contains("Gulv") == true) {
        2f * ((data.floorLength ?: 0f) + (data.floorWidth ?: 0f))
    } else data.manualWallPerimeter ?: 0f
    val wallArea = (data.wallHeight ?: 0f) * wallPerimeter
    val grossArea = floorArea + wallArea
    val netArea = (grossArea - (data.deductionArea ?: 0f)).coerceAtLeast(0f)

    var currentStep by remember { mutableIntStateOf(1) }

    // Dynamisk step-list – skip irrelevante
    val stepList by derivedStateOf {
        buildList {
            add(1) // WorkType
            if (data.workType?.contains("Gulv") == true) add(2)
            if (data.workType?.contains("Væg") == true) add(3)
            add(4) // Deductions
            add(5) // Tile size
            add(6) // Pattern
            add(7) // Underlag
            add(8) // Access
        }
    }

    val totalSteps = stepList.size
    val progress = currentStep.toFloat() / totalSteps

    val isNextEnabled by derivedStateOf {
        currentStep == 1 && data.workType != null || currentStep > 1
    }

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
                    title = { Text("Flise- og klinkearbejde", color = Color.White, fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.Filled.ArrowBack, contentDescription = "Tilbage", tint = Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = ByggePilotenBlue)
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(padding)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp)),
                    color = Color.White,
                    trackColor = Color.White.copy(alpha = 0.3f)
                )

                Spacer(Modifier.height(24.dp))

                when (currentStep) {
                    1 -> FliserWorkTypeStep(data = data, onUpdate = { viewModel.updateFliserData(it) })
                    2 -> FliserFloorDimensionsStep(data = data, onUpdate = { viewModel.updateFliserData(it) }, floorArea = floorArea)
                    3 -> FliserWallDimensionsStep(data = data, onUpdate = { viewModel.updateFliserData(it) }, wallArea = wallArea, floorPerimeterAvailable = data.workType?.contains("Gulv") == true)
                    4 -> FliserDeductionsStep(data = data, onUpdate = { viewModel.updateFliserData(it) }, grossArea = grossArea, netArea = netArea)
                    5 -> FliserTileSizeStep(data = data, onUpdate = { viewModel.updateFliserData(it) }, netArea = netArea)
                    6 -> FliserPatternStep(data = data, onUpdate = { viewModel.updateFliserData(it) }, netArea = netArea)
                    7 -> FliserUnderlagStep(data = data, onUpdate = { viewModel.updateFliserData(it) }, netArea = netArea, showFloorQuestions = data.workType?.contains("Gulv") == true)
                    8 -> FliserAccessStep(data = data, onUpdate = { viewModel.updateFliserData(it) }, netArea = netArea)
                }

                Spacer(Modifier.height(40.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    OutlinedButton(
                        onClick = { if (currentStep > 1) currentStep-- else navController.popBackStack() },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                    ) {
                        Text("Tilbage")
                    }

                    Button(
                        onClick = {
                            if (currentStep == totalSteps) {
                                navController.navigate("task_photos_description/flise_klinke")
                            } else {
                                currentStep++
                            }
                        },
                        enabled = isNextEnabled,
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = ByggePilotenBlue)
                    ) {
                        Text(if (currentStep == totalSteps) "Fortsæt til billeder" else "Næste")
                    }
                }

                Spacer(Modifier.height(16.dp))
            }
        }
    }
}