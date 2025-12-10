// File: app/src/main/java/dk/byggepiloten/firma/ui/screen/OpmuringScreen.kt
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

// Samme genbrugbare RadioButtonGroup – placeret direkte i filen
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
fun OpmuringScreen(navController: NavController) {
    var length by remember { mutableStateOf("") }
    var height by remember { mutableStateOf("") }
    var thickness by remember { mutableStateOf("24 cm") }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Opmuring") },
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
                    value = height,
                    onValueChange = { height = it },
                    label = { Text("Højde (m)") },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(Modifier.height(24.dp))

            Text("Vægtykkelse:", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))

            RadioButtonGroup(
                options = listOf("12 cm", "24 cm", "36 cm"),
                selectedOption = thickness,
                onOptionSelected = { thickness = it }
            )

            Spacer(Modifier.height(32.dp))

            Button(
                onClick = { navController.navigate("task_photos_description") },
                modifier = Modifier.fillMaxWidth(),
                enabled = length.isNotBlank() && height.isNotBlank()
            ) {
                Text("Fortsæt til billeder")
            }
        }
    }
}