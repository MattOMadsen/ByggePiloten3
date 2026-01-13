// Fil: app/src/main/java/dk/byggepiloten/firma/ui/screen/new_task/categories/facade/FacadePudsningWizardScreen.kt
// FULD RETTET – collectAsState, navController til OpsummeringStep, linjer: 202

package dk.byggepiloten.firma.ui.screen.new_task.categories.facade

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import dk.byggepiloten.firma.ui.screen.new_task.components.WizardScaffold
import dk.byggepiloten.firma.ui.viewmodel.task.FacadeTaskViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FacadePudsningWizardScreen(
    navController: NavController
) {
    val viewModel: FacadeTaskViewModel = hiltViewModel()
    val data by viewModel.facadeData.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.setCurrentCategory("facade_pudsning")
    }

    var currentStep by remember { mutableIntStateOf(1) }
    val totalSteps = 7
    val progress = currentStep.toFloat() / totalSteps

    LaunchedEffect(data.vaegtype) {
        if (data.vaegtype == "Mursten" && data.armeringsnet == null) {
            viewModel.updateFacadeData(data.copy(armeringsnet = "Ja"))
        }
    }

    val isNextEnabled by derivedStateOf {
        when (currentStep) {
            1 -> data.area?.let { it > 0f } == true && data.vaegtype != null
            else -> true
        }
    }

    WizardScaffold(
        title = "Facadepudsning",
        progress = progress,
        onNavigationBack = { navController.popBackStack() },
        onPrevious = { if (currentStep > 1) currentStep-- else navController.popBackStack() },
        onNext = {
            if (currentStep == totalSteps) {
                navController.navigate("task_photos_description/facade_pudsning")
            } else {
                currentStep++
            }
        },
        isNextEnabled = isNextEnabled,
        nextButtonText = if (currentStep == totalSteps) "Fortsæt til billeder" else "Næste"
    ) {
        when (currentStep) {
            1 -> FacadeAreaVaegtypeStep(data = data, onUpdate = { viewModel.updateFacadeData(it) })
            2 -> FacadeStilladsStep(data = data, onUpdate = { viewModel.updateFacadeData(it) })
            3 -> FacadeArmeringIsoleringStep(data = data, onUpdate = { viewModel.updateFacadeData(it) })
            4 -> FacadeUnderlagStep(data = data, onUpdate = { viewModel.updateFacadeData(it) })
            5 -> FacadeVejrStep(data = data, onUpdate = { viewModel.updateFacadeData(it) })
            6 -> FacadeHaeftemoertelStep(data = data, onUpdate = { viewModel.updateFacadeData(it) })
            7 -> FacadeOpsummeringStep(data = data, navController = navController)
        }
    }
}