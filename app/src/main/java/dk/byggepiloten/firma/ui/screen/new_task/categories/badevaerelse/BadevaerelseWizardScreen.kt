// Fil: app/src/main/java/dk/byggepiloten/firma/ui/screen/new_task/categories/badevaerelse/BadevaerelseWizardScreen.kt
// FULD RETTET – collectAsState, klar validering, linjer: 312

package dk.byggepiloten.firma.ui.screen.new_task.categories.badevaerelse

import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import dk.byggepiloten.firma.ui.screen.new_task.components.WizardScaffold
import dk.byggepiloten.firma.ui.viewmodel.task.BadevaerelseTaskViewModel

@Composable
fun BadevaerelseWizardScreen(
    navController: NavController
) {
    val viewModel: BadevaerelseTaskViewModel = hiltViewModel()
    val data by viewModel.badevaerelseData.collectAsState()

    var currentStep by remember { mutableIntStateOf(1) }

    val stepList by derivedStateOf {
        buildList {
            add(1); add(2); add(3); add(4)
            if (data.renovationType == "Fuldt nyt (med nedrivning)") add(5)
            add(6); add(7); add(8); add(9); add(10); add(11); add(12)
        }
    }

    val totalSteps = stepList.size
    val progress = currentStep.toFloat() / totalSteps

    val isNextEnabled by derivedStateOf {
        when (currentStep) {
            1 -> data.renovationType != null
            2 -> data.floorLength?.let { it > 0f } == true && data.floorWidth?.let { it > 0f } == true
            3 -> data.wallHeight?.let { it > 0f } == true
            4 -> data.hasShowerNiche != null && (data.hasShowerNiche == false ||
                    (data.showerLength?.let { it > 0f } == true && data.showerWidth?.let { it > 0f } == true && data.drainType != null))
            5 -> true
            6 -> data.floorTileSize != null
            7 -> data.wallTileSize != null
            8 -> data.hasFloorHeating != null && (data.hasFloorHeating == false || data.floorHeatingType != null)
            9 -> data.hasMembrane != null && data.hasVentilation != null
            10 -> true
            11 -> data.relocatePipes != null && data.relocateElectrical != null
            12 -> data.goodAccess != null && (data.goodAccess == true || data.floorNumber != null)
            else -> true
        }
    }

    WizardScaffold(
        title = "Badeværelse",
        progress = progress,
        onNavigationBack = { navController.popBackStack() },
        onPrevious = { if (currentStep > 1) currentStep-- else navController.popBackStack() },
        onNext = {
            if (currentStep == totalSteps) {
                navController.navigate("task_photos_description/badeværelse")
            } else {
                currentStep++
            }
        },
        isNextEnabled = isNextEnabled,
        nextButtonText = if (currentStep == totalSteps) "Fortsæt til billeder" else "Næste"
    ) {
        when (currentStep) {
            1 -> BadevaerelseRenoveringstypeStep(data = data, onDataChange = { viewModel.updateBadevaerelseData(it) })
            2 -> BadevaerelseGulvDimensionsStep(data = data, onDataChange = { viewModel.updateBadevaerelseData(it) })
            3 -> BadevaerelseVaeggeStep(data = data, onDataChange = { viewModel.updateBadevaerelseData(it) })
            4 -> BadevaerelseBrusenicheStep(data = data, onDataChange = { viewModel.updateBadevaerelseData(it) })
            5 -> BadevaerelseNedrivningStep(data = data, onDataChange = { viewModel.updateBadevaerelseData(it) })
            6 -> BadevaerelseFliserGulvStep(data = data, onDataChange = { viewModel.updateBadevaerelseData(it) })
            7 -> BadevaerelseFliserVaeggeStep(data = data, onDataChange = { viewModel.updateBadevaerelseData(it) })
            8 -> BadevaerelseGulvvarmeStep(data = data, onDataChange = { viewModel.updateBadevaerelseData(it) })
            9 -> BadevaerelseVådrumssikringStep(data = data, onDataChange = { viewModel.updateBadevaerelseData(it) })
            10 -> BadevaerelseÅbningerStep(data = data, onDataChange = { viewModel.updateBadevaerelseData(it) })
            11 -> BadevaerelseRørElStep(data = data, onDataChange = { viewModel.updateBadevaerelseData(it) })
            12 -> BadevaerelseAdgangStep(data = data, onDataChange = { viewModel.updateBadevaerelseData(it) })
        }
    }
}