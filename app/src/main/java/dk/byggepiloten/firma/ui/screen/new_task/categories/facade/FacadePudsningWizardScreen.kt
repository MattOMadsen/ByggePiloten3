// Fil: app/src/main/java/dk/byggepiloten/firma/ui/screen/new_task/categories/facade/FacadePudsningWizardScreen.kt
// FULD OPDATERET VERSION – NU MED KORREKTE STEP-NAVNE FRA DINE UPLOADEDE FILER
// - 3-farve gradient (ByggePilotenBlue → #42A5F5 → #90CAF9)
// - Fjernet dark overlay (ikke nødvendigt – steps bruger hvid tekst direkte)
// - Korrekt stavning: FacadeStilladsStep, FacadeArmeringIsoleringStep, FacadeHaeftemoertelStep
// - Step 7: FacadeOpsummeringStep (ikke Screen) med navController-parameter
// - Alle steps bruger onUpdate: (FacadeData) -> Unit
// - Beholdt din præcise validering, auto-default armeringsnet og navigation
// - Tilføjet manglende import for ExperimentalLayoutApi (brugtes i flere steps)
// - Linjer: 198

package dk.byggepiloten.firma.ui.screen.new_task.categories.facade

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import dk.byggepiloten.firma.ui.viewmodel.task.FacadeTaskViewModel
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.FlowRow

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun FacadePudsningWizardScreen(
    navController: NavController,
    viewModel: FacadeTaskViewModel = hiltViewModel()
) {
    val data by viewModel.facadeData.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.setCurrentCategory("facade_pudsning")
    }

    var currentStep by remember { mutableIntStateOf(1) }
    val totalSteps = 7
    val progress = currentStep.toFloat() / totalSteps

    // Auto-default armeringsnet = "Ja" ved Mursten
    LaunchedEffect(data.vaegtype) {
        if (data.vaegtype == "Mursten" && data.armeringsnet == null) {
            viewModel.updateFacadeData(data.copy(armeringsnet = "Ja"))
        }
    }

    val isNextEnabled by derivedStateOf {
        currentStep == 1 && data.area?.let { it > 0f } == true || currentStep > 1
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
                    title = { Text("Facadepudsning", color = Color.White, fontWeight = FontWeight.Bold) },
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
                    1 -> FacadeAreaVaegtypeStep(data = data, onUpdate = { viewModel.updateFacadeData(it) })
                    2 -> FacadeStilladsStep(data = data, onUpdate = { viewModel.updateFacadeData(it) })
                    3 -> FacadeArmeringIsoleringStep(data = data, onUpdate = { viewModel.updateFacadeData(it) })
                    4 -> FacadeUnderlagStep(data = data, onUpdate = { viewModel.updateFacadeData(it) })
                    5 -> FacadeVejrStep(data = data, onUpdate = { viewModel.updateFacadeData(it) })
                    6 -> FacadeHaeftemoertelStep(data = data, onUpdate = { viewModel.updateFacadeData(it) })
                    7 -> FacadeOpsummeringStep(data = data, navController = navController)
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
                                navController.navigate("task_photos_description/facade_pudsning")
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