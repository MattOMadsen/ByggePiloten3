package dk.byggepiloten.firma.ui.screen.new_task.categories.facade

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import dk.byggepiloten.firma.ui.screen.new_task.components.PhotoUploadSection
import dk.byggepiloten.firma.ui.screen.new_task.components.common.ChoiceBoxRow
import dk.byggepiloten.firma.ui.screen.new_task.components.WizardScaffold
import dk.byggepiloten.firma.ui.viewmodel.task.FacadeTaskViewModel

@Composable
fun FacadeUnderlagStep(
    navController: NavController,
    viewModel: FacadeTaskViewModel = hiltViewModel()
) {
    val facadeData by viewModel.facadeData.collectAsState()
    val stepImages by viewModel.stepPhotos.collectAsState()

    var revner by remember { mutableStateOf(facadeData.underlagRevner ?: "") }
    var fugt by remember { mutableStateOf(facadeData.underlagFugt ?: "") }
    var gammelPuds by remember { mutableStateOf(facadeData.underlagGammelPuds ?: "") }

    WizardScaffold(
        title = "Facadepudsning – Underlag",
        progress = 5f / 9f,
        onNavigationBack = { navController.popBackStack() },
        onPrevious = { navController.popBackStack() },
        onNext = {
            val updated = facadeData.copy(
                underlagRevner = revner,
                underlagFugt = fugt,
                underlagGammelPuds = gammelPuds
            )
            viewModel.updateFacadeData(updated)
            navController.navigate("facade_vejret")
        },
        isNextEnabled = revner.isNotBlank() && fugt.isNotBlank() && gammelPuds.isNotBlank()
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.95f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        "Hvordan er underlaget?",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.Black
                    )
                    Spacer(Modifier.height(16.dp))

                    ChoiceBoxRow(
                        label = "Er der revner?",
                        options = listOf("Ja", "Nej"),
                        selectedOption = revner,
                        onOptionSelected = { revner = it }
                    )
                    Spacer(Modifier.height(12.dp))
                    ChoiceBoxRow(
                        label = "Er der fugtskader?",
                        options = listOf("Ja", "Nej"),
                        selectedOption = fugt,
                        onOptionSelected = { fugt = it }
                    )
                    Spacer(Modifier.height(12.dp))
                    ChoiceBoxRow(
                        label = "Er der gammel puds?",
                        options = listOf("Ja", "Nej"),
                        selectedOption = gammelPuds,
                        onOptionSelected = { gammelPuds = it }
                    )
                }
            }

            PhotoUploadSection(
                label = "Billeder af underlag (anbefalet)",
                currentUris = stepImages["underlag"] ?: emptyList(),
                onUrisChange = { viewModel.updateStepPhotos("underlag", it) }
            )
        }
    }
}
