// File: app/src/main/java/dk/byggepiloten/firma/ui/screen/BadeværelseScreen.kt
package dk.byggepiloten.firma.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BadeværelseScreen(navController: NavController) {
    var area by remember { mutableStateOf("") }
    val tasks = remember { mutableStateListOf<String>() }

    val allTasks = listOf(
        "Vådrumssikring",
        "Fjern gamle fliser på væg",
        "Fjern gamle klinker fra gulv",
        "Pudsning af vægge",
        "Opretning af gulv",
        "Fliser på gulv",
        "Fliser på vægge",
        "Opbygning af skillevæg i letbeton",
        "Reparation af eksisterende murværk",
        "Komplet renovering (inkl. alt ovenstående)"
    )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Badeværelse") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Tilbage")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.padding(padding).padding(16.dp)) {
            item {
                OutlinedTextField(
                    value = area,
                    onValueChange = { area = it },
                    label = { Text("Areal (m²)") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                Spacer(Modifier.height(24.dp))
                Text("Vælg opgaver:", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(16.dp))
            }

            items(allTasks) { task ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = tasks.contains(task),
                        onCheckedChange = { checked ->
                            if (checked) tasks.add(task) else tasks.remove(task)
                        }
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(task)
                }
            }

            item {
                Spacer(Modifier.height(32.dp))
                Button(
                    onClick = { navController.navigate("task_photos_description") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = area.isNotBlank() && tasks.isNotEmpty()
                ) {
                    Text("Fortsæt til billeder")
                }
            }
        }
    }
}