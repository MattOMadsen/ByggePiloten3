// Fil: app/src/main/java/dk/byggepiloten/firma/ui/screen/new_task/categories/opmuring/OpmuringWizardScreen.kt
// OPDATERET – Validering flyttet til separat OpmuringValidator.kt
// Step 4 FIX: wallMode != null + reel check på areal/målinger
// isNextEnabled opdateres nu live (tracker data + stepPhotos korrekt)
// Linjer: 398 (ca. -20 linjer pga. flytning af validering)

package dk.byggepiloten.firma.ui.screen.new_task.categories.opmuring

import androidx.compose.runtime.*
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

    val damagePhotos = stepPhotos["damage"] ?: emptyList()
    val accessPhotos = stepPhotos["access"] ?: emptyList()
    val openingsPhotos = stepPhotos["openings"] ?: emptyList()
    val foundationPhotos = stepPhotos["foundation"] ?: emptyList()

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
            add(1); add(2); add(3); add(4)
            if (isNewMur) {
                add(5); add(6); add(7); add(8)
                add(9)
                if (needsArmering) add(10)
                if (isFacadeMur) add(11)
                add(12)
            } else if (data.isRepair == true) {
                add(13)
            }
            add(14)
        }
    }

    var currentStepIndex by remember { mutableIntStateOf(0) }

    val totalSteps = stepList.size
    val progress = if (totalSteps > 0) (currentStepIndex + 1f) / totalSteps else 0f
    val currentStepNumber = if (stepList.isNotEmpty() && currentStepIndex < stepList.size) stepList[currentStepIndex] else 1

    // Validering via separat object → renere kode + nemmere vedligeholdelse
    val isNextEnabled by derivedStateOf {
        OpmuringValidator.isStepValid(data, stepPhotos, currentStepNumber)
    }

    WizardScaffold(
        title = "Opmuring",
        progress = progress,
        onNavigationBack = { navController.popBackStack() },
        onPrevious = { if (currentStepIndex > 0) currentStepIndex-- else navController.popBackStack() },
        onNext = {
            if (currentStepIndex >= totalSteps - 1) {
                navController.navigate("task_photos_description/opmuring")
            } else {
                currentStepIndex++
            }
        },
        isNextEnabled = isNextEnabled,
        nextButtonText = if (currentStepIndex >= totalSteps - 1) "Fortsæt til billeder" else "Næste"
    ) {
        when (currentStepNumber) {
            1 -> OpmuringMurTypeStep(data = data, onDataChange = { viewModel.updateWallData(it) })
            2 -> OpmuringNewOrRepairStep(data = data, onDataChange = { viewModel.updateWallData(it) })
            3 -> OpmuringBearingWallStep(data = data, onDataChange = { viewModel.updateWallData(it) })
            4 -> OpmuringDimensionsStep(data = data, onDataChange = { viewModel.updateWallData(it) })
            5 -> OpmuringThicknessStep(data = data, onDataChange = { viewModel.updateWallData(it) })
            6 -> OpmuringStoneStep(data = data, onDataChange = { viewModel.updateWallData(it) })
            7 -> OpmuringMortarStep(data = data, onDataChange = { viewModel.updateWallData(it) })
            8 -> OpmuringOpeningsStep(
                data = data,
                onDataChange = { viewModel.updateWallData(it) },
                openingsPhotos = openingsPhotos,
                onOpeningsPhotosChange = { viewModel.updateStepPhotos("openings", it) }
            )
            9 -> OpmuringSurfaceStep(data = data, onDataChange = { viewModel.updateWallData(it) })
            10 -> OpmuringArmeringStep(data = data, onDataChange = { viewModel.updateWallData(it) })
            11 -> OpmuringInsulationStep(data = data, onDataChange = { viewModel.updateWallData(it) })
            12 -> OpmuringFoundationStep(
                data = data,
                onDataChange = { viewModel.updateWallData(it) },
                foundationPhotos = foundationPhotos,
                onFoundationPhotosChange = { viewModel.updateStepPhotos("foundation", it) }
            )
            13 -> OpmuringDamageStep(
                data = data,
                onDataChange = { viewModel.updateWallData(it) },
                damagePhotos = damagePhotos,
                onDamagePhotosChange = { viewModel.updateStepPhotos("damage", it) }
            )
            else -> OpmuringAccessStep(
                data = data,
                onDataChange = { viewModel.updateWallData(it) },
                accessPhotos = accessPhotos,
                onAccessPhotosChange = { viewModel.updateStepPhotos("access", it) }
            )
        }
    }
}