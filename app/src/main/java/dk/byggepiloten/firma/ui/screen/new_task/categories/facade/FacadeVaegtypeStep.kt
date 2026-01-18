package dk.byggepiloten.firma.ui.screen.new_task.categories.facade

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import dk.byggepiloten.firma.ui.screen.new_task.components.common.StyledTextField
import dk.byggepiloten.firma.ui.screen.new_task.components.WizardScaffold
import dk.byggepiloten.firma.ui.viewmodel.task.FacadeTaskViewModel

private val vaegtyper = listOf("Mursten", "Gasbeton", "Letbeton", "Anden")

@Composable
fun FacadeVaegtypeStep(
    navController: NavController,
    viewModel: FacadeTaskViewModel = hiltViewModel()
) {
    val facadeData by viewModel.facadeData.collectAsState()

    var selectedType by remember { mutableStateOf(facadeData.vaegtype ?: "") }
    var customText by remember { mutableStateOf(facadeData.andenVaegtype ?: "") }

    WizardScaffold(
        title = "Facadepudsning – Vægtype",
        progress = 2f / 9f,
        onNavigationBack = { navController.popBackStack() },
        onPrevious = { navController.popBackStack() },
        onNext = {
            val updated = facadeData.copy(
                vaegtype = selectedType,
                andenVaegtype = if (selectedType == "Anden") customText else null
            )
            viewModel.updateFacadeData(updated)
            navController.navigate("facade_hoejde")
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
                        "Hvilken type væg skal pudses?",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.Black
                    )
                    Spacer(Modifier.height(16.dp))

                    Column(Modifier.selectableGroup()) {
                        vaegtyper.forEach { type ->
                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .height(56.dp)
                                    .selectable(
                                        selected = (selectedType == type),
                                        onClick = { selectedType = type },
                                        role = Role.RadioButton
                                    )
                                    .padding(horizontal = 16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = (selectedType == type),
                                    onClick = null
                                )
                                Text(
                                    text = type,
                                    style = MaterialTheme.typography.bodyLarge,
                                    modifier = Modifier.padding(start = 16.dp),
                                    color = Color.Black
                                )
                            }
                        }
                    }

                    if (selectedType == "Anden") {
                        Spacer(Modifier.height(16.dp))
                        StyledTextField(
                            value = customText,
                            onValueChange = { customText = it },
                            label = "Beskriv vægtypen"
                        )
                    }
                }
            }
        }
    }
}
