// File: app/src/main/java/dk/byggepiloten/firma/ui/screen/FundamentScreen.kt

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
fun FundamentScreen(navController: NavController) {
    var length by remember { mutableStateOf("") }
    var width by remember { mutableStateOf("") }
    var depth by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Fundament") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Tilbage")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            Row {
                OutlinedTextField(
                    value = length,
                    onValueChange = { length = it },
                    label = { Text("Længde (m)") },
                    modifier = Modifier.weight(1f)
                )
                Spacer(Modifier.width(8.dp))
                OutlinedTextField(
                    value = width,
                    onValueChange = { width = it },
                    label = { Text("Bredde (m)") },
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(Modifier.height(16.dp))
            OutlinedTextField(
                value = depth,
                onValueChange = { depth = it },
                label = { Text("Dybde (m) – valgfrit") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(32.dp))
            Button(
                onClick = { navController.navigate("task_photos_description") },
                modifier = Modifier.fillMaxWidth(),
                enabled = length.isNotBlank() && width.isNotBlank()
            ) {
                Text("Fortsæt til billeder")
            }
        }
    }
}