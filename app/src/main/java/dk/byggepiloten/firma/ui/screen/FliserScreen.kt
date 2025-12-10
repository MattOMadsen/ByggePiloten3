// File: app/src/main/java/dk/byggepiloten/firma/ui/screen/FliserScreen.kt

package dk.byggepiloten.firma.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FliserScreen(navController: NavController) {
    var floorArea by remember { mutableStateOf("") }
    var wallArea by remember { mutableStateOf("") }
    var hasPattern by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Flisearbejde") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Tilbage")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            OutlinedTextField(
                value = floorArea,
                onValueChange = { floorArea = it },
                label = { Text("Gulvareal (m²)") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(16.dp))
            OutlinedTextField(
                value = wallArea,
                onValueChange = { wallArea = it },
                label = { Text("Vægareal (m²) – valgfrit") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(16.dp))
            Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                Checkbox(checked = hasPattern, onCheckedChange = { hasPattern = it })
                Text("Ønskes sildeben, fiskeben eller andet mønster?")
            }
            Spacer(Modifier.height(32.dp))
            Button(
                onClick = { navController.navigate("task_photos_description") },
                modifier = Modifier.fillMaxWidth(),
                enabled = floorArea.isNotBlank()
            ) {
                Text("Fortsæt til billeder")
            }
        }
    }
}