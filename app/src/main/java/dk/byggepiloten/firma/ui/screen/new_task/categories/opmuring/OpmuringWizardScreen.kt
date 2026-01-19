// Fil: app/src/main/java/dk/byggepiloten/firma/ui/screen/new_task/categories/opmuring/OpmuringWizardScreen.kt
// FIX: Fjernet verticalScroll herfra, da WizardScaffold allerede håndterer scroll.
// Dette løser "Infinite maximum height constraints" crashet.

package dk.byggepiloten.firma.ui.screen.new_task.categories.opmuring

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import dk.byggepiloten.firma.ui.screen.new_task.components.WizardScaffold
import dk.byggepiloten.firma.ui.viewmodel.task.OpmuringTaskViewModel

@Composable
fun OpmuringWizardScreen(
    navController: NavController
) {
    val viewModel: OpmuringTaskViewModel = hiltViewModel()
    val data by viewModel.wallData.collectAsStateWithLifecycle()
    val stepPhotos by viewModel.stepPhotos.collectAsStateWithLifecycle()
    val errorMessage by viewModel.error.collectAsStateWithLifecycle()
    val isSending by viewModel.isSending.collectAsStateWithLifecycle()

    var currentStepIndex by remember { mutableIntStateOf(0) }

    val totalSteps = 17
    val progress by derivedStateOf { (currentStepIndex + 1f) / totalSteps }
    val currentStepNumber = currentStepIndex + 1

    val needsPudsarmering by derivedStateOf {
        val surface = data.surfaceFinish.orEmpty().lowercase()
        surface.contains("puds") || surface.contains("malet") || surface.contains("filt")
    }

    val needsDamageStep by derivedStateOf { data.isRepair == true }

    val isStepValid by derivedStateOf {
        if ((currentStepNumber == 10 && !needsPudsarmering) ||
            (currentStepNumber == 13 && !needsDamageStep)) {
            true
        } else {
            OpmuringValidator.isStepValid(data, stepPhotos, currentStepNumber)
        }
    }

    val tryNext = {
        if (isStepValid || currentStepNumber == totalSteps) {
            if (currentStepIndex < totalSteps - 1) {
                currentStepIndex++
                viewModel.clearError()
            } else {
                viewModel.sendTask {
                    navController.navigate("dashboard") { popUpTo(0) }
                }
            }
        } else {
            viewModel.setError("Udfyld venligst alle påkrævede felter før du går videre")
        }
    }

    LaunchedEffect(currentStepNumber) {
        if (currentStepNumber == totalSteps) {
            viewModel.calculateAndGenerateEstimate()
        }
    }

    WizardScaffold(
        title = "Opmuring",
        progress = progress,
        onNavigationBack = { navController.popBackStack() },
        onPrevious = {
            if (currentStepIndex > 0) {
                currentStepIndex--
                viewModel.clearError()
            } else {
                navController.popBackStack()
            }
        },
        onNext = tryNext,
        isNextEnabled = isStepValid || currentStepNumber == totalSteps,
        nextButtonText = if (currentStepNumber == totalSteps) if (isSending) "Sender..." else "Send opgave" else "Næste"
    ) {
        // VIGTIGT: Ingen .verticalScroll() her, da WizardScaffold har det i sin definition.
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            if (errorMessage != null) {
                Text(
                    text = errorMessage!!,
                    color = Color.Red,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(bottom = 24.dp)
                )
            }

            when (currentStepNumber) {
                1 -> OpmuringMurTypeStep(viewModel = viewModel)
                2 -> OpmuringNewOrRepairStep(viewModel = viewModel)
                3 -> OpmuringBearingWallStep(viewModel = viewModel)
                4 -> OpmuringDimensionsStep(viewModel = viewModel)
                5 -> OpmuringThicknessStep(viewModel = viewModel)
                6 -> OpmuringStoneStep(viewModel = viewModel)
                7 -> OpmuringMortarStep(viewModel = viewModel)
                8 -> OpmuringOpeningsStep(viewModel = viewModel)
                9 -> OpmuringSurfaceStep(viewModel = viewModel)
                10 -> {
                    if (needsPudsarmering) {
                        OpmuringArmeringStep(viewModel = viewModel)
                    } else {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = "Armeringsnet ikke relevant",
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.White,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )
                            Text(
                                text = "Ved rå, blank eller ubehandlet murværk pudses der ikke, og armeringsnet i pudslag er derfor ikke nødvendigt.\nDette trin springes automatisk over.",
                                style = MaterialTheme.typography.bodyLarge,
                                color = Color.White
                            )
                        }
                    }
                }
                11 -> OpmuringInsulationStep(viewModel = viewModel)
                12 -> OpmuringFoundationStep(viewModel = viewModel)
                13 -> {
                    if (needsDamageStep) {
                        OpmuringDamageStep(viewModel = viewModel)
                    } else {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = "Skader ikke relevant",
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.White,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )
                            Text(
                                text = "Dette er en ny opmuring, så skader på eksisterende mur er ikke relevant.\nTrinnet springes automatisk over.",
                                style = MaterialTheme.typography.bodyLarge,
                                color = Color.White
                            )
                        }
                    }
                }
                14 -> OpmuringAccessStep(viewModel = viewModel)
                15 -> OpmuringPhotosStep(viewModel = viewModel)
                16 -> OpmuringDescriptionStep(viewModel = viewModel)
                17 -> OpmuringSummaryStep(data = data, viewModel = viewModel, isSending = isSending)
            }
        }
    }
}
