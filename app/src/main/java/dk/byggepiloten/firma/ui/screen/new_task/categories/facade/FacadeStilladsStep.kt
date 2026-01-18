package dk.byggepiloten.firma.ui.screen.new_task.categories.facade

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import dk.byggepiloten.firma.ui.screen.new_task.components.PhotoUploadSection
import dk.byggepiloten.firma.ui.screen.new_task.components.common.StyledTextField
import dk.byggepiloten.firma.ui.screen.new_task.components.WizardScaffold
import dk.byggepiloten.firma.ui.viewmodel.task.FacadeTaskViewModel

private val stilladsOptions = listOf("Ja", "Nej")

@Composable
fun FacadeStilladsStep(
    navController: NavController,
    viewModel: FacadeTaskViewModel = hiltViewModel()
) {
    val facadeData by viewModel.facadeData.collectAsState()
    val stepImages by viewModel.stepPhotos.collectAsState()

    var selected by remember { mutableStateOf(facadeData.stilladsNoedvendigt ?: "") }
    var adgang by remember { mutableStateOf(facadeData.stilladsAdgang ?: "") }
    var trapper by remember { mutableStateOf(facadeData.stilladsTrapper ?: "") }

    WizardScaffold(
        title = "Facadepudsning – Stillads",
        progress = 4f / 9f,
        onNavigationBack = { navController.popBackStack() },
        onPrevious = { navController.popBackStack() },
        onNext = {
            val updated = facadeData.copy(
                stilladsNoedvendigt = selected,
                stilladsAdgang = if (selected == "Ja") adgang else null,
                stilladsTrapper = if (selected == "Ja") trapper else null
            )
            viewModel.updateFacadeData(updated)
            navController.navigate("facade_underlag")
        },
        isNextEnabled = selected.isNotBlank()
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
                        "Er stillads nødvendigt?",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.Black
                    )
                    Spacer(Modifier.height(16.dp))

                    stilladsOptions.forEach { option ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selected = option }
                        ) {
                            RadioButton(
                                selected = selected == option,
                                onClick = { selected = option }
                            )
                            Text(option, modifier = Modifier.padding(start = 8.dp), color = Color.Black)
                        }
                    }

                    if (selected == "Ja") {
                        Spacer(Modifier.height(16.dp))
                        StyledTextField(
                            value = adgang,
                            onValueChange = { adgang = it },
                            label = "Beskriv adgang til stillads"
                        )
                        Spacer(Modifier.height(8.dp))
                        StyledTextField(
                            value = trapper,
                            onValueChange = { trapper = it },
                            label = "Er der trapper/adgangsveje?"
                        )
                    }
                }
            }

            PhotoUploadSection(
                label = "Billeder af adgangsforhold/stillads (valgfrit)",
                currentUris = stepImages["stillads"] ?: emptyList(),
                onUrisChange = { viewModel.updateStepPhotos("stillads", it) }
            )
        }
    }
}
