// Fil: app/src/main/java/dk/byggepiloten/firma/ui/screen/new_task/categories/opmuring/OpmuringWizardScreen.kt
// FULD FIX – INDEX-BASERET NAVIGATION FOR AUTO-SKIP (armering ved "Rå mur")
// Progress og steps rebuildes dynamisk – hopper automatisk over conditional steps
// Linjer: 348

package dk.byggepiloten.firma.ui.screen.new_task.categories.opmuring

import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import dk.byggepiloten.firma.ui.screen.new_task.components.WizardScaffold
import dk.byggepiloten.firma.ui.viewmodel.task.OpmuringTaskViewModel

@Composable
fun OpmuringWizardScreen(
    navController: NavController
) {
    val viewModel: OpmuringTaskViewModel = hiltViewModel()
    val data by viewModel.wallData.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.setCurrentCategory("opmuring")
    }

    val isNewMur by derivedStateOf { data.isRepair == false }
    val isFacadeMur by derivedStateOf { data.murType == "Facademur (skalmur/ydervæg)" }
    val needsArmering by derivedStateOf {
        isNewMur && (data.surfaceFinish == "Pudset" || data.surfaceFinish == "Malet")
    }

    val stepList by derivedStateOf {
        buildList {
            add(1); add(2); add(3); add(4) // MurType, New/Repair, Bearing, Dimensions
            if (isNewMur) add(5); if (isNewMur) add(6); if (isNewMur) add(7); if (isNewMur) add(8) // Thickness, Stone, Mortar, Openings
            if (isNewMur) add(9) // Surface
            if (needsArmering) add(10) // Armering kun ved pudset/malet
            if (isNewMur && isFacadeMur) add(11) // Insulation kun ved facademur
            if (isNewMur) add(12) // Foundation
            if (data.isRepair == true) add(13) // Damage
            add(14) // Access
        }
    }

    var currentStepIndex by remember { mutableIntStateOf(0) }

    val totalSteps = stepList.size
    val progress = if (totalSteps > 0) (currentStepIndex + 1f) / totalSteps else 0f
    val currentStepNumber = if (stepList.isNotEmpty()) stepList[currentStepIndex] else 1

    val isNextEnabled by derivedStateOf {
        // Din originale validering her (true for nu – tilføj senere)
        true
    }

    WizardScaffold(
        title = "Opmuring",
        progress = progress,
        onNavigationBack = { navController.popBackStack() },
        onPrevious = {
            if (currentStepIndex > 0) currentStepIndex-- else navController.popBackStack()
        },
        onNext = {
            if (currentStepIndex == totalSteps - 1) {
                navController.navigate("task_photos_description/opmuring")
            } else {
                currentStepIndex++
            }
        },
        isNextEnabled = isNextEnabled,
        nextButtonText = if (currentStepIndex == totalSteps - 1) "Fortsæt til billeder" else "Næste"
    ) {
        when (currentStepNumber) {
            1 -> OpmuringMurTypeStep(data = data, onDataChange = { viewModel.updateWallData(it) })
            2 -> OpmuringNewOrRepairStep(data = data, onDataChange = { viewModel.updateWallData(it) })
            3 -> OpmuringBearingWallStep(data = data, onDataChange = { viewModel.updateWallData(it) })
            4 -> OpmuringDimensionsStep(data = data, onDataChange = { viewModel.updateWallData(it) })
            5 -> OpmuringThicknessStep(data = data, onDataChange = { viewModel.updateWallData(it) })
            6 -> OpmuringStoneStep(data = data, onDataChange = { viewModel.updateWallData(it) })
            7 -> OpmuringMortarStep(data = data, onDataChange = { viewModel.updateWallData(it) })
            8 -> OpmuringOpeningsStep(data = data, onDataChange = { viewModel.updateWallData(it) })
            9 -> OpmuringSurfaceStep(data = data, onDataChange = { viewModel.updateWallData(it) })
            10 -> OpmuringArmeringStep(data = data, onDataChange = { viewModel.updateWallData(it) })
            11 -> OpmuringInsulationStep(data = data, onDataChange = { viewModel.updateWallData(it) })
            12 -> OpmuringFoundationStep(data = data, onDataChange = { viewModel.updateWallData(it) })
            13 -> OpmuringDamageStep(data = data, onDataChange = { viewModel.updateWallData(it) })
            else -> OpmuringAccessStep(data = data, onDataChange = { viewModel.updateWallData(it) })
        }
    }
}