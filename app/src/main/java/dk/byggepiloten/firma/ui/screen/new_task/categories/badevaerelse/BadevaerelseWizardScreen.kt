// Fil: app/src/main/java/dk/byggepiloten/firma/ui/screen/new_task/categories/badevaerelse/BadevaerelseWizardScreen.kt
// RETTET: floorNumber-reference i validering nu gyldig.
// - Validering step 12: goodAccess != null && (goodAccess == true || floorNumber != null)

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
import dk.byggepiloten.firma.data.model.BadevaerelseData
import dk.byggepiloten.firma.ui.theme.ByggePilotenBlue
import dk.byggepiloten.firma.ui.viewmodel.TaskViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BadevaerelseWizardScreen(
    onBack: () -> Unit,
    onComplete: () -> Unit
) {
    val viewModel: TaskViewModel = hiltViewModel()
    val data by viewModel.badevaerelseData.collectAsState()

    var currentStep by remember { mutableStateOf(1) }
    val totalSteps = if (data.renovationType == "Fuldt nyt (med nedrivning)") 12 else 11
    val progress = currentStep.toFloat() / totalSteps

    val isStepValid = when (currentStep) {
        1 -> data.renovationType != null
        2 -> data.floorLength != null && data.floorLength!! > 0f && data.floorWidth != null && data.floorWidth!! > 0f
        3 -> data.wallHeight != null && data.wallHeight!! > 0f
        4 -> data.hasShowerNiche != null && (data.hasShowerNiche == false || (data.showerLength != null && data.showerLength!! > 0f && data.showerWidth != null && data.showerWidth!! > 0f && data.drainType != null))
        5 -> true
        6 -> data.floorTileSize != null
        7 -> data.wallTileSize != null && data.tilesToCeiling != null && (data.tilesToCeiling == true || data.wallTileHeightIfNotCeiling != null)
        8 -> data.hasFloorHeating != null && (data.hasFloorHeating == false || data.floorHeatingType != null)
        9 -> data.hasMembrane != null && data.hasVentilation != null
        10 -> true
        11 -> (data.relocatePipes != true || data.pipeDescription?.isNotBlank() == true) && (data.relocateElectrical != true || data.electricalDescription?.isNotBlank() == true)
        12 -> data.goodAccess != null && (data.goodAccess == true || data.floorNumber != null) // RETTET: floorNumber nu gyldig
        else -> true
    }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Badeværelse renovering", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Tilbage", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = ByggePilotenBlue)
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(ByggePilotenBlue, ByggePilotenBlue.copy(alpha = 0.8f))
                    )
                )
                .padding(padding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
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
                    5 -> BadevaerelseNedrivningStep(data = data, onDataChange = { viewModel.updateBadevaerelseData(it) })
                    6 -> BadevaerelseFliserGulvStep(data = data, onDataChange = { viewModel.updateBadevaerelseData(it) })
                    7 -> BadevaerelseFliserVaeggeStep(data = data, onDataChange = { viewModel.updateBadevaerelseData(it) })
                    8 -> BadevaerelseGulvvarmeStep(data = data, onDataChange = { viewModel.updateBadevaerelseData(it) })
                    9 -> BadevaerelseVådrumssikringStep(data = data, onDataChange = { viewModel.updateBadevaerelseData(it) })
                    10 -> BadevaerelseÅbningerStep(data = data, onDataChange = { viewModel.updateBadevaerelseData(it) })
                    11 -> BadevaerelseRørElStep(data = data, onDataChange = { viewModel.updateBadevaerelseData(it) })
                    12 -> BadevaerelseAdgangStep(data = data, onDataChange = { viewModel.updateBadevaerelseData(it) })
                }

                Spacer(Modifier.height(32.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    OutlinedButton(onClick = { if (currentStep > 1) currentStep-- else onBack() }, colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)) {
                        Text("Tilbage")
                    }
                    Button(
                        onClick = {
                            if (currentStep == totalSteps) onComplete() else {
                                currentStep++
                                if (currentStep == 5 && data.renovationType != "Fuldt nyt (med nedrivning)") currentStep = 6
                            }
                        },
                        enabled = isStepValid,
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = ByggePilotenBlue)
                    ) {
                        Text(if (currentStep == totalSteps) "Fortsæt til billeder" else "Næste")
                    }
                }
            }
        }
    }
}