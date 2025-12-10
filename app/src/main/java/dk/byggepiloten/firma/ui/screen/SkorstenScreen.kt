// File: app/src/main/java/dk/byggepiloten/firma/ui/screen/SkorstenScreen.kt
package dk.byggepiloten.firma.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

// Genbrugbar RadioButtonGroup – placeret direkte i filen
@Composable
private fun RadioButtonGroup(
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    Column(Modifier.selectableGroup()) {
        options.forEach { text ->
            Row(
                Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = (text == selectedOption),
                    onClick = { onOptionSelected(text) }
                )
                Spacer(Modifier.width(16.dp))
                Text(text = text, style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SkorstenScreen(navController: NavController) {
    var description by remember { mutableStateOf("") }
    var taskType by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Skorstensarbejde") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Tilbage")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            Text("Vælg type arbejde:", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))

            RadioButtonGroup(
                options = listOf(
                    "Reparation af eksisterende skorsten",
                    "Ny skorsten",
                    "Fjerne skorsten",
                    "Rensning og kontrol"
                ),
                selectedOption = taskType,
                onOptionSelected = { taskType = it }
            )

            Spacer(Modifier.height(24.dp))

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Beskriv opgaven (valgfrit)") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            Spacer(Modifier.height(32.dp))

            Button(
                onClick = { navController.navigate("task_photos_description") },
                modifier = Modifier.fillMaxWidth(),
                enabled = taskType.isNotBlank()
            ) {
                Text("Fortsæt til billeder")
            }
        }
    }
}