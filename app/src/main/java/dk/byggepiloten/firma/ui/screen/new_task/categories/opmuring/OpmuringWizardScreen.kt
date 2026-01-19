// Fil: app/src/main/java/dk/byggepiloten/firma/ui/screen/new_task/categories/opmuring/OpmuringWizardScreen.kt
// OPDATERET: Irrelevante trin springes helt over (ingen "ikke relevant"-side)
// - Armeringsnet (trin 10) springes hvis !needsPudsarmering (inkl. "Ingen/Rå")
// - Skader (trin 13) springes hvis !needsDamageStep (ny mur)
// - onNext incrementer automatisk indtil relevant trin
// - Fjernet placeholder-branchene i when()
// - Progress og totalSteps beholdt fixed (17) – føles naturligt
// - Validation uændret (skipped trin er altid valid)

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
    val generalUris by viewModel.imageUris.collectAsStateWithLifecycle()
    val errorMessage by viewModel.error.collectAsStateWithLifecycle()
    val isSending by viewModel.isSending.collectAsStateWithLifecycle()

    var currentStepIndex by remember { mutableIntStateOf(0) }

    val totalSteps = 17 // Fixed – brugeren ser kun relevante trin
    val progress by derivedStateOf { (currentStepIndex + 1f) / totalSteps }
    val currentStepNumber = currentStepIndex + 1

    // Udvidet: "Ingen/Rå" betyder ingen puds → ingen armering
    val needsPudsarmering by derivedStateOf {
        val surface = data.surfaceFinish.orEmpty().lowercase()
        surface.contains("puds") || surface.contains("malet") || surface.contains("filt") || surface.contains("skalcem") || surface.contains("dura") || surface.contains("vandskuring")
    }

    val needsDamageStep by derivedStateOf { data.isRepair == true }

    // Hjælpefunktion: Er dette trin irrelevant?
    fun isStepSkipped(step: Int): Boolean {
        return when (step) {
            10 -> !needsPudsarmering
            13 -> !needsDamageStep
            else -> false
        }
    }

    val isStepValid by derivedStateOf {
        if (isStepSkipped(currentStepNumber)) true
        else OpmuringValidator.isStepValid(data, stepPhotos, currentStepNumber)
    }

    val tryNext = {
        if (isStepValid || currentStepNumber == totalSteps) {
            if (currentStepIndex < totalSteps - 1) {
                currentStepIndex++
                viewModel.clearError()

                // Spring irrelevante trin over automatisk
                while (isStepSkipped(currentStepIndex + 1) && currentStepIndex < totalSteps - 1) {
                    currentStepIndex++
                }
            } else {
                // Automatisk fallback billede hvis ingen generelle
                if (generalUris.isEmpty()) {
                    val allStepUris = stepPhotos.values.flatten()
                    if (allStepUris.isNotEmpty()) {
                        viewModel.updateImages(listOf(allStepUris.first()))
                    }
                }

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
                // Gå tilbage til sidste relevante trin
                while (isStepSkipped(currentStepIndex + 1) && currentStepIndex > 0) {
                    currentStepIndex--
                }
            } else {
                navController.popBackStack()
            }
        },
        onNext = tryNext,
        isNextEnabled = isStepValid || currentStepNumber == totalSteps,
        nextButtonText = if (currentStepNumber == totalSteps) if (isSending) "Sender..." else "Send opgave" else "Næste"
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
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
                10 -> OpmuringArmeringStep(viewModel = viewModel) // Kun vist hvis needsPudsarmering
                11 -> OpmuringInsulationStep(viewModel = viewModel)
                12 -> OpmuringFoundationStep(viewModel = viewModel)
                13 -> OpmuringDamageStep(viewModel = viewModel) // Kun vist hvis needsDamageStep
                14 -> OpmuringAccessStep(viewModel = viewModel)
                15 -> OpmuringPhotosStep(viewModel = viewModel)
                16 -> OpmuringDescriptionStep(viewModel = viewModel)
                17 -> OpmuringSummaryStep(data = data, viewModel = viewModel, isSending = isSending)
            }
        }
    }
}