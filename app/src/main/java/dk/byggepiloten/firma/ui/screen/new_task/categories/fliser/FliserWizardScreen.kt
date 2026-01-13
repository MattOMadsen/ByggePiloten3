// Fil: app/src/main/java/dk/byggepiloten/firma/ui/screen/new_task/categories/fliser/FliserWizardScreen.kt
// FULD RETTET – collectAsState, fuld validering, linjer: 286

package dk.byggepiloten.firma.ui.screen.new_task.categories.fliser

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import dk.byggepiloten.firma.ui.screen.new_task.components.WizardScaffold
import dk.byggepiloten.firma.ui.viewmodel.task.FliserTaskViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FliserWizardScreen(
    navController: NavController
) {
    val viewModel: FliserTaskViewModel = hiltViewModel()
    val data by viewModel.fliserData.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.setCurrentCategory("flise_klinke")
    }

    val floorArea = (data.floorLength ?: 0f) * (data.floorWidth ?: 0f)
    val wallPerimeter = if (data.useFloorPerimeterForWalls == true && data.workType?.contains("Gulv") == true) {
        2f * ((data.floorLength ?: 0f) + (data.floorWidth ?: 0f))
    } else data.manualWallPerimeter ?: 0f
    val wallArea = (data.wallHeight ?: 0f) * wallPerimeter
    val grossArea = floorArea + wallArea
    val netArea = (grossArea - (data.deductionArea ?: 0f)).coerceAtLeast(0f)

    var currentStep by remember { mutableIntStateOf(1) }

    val stepList by derivedStateOf {
        buildList {
            add(1)
            if (data.workType?.contains("Gulv") == true) add(2)
            if (data.workType?.contains("Væg") == true) add(3)
            add(4); add(5); add(6); add(7); add(8)
        }
    }

    val totalSteps = stepList.size
    val progress = currentStep.toFloat() / totalSteps

    val isNextEnabled by derivedStateOf {
        when (currentStep) {
            1 -> data.workType != null
            2 -> data.floorLength?.let { it > 0f } == true && data.floorWidth?.let { it > 0f } == true
            3 -> data.wallHeight?.let { it > 0f } == true && (data.useFloorPerimeterForWalls == true || data.manualWallPerimeter?.let { it > 0f } == true)
            4 -> true
            5 -> data.tileSize != null
            6 -> data.pattern != null
            7 -> true
            8 -> data.goodAccess != null && data.needsScaffolding != null
            else -> false
        }
    }

    WizardScaffold(
        title = "Flise- og klinkearbejde",
        progress = progress,
        onNavigationBack = { navController.popBackStack() },
        onPrevious = { if (currentStep > 1) currentStep-- else navController.popBackStack() },
        onNext = {
            if (currentStep == totalSteps) {
                navController.navigate("task_photos_description/flise_klinke")
            } else {
                currentStep++
            }
        },
        isNextEnabled = isNextEnabled,
        nextButtonText = if (currentStep == totalSteps) "Fortsæt til billeder" else "Næste"
    ) {
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
    }
}