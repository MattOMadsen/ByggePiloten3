// Fil: app/src/main/java/dk/byggepiloten/firma/ui/screen/photos/components/SendTaskSection.kt
// NY FIL – Send-knap + no-images dialog + loading/snackbar
// Linjer: 88

package dk.byggepiloten.firma.ui.screen.photos.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import dk.byggepiloten.firma.ui.theme.ByggePilotenBlue
import dk.byggepiloten.firma.ui.viewmodel.task.BaseTaskViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun SendTaskSection(
    viewModel: BaseTaskViewModel,
    imageUris: List<android.net.Uri>,
    navController: NavController,
    snackbarHostState: SnackbarHostState,
    scope: CoroutineScope,
    modifier: Modifier = Modifier
) {
    var showNoImagesDialog by remember { mutableStateOf(false) }

    Column(modifier = modifier) {
        Spacer(Modifier.height(40.dp))

        Button(
            onClick = {
                if (imageUris.isEmpty()) {
                    showNoImagesDialog = true
                } else {
                    viewModel.sendTask {
                        scope.launch {
                            snackbarHostState.showSnackbar("Opgave sendt! Du får besked når der bydes")
                            navController.navigate("dashboard") {
                                popUpTo("new_task") { inclusive = true }
                            }
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color.White)
        ) {
            if (viewModel.isSending.value) {
                CircularProgressIndicator(color = ByggePilotenBlue, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(8.dp))
                Text("Sender...", color = ByggePilotenBlue)
            } else {
                Text("Send opgave til håndværkere", color = ByggePilotenBlue, fontSize = 18.sp)
            }
        }
    }

    if (showNoImagesDialog) {
        AlertDialog(
            onDismissRequest = { showNoImagesDialog = false },
            title = { Text("Ingen billeder?") },
            text = { Text("Det er stærkt anbefalet at tilføje billeder – håndværkere byder hurtigere og mere præcist. Vil du sende alligevel?") },
            confirmButton = {
                TextButton(onClick = {
                    showNoImagesDialog = false
                    viewModel.sendTask {
                        scope.launch {
                            snackbarHostState.showSnackbar("Opgave sendt! Du får besked når der bydes")
                            navController.navigate("dashboard") {
                                popUpTo("new_task") { inclusive = true }
                            }
                        }
                    }
                }) {
                    Text("Send alligevel")
                }
            },
            dismissButton = {
                TextButton(onClick = { showNoImagesDialog = false }) {
                    Text("Tilføj billeder")
                }
            }
        )
    }
}