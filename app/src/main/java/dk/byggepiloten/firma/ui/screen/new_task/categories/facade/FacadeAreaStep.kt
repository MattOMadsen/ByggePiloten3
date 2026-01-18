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
import dk.byggepiloten.firma.ui.screen.new_task.components.PhotoUploadSection
import dk.byggepiloten.firma.ui.screen.new_task.components.WizardScaffold
import dk.byggepiloten.firma.ui.viewmodel.task.FacadeTaskViewModel

@Composable
fun FacadeAreaStep(
    navController: NavController,
    viewModel: FacadeTaskViewModel = hiltViewModel()
) {
    val facadeData by viewModel.facadeData.collectAsState()
    val generalImages by viewModel.imageUris.collectAsState()

    var areaText by remember { mutableStateOf(facadeData.area?.toString() ?: "") }

    WizardScaffold(
        title = "Facadepudsning – Areal",
        progress = 1f / 9f,
        onNavigationBack = { navController.popBackStack() },
        onPrevious = { navController.popBackStack() },
        onNext = {
            val area = areaText.toFloatOrNull()
            if (area != null && area > 0f) {
                viewModel.updateFacadeData(facadeData.copy(area = area))
                navController.navigate("facade_vaegtype")
            }
        },
        isNextEnabled = areaText.toFloatOrNull()?.let { it > 0 } ?: false
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
                        "Hvad er det samlede areal der skal pudses?",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.Black
                    )
                    Spacer(Modifier.height(12.dp))
                    StyledTextField(
                        value = areaText,
                        onValueChange = { areaText = it },
                        label = "Areal i m²"
                    )
                }
            }

            PhotoUploadSection(
                label = "Billeder af facaden",
                currentUris = generalImages,
                onUrisChange = { viewModel.updateImages(it) }
            )
        }
    }
}
