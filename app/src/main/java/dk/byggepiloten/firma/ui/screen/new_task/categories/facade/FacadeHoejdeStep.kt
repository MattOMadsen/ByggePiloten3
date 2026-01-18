package dk.byggepiloten.firma.ui.screen.new_task.categories.facade

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import dk.byggepiloten.firma.ui.screen.new_task.components.common.StyledTextField
import dk.byggepiloten.firma.ui.screen.new_task.components.WizardScaffold
import dk.byggepiloten.firma.ui.viewmodel.task.FacadeTaskViewModel

@Composable
fun FacadeHoejdeStep(
    navController: NavController,
    viewModel: FacadeTaskViewModel = hiltViewModel()
) {
    val facadeData by viewModel.facadeData.collectAsState()

    var hoejdeText by remember { mutableStateOf(facadeData.hojde?.toString() ?: "") }

    WizardScaffold(
        title = "Facadepudsning – Bygningshøjde",
        progress = 3f / 9f,
        onNavigationBack = { navController.popBackStack() },
        onPrevious = { navController.popBackStack() },
        onNext = {
            val hoejde = hoejdeText.toFloatOrNull()
            if (hoejde != null && hoejde > 0f) {
                viewModel.updateFacadeData(facadeData.copy(hojde = hoejde))
                navController.navigate("facade_stillads")
            }
        },
        isNextEnabled = hoejdeText.toFloatOrNull()?.let { it > 0 } ?: false
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
                        "Hvad er bygningens højde?",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.Black
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Dette bruges til at vurdere om stillads er nødvendigt.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Black.copy(alpha = 0.7f)
                    )
                    Spacer(Modifier.height(16.dp))
                    StyledTextField(
                        value = hoejdeText,
                        onValueChange = { hoejdeText = it },
                        label = "Højde i meter"
                    )
                }
            }
        }
    }
}
