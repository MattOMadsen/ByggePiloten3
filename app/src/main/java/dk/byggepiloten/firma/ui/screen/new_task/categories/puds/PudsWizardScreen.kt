// Fil: app/src/main/java/dk/byggepiloten/firma/ui/screen/new_task/categories/puds/PudsWizardScreen.kt
// RETTET: Fjernet verticalScroll fra hoved-Column for at løse "Infinity maximum height" crash.

package dk.byggepiloten.firma.ui.screen.new_task.categories.puds

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import dk.byggepiloten.firma.ui.screen.new_task.components.WizardScaffold
import dk.byggepiloten.firma.ui.screen.new_task.components.common.WizardStepTitle
import dk.byggepiloten.firma.ui.viewmodel.task.PudsTaskViewModel

@Composable
fun PudsWizardScreen(
    navController: NavController
) {
    val viewModel: PudsTaskViewModel = hiltViewModel()
    val data by viewModel.pudsData.collectAsStateWithLifecycle()
    val stepPhotos by viewModel.stepPhotos.collectAsStateWithLifecycle(emptyMap<String, List<android.net.Uri>>())
    val generalUris by viewModel.imageUris.collectAsStateWithLifecycle()
    val errorMessage by viewModel.error.collectAsStateWithLifecycle()
    val isSending by viewModel.isSending.collectAsStateWithLifecycle()

    var currentStepIndex by remember { mutableIntStateOf(0) }
    val totalSteps = 10

    val progress by remember(currentStepIndex) {
        derivedStateOf { (currentStepIndex + 1f) / totalSteps }
    }

    val currentStepNumber = currentStepIndex + 1

    fun isStepSkipped(step: Int): Boolean =
        data.indeUde == "Inde" && step in listOf(4, 5, 7, 8)

    val isCurrentStepValid by remember(currentStepNumber, data, stepPhotos) {
        derivedStateOf {
            if (isStepSkipped(currentStepNumber)) true
            else PudsValidator.isStepValid(data, stepPhotos, currentStepNumber)
        }
    }

    val missingSteps by remember(data, stepPhotos) {
        derivedStateOf { viewModel.validateBeforeSend() }
    }

    val isSummaryValid by remember(missingSteps) {
        derivedStateOf { missingSteps.isEmpty() }
    }

    var showMissingStepsDialog by remember { mutableStateOf(false) }
    var pendingMissingSteps by remember { mutableStateOf<List<Int>>(emptyList()) }

    val stepTitles = mapOf(
        1 to "Inde eller ude?",
        2 to "Indtast mål for hver væg",
        3 to "Hvilken vægtype?",
        4 to "Bygningshøjde",
        5 to "Stillads og adgang",
        6 to "Underlag",
        7 to "Hvornår på året udsættes væggen mest for vejr?",
        8 to "Armering og isolering",
        9 to "Hæftemørtel",
        10 to "Opsummering og afsend"
    )

    fun advanceToNextVisibleStep(fromIndex: Int): Int {
        var next = fromIndex + 1
        while (next < totalSteps && isStepSkipped(next + 1)) next++
        return next.coerceAtMost(totalSteps - 1)
    }

    fun retreatToPrevVisibleStep(fromIndex: Int): Int {
        var prev = fromIndex - 1
        while (prev > 0 && isStepSkipped(prev + 1)) prev--
        return prev.coerceAtLeast(0)
    }

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
                    navController.navigate("dashboard") {
                        popUpTo(0) { inclusive = true }
                    }
                }
            } else {
                pendingMissingSteps = missingSteps.sorted()
                showMissingStepsDialog = true
            }
        } else {
            if (isCurrentStepValid) {
                currentStepIndex = advanceToNextVisibleStep(currentStepIndex)
                viewModel.clearError()
            } else {
                viewModel.setError("Udfyld venligst alle påkrævede felter før du går videre")
            }
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
                                    currentStepIndex = retreatToPrevVisibleStep(step - 1)
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
                    Text("Luk")
                }
            }
        )
    }

    WizardScaffold(
        title = "Pudsning",
        progress = progress,
        onNavigationBack = { navController.popBackStack() },
        onPrevious = {
            if (currentStepIndex > 0) {
                currentStepIndex = retreatToPrevVisibleStep(currentStepIndex)
                viewModel.clearError()
            } else {
                navController.popBackStack()
            }
        },
        onNext = tryNext,
        isNextEnabled = if (currentStepNumber == totalSteps) true else isCurrentStepValid,
        nextButtonText = if (currentStepNumber == totalSteps) {
            if (isSending) "Sender..." else "Send opgave"
        } else "Næste"
    ) {
        // RETTELSE: Fjernet .verticalScroll() herfra da det crasher med underkomponenter (infinity height)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            WizardStepTitle(text = stepTitles[currentStepNumber] ?: "Trin $currentStepNumber")

            if (errorMessage != null) {
                Text(
                    text = errorMessage!!,
                    color = Color.Red,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }

            when (currentStepNumber) {
                1 -> PudsIndeUdeStep(viewModel = viewModel)
                2 -> PudsAreaStep(viewModel = viewModel)
                3 -> PudsVaegtypeStep(viewModel = viewModel)
                4 -> PudsHoejdeStep(viewModel = viewModel)
                5 -> PudsStilladsStep(viewModel = viewModel)
                6 -> if (data.indeUde == "Inde") PudsUnderlagIndeStep(viewModel = viewModel) else PudsUnderlagStep(viewModel = viewModel)
                7 -> PudsVejrStep(viewModel = viewModel)
                8 -> PudsArmeringsisoleringStep(viewModel = viewModel)
                9 -> PudsHaeftemoertelStep(viewModel = viewModel)
                10 -> PudsSummaryStep(viewModel = viewModel, isSending = isSending)
            }
        }
    }
}
