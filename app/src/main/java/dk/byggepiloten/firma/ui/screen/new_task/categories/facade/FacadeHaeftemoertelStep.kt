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

private val haeftemoertelTyper = listOf("DuraPuds 615", "Skalcem S2000", "Anden")

@Composable
fun FacadeHaeftemoertelStep(
    navController: NavController,
    viewModel: FacadeTaskViewModel = hiltViewModel()
) {
    val facadeData by viewModel.facadeData.collectAsState()
    val stepImages by viewModel.stepPhotos.collectAsState()

    var selectedType by remember { mutableStateOf(facadeData.haeftemoertelType ?: "") }
    var customType by remember { mutableStateOf(facadeData.andenHaeftemoertel ?: "") }
    var durapudsFarve by remember { mutableStateOf(facadeData.durapudsFarve ?: "") }
    var skalcemFarve by remember { mutableStateOf(facadeData.skalcemFarve ?: "") }

    WizardScaffold(
        title = "Facadepudsning – Hæftemørtel",
        progress = 8f / 9f,
        onNavigationBack = { navController.popBackStack() },
        onPrevious = { navController.popBackStack() },
        onNext = {
            val updated = facadeData.copy(
                haeftemoertelType = selectedType,
                andenHaeftemoertel = if (selectedType == "Anden") customType else null,
                durapudsFarve = if (selectedType == "DuraPuds 615") durapudsFarve else null,
                skalcemFarve = if (selectedType == "Skalcem S2000") skalcemFarve else null
            )
            viewModel.updateFacadeData(updated)
            navController.navigate("facade_opsummering")
        },
        isNextEnabled = selectedType.isNotBlank()
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
                        "Hvilken hæftemørtel skal bruges?",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.Black
                    )
                    Spacer(Modifier.height(16.dp))

                    haeftemoertelTyper.forEach { type ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedType = type }
                        ) {
                            RadioButton(
                                selected = selectedType == type,
                                onClick = { selectedType = type }
                            )
                            Text(type, modifier = Modifier.padding(start = 8.dp), color = Color.Black)
                        }
                    }

                    if (selectedType == "Anden") {
                        Spacer(Modifier.height(8.dp))
                        StyledTextField(
                            value = customType,
                            onValueChange = { customType = it },
                            label = "Beskriv hæftemørtel"
                        )
                    }

                    if (selectedType == "DuraPuds 615") {
                        Spacer(Modifier.height(8.dp))
                        StyledTextField(
                            value = durapudsFarve,
                            onValueChange = { durapudsFarve = it },
                            label = "Ønsket farve (DuraPuds)"
                        )
                    }

                    if (selectedType == "Skalcem S2000") {
                        Spacer(Modifier.height(8.dp))
                        StyledTextField(
                            value = skalcemFarve,
                            onValueChange = { skalcemFarve = it },
                            label = "Ønsket farve (Skalcem)"
                        )
                    }
                }
            }

            PhotoUploadSection(
                label = "Billeder af eksisterende puds (valgfrit)",
                currentUris = stepImages["haeftemoertel"] ?: emptyList(),
                onUrisChange = { viewModel.updateStepPhotos("haeftemoertel", it) }
            )
        }
    }
}
