// Fil: app/src/main/java/dk/byggepiloten/firma/ui/screen/new_task/categories/badevaerelse/BadevaerelseWizardScreen.kt

package dk.byggepiloten.firma.ui.screen.new_task.categories.badevaerelse

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
import dk.byggepiloten.firma.ui.viewmodel.task.TaskViewModel
import dk.byggepiloten.firma.data.model.task.BadevaerelseData

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BadevaerelseWizardScreen(
    navController: NavController,
    viewModel: TaskViewModel = hiltViewModel()
) {
    val data by viewModel.badevaerelseData.collectAsStateWithLifecycle()

    var currentStep by remember { mutableIntStateOf(1) }
    val totalSteps = if (data.renovationType == "Fuldt nyt (med nedrivning)") 12 else 11
    val progress = currentStep.toFloat() / totalSteps

    val isStepValid = when (currentStep) {
        1 -> data.renovationType != null
        2 -> data.floorLength != null && data.floorLength!! > 0f && data.floorWidth != null && data.floorWidth!! > 0f
        3 -> data.wallHeight != null && data.wallHeight!! > 0f
        4 -> data.hasShowerNiche != null && (data.hasShowerNiche == false || (data.showerLength != null && data.showerLength!! > 0f && data.showerWidth != null && data.showerWidth!! > 0f && data.drainType != null))
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
                    title = { Text("Badeværelse", color = Color.White, fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.Filled.ArrowBack, contentDescription = "Tilbage", tint = Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent)
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
                    1 -> BadevaerelseRenoveringstypeStep(data = data, onDataChange = { viewModel.updateBadevaerelseData(it) })
                    2 -> BadevaerelseGulvDimensionsStep(data = data, onDataChange = { viewModel.updateBadevaerelseData(it) })
                    3 -> BadevaerelseVaeggeStep(data = data, onDataChange = { viewModel.updateBadevaerelseData(it) })
                    4 -> BadevaerelseBrusenicheStep(data = data, onDataChange = { viewModel.updateBadevaerelseData(it) })
                    5 -> if (data.renovationType == "Fuldt nyt (med nedrivning)") {
                        BadevaerelseNedrivningStep(data = data, onDataChange = { viewModel.updateBadevaerelseData(it) })
                    } else {
                        // Hvis vi er i step 5 men typen er delvis, bør vi teknisk set have skippet det
                        Text("Indlæser...", color = Color.White)
                    }
                    6 -> BadevaerelseFliserGulvStep(data = data, onDataChange = { viewModel.updateBadevaerelseData(it) })
                    7 -> BadevaerelseFliserVaeggeStep(data = data, onDataChange = { viewModel.updateBadevaerelseData(it) })
                    8 -> BadevaerelseGulvvarmeStep(data = data, onDataChange = { viewModel.updateBadevaerelseData(it) })
                    9 -> BadevaerelseVådrumssikringStep(data = data, onDataChange = { viewModel.updateBadevaerelseData(it) })
                    10 -> BadevaerelseÅbningerStep(data = data, onDataChange = { viewModel.updateBadevaerelseData(it) })
                    11 -> BadevaerelseRørElStep(data = data, onDataChange = { viewModel.updateBadevaerelseData(it) })
                    12 -> BadevaerelseAdgangStep(data = data, onDataChange = { viewModel.updateBadevaerelseData(it) })
                }

                Spacer(Modifier.height(40.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    OutlinedButton(
                        onClick = { 
                            if (currentStep == 6 && data.renovationType != "Fuldt nyt (med nedrivning)") {
                                currentStep = 4
                            } else if (currentStep > 1) {
                                currentStep--
                            } else {
                                navController.popBackStack()
                            }
                        },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                    ) {
                        Text("Tilbage")
                    }

                    Button(
                        onClick = {
                            if (currentStep == totalSteps) {
                                navController.navigate("task_photos_description/badeværelse")
                            } else {
                                if (currentStep == 4 && data.renovationType != "Fuldt nyt (med nedrivning)") {
                                    currentStep = 6
                                } else {
                                    currentStep++
                                }
                            }
                        },
                        enabled = isStepValid,
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = ByggePilotenBlue)
                    ) {
                        Text(if (currentStep == totalSteps) "Fortsæt" else "Næste")
                    }
                }

                Spacer(Modifier.height(16.dp))
            }
        }
    }
}
