// Fil: app/src/main/java/dk/byggepiloten/firma/ui/screen/new_task/categories/opmuring/OpmuringWizardScreen.kt
// FIX: "Send opgave" altid trykbar på summary
// - Ved tryk: Dialog med clickable trin tilbage hvis mangler

package dk.byggepiloten.firma.ui.screen.new_task.categories.opmuring

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
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

    val totalSteps = 17

    val progress by remember {
        derivedStateOf { (currentStepIndex + 1f) / totalSteps }
    }

    val currentStepNumber = currentStepIndex + 1

    val needsPudsarmering by remember {
        derivedStateOf {
            val surface = data.surfaceFinish.orEmpty().lowercase()
            surface.contains("puds") || surface.contains("malet") || surface.contains("filt") ||
                    surface.contains("skalcem") || surface.contains("dura") || surface.contains("vandskuring")
        }
    }

    val needsDamageStep by remember {
        derivedStateOf { data.isRepair == true }
    }

    fun isStepSkipped(step: Int): Boolean {
        return when (step) {
            10 -> !needsPudsarmering
            13 -> !needsDamageStep
            else -> false
        }
    }

    val isCurrentStepValid by remember {
        derivedStateOf {
            if (isStepSkipped(currentStepNumber)) true
            else OpmuringValidator.isStepValid(data, stepPhotos, currentStepNumber)
        }
    }

    val missingSteps by remember {
        derivedStateOf { viewModel.validateBeforeSend() }
    }

    val isSummaryValid by remember {
        derivedStateOf { missingSteps.isEmpty() }
    }

    var pendingMissingSteps by remember { mutableStateOf<List<Int>>(emptyList()) }
    var showMissingStepsDialog by remember { mutableStateOf(false) }

    val stepTitles = mapOf(
        1 to "Murtype",
        2 to "Ny opmuring eller reparation",
        3 to "Bærende væg",
        4 to "Dimensioner",
        5 to "Tykkelse",
        6 to "Sten type",
        7 to "Mørtel",
        8 to "Åbninger",
        9 to "Overfladebehandling",
        10 to "Armering",
        11 to "Isolering",
        12 to "Fundament",
        13 to "Skader",
        14 to "Adgangsforhold",
        15 to "Billeder",
        16 to "Beskrivelse"
    )

    val tryNext = {
        if (currentStepNumber == totalSteps) {
            if (isSummaryValid) {
                if (generalUris.isEmpty()) {
                    val allStepUris = stepPhotos.values.flatten()
                    if (allStepUris.isNotEmpty()) {
                        viewModel.updateImages(listOf(allStepUris.first()))
                    }
                }

                viewModel.sendTask {
                    pendingMissingSteps = emptyList()
                    navController.navigate("dashboard") { popUpTo(0) }
                }
            } else {
                pendingMissingSteps = missingSteps.sorted()
                showMissingStepsDialog = true
            }
        } else {
            if (isCurrentStepValid) {
                if (currentStepIndex < totalSteps - 1) {
                    currentStepIndex++
                    viewModel.clearError()

                    while (isStepSkipped(currentStepIndex + 1) && currentStepIndex < totalSteps - 1) {
                        currentStepIndex++
                    }

                    if (pendingMissingSteps.isNotEmpty()) {
                        val updatedMissing = viewModel.validateBeforeSend()
                            .filter { it > currentStepNumber }
                            .sorted()

                        pendingMissingSteps = updatedMissing

                        if (updatedMissing.isNotEmpty()) {
                            currentStepIndex = updatedMissing.first() - 1
                            while (isStepSkipped(currentStepIndex + 1) && currentStepIndex < totalSteps - 1) {
                                currentStepIndex++
                            }
                        } else {
                            currentStepIndex = totalSteps - 1
                            pendingMissingSteps = emptyList()
                        }
                    }
                }
            } else {
                viewModel.setError("Udfyld venligst alle påkrævede felter før du går videre")
            }
        }
    }

    LaunchedEffect(currentStepNumber) {
        if (currentStepNumber == totalSteps) {
            viewModel.calculateAndGenerateEstimate()
        }
    }

    if (showMissingStepsDialog) {
        AlertDialog(
            onDismissRequest = { showMissingStepsDialog = false },
            title = { Text("Du mangler at udfylde følgende trin:") },
            text = {
                LazyColumn {
                    items(pendingMissingSteps) { step ->
                        Text(
                            text = "Trin $step: ${stepTitles[step] ?: "Ukendt trin"}",
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    currentStepIndex = step - 1
                                    while (isStepSkipped(currentStepIndex + 1) && currentStepIndex < totalSteps - 1) {
                                        currentStepIndex++
                                    }
                                    showMissingStepsDialog = false
                                }
                                .padding(vertical = 8.dp),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showMissingStepsDialog = false }) {
                    Text("Annuller")
                }
            }
        )
    }

    WizardScaffold(
        title = "Opmuring",
        progress = progress,
        onNavigationBack = { navController.popBackStack() },
        onPrevious = {
            pendingMissingSteps = emptyList()
            if (currentStepIndex > 0) {
                currentStepIndex--
                viewModel.clearError()
                while (isStepSkipped(currentStepIndex + 1) && currentStepIndex > 0) {
                    currentStepIndex--
                }
            } else {
                navController.popBackStack()
            }
        },
        onNext = tryNext,
        isNextEnabled = true, // Altid enabled på summary
        nextButtonText = if (currentStepNumber == totalSteps) {
            if (isSending) "Sender..." else "Send opgave"
        } else "Næste"
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
                10 -> OpmuringArmeringStep(viewModel = viewModel)
                11 -> OpmuringInsulationStep(viewModel = viewModel)
                12 -> OpmuringFoundationStep(viewModel = viewModel)
                13 -> OpmuringDamageStep(viewModel = viewModel)
                14 -> OpmuringAccessStep(viewModel = viewModel)
                15 -> OpmuringPhotosStep(viewModel = viewModel)
                16 -> OpmuringDescriptionStep(viewModel = viewModel)
                17 -> OpmuringSummaryStep(viewModel = viewModel, isSending = isSending)
            }
        }
    }
}