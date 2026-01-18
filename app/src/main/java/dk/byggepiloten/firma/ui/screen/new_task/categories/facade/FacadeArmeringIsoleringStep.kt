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
import dk.byggepiloten.firma.ui.screen.new_task.components.common.StyledTextField
import dk.byggepiloten.firma.ui.screen.new_task.components.WizardScaffold
import dk.byggepiloten.firma.ui.viewmodel.task.FacadeTaskViewModel

private val isoleringTyper = listOf("Mineraluld", "EPS", "Anden")

@Composable
fun FacadeArmeringIsoleringStep(
    navController: NavController,
    viewModel: FacadeTaskViewModel = hiltViewModel()
) {
    val facadeData by viewModel.facadeData.collectAsState()
    val stepImages by viewModel.stepPhotos.collectAsState()

    var armering by remember { mutableStateOf(facadeData.armeringsnet ?: if (facadeData.vaegtype == "Mursten") "Ja" else "") }
    var isolering by remember { mutableStateOf(facadeData.isolering ?: "") }
    var isoleringType by remember { mutableStateOf(facadeData.isoleringType ?: "") }

    WizardScaffold(
        title = "Facadepudsning – Armering & Isolering",
        progress = 7f / 9f,
        onNavigationBack = { navController.popBackStack() },
        onPrevious = { navController.popBackStack() },
        onNext = {
            val updated = facadeData.copy(
                armeringsnet = armering,
                isolering = isolering,
                isoleringType = if (isolering == "Ja") isoleringType else null
            )
            viewModel.updateFacadeData(updated)
            navController.navigate("facade_haeftemoertel")
        },
        isNextEnabled = armering.isNotBlank() && isolering.isNotBlank()
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
                        "Armering og isolering",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.Black
                    )
                    Spacer(Modifier.height(16.dp))

                    ChoiceBoxRow(
                        label = "Skal der armeringsnet?",
                        options = listOf("Ja", "Nej"),
                        selectedOption = armering,
                        onOptionSelected = { armering = it }
                    )
                    
                    if (facadeData.vaegtype == "Mursten") {
                        Text(
                            "Ved mursten anbefales armeringsnet",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Black.copy(alpha = 0.7f),
                            modifier = Modifier.padding(top = 8.dp, bottom = 8.dp)
                        )
                    }

                    Spacer(Modifier.height(16.dp))

                    ChoiceBoxRow(
                        label = "Skal der isoleres?",
                        options = listOf("Ja", "Nej"),
                        selectedOption = isolering,
                        onOptionSelected = { isolering = it }
                    )

                    if (isolering == "Ja") {
                        Spacer(Modifier.height(24.dp))
                        
                        ChoiceBoxRow(
                            label = "Vælg isoleringstype",
                            options = isoleringTyper,
                            selectedOption = isoleringType,
                            onOptionSelected = { isoleringType = it }
                        )

                        if (isoleringType == "Anden") {
                            Spacer(Modifier.height(16.dp))
                            StyledTextField(
                                value = facadeData.isoleringType ?: "",
                                onValueChange = { viewModel.updateFacadeData(facadeData.copy(isoleringType = it)) },
                                label = "Beskriv isoleringstype"
                            )
                        }
                    }
                }
            }

            PhotoUploadSection(
                label = "Billeder af væg/underlag (valgfrit)",
                currentUris = stepImages["isolering"] ?: emptyList(),
                onUrisChange = { viewModel.updateStepPhotos("isolering", it) }
            )
        }
    }
}
