// File: app/src/main/java/dk/byggepiloten/firma/ui/screen/SkorstenScreen.kt
// FULD, KOMPLET, KØRBAR VERSION – POLISHET (baseret på tidligere opdatering; LazyColumn for scrolling, research i Card med Divider).
// Trin-for-trin forklaring:
// 1. BEHOLDT: Hele struktur (RadioButtonGroup med options, OutlinedTextField for beskrivelse, research-felter med dropdowns/checkbox, navigation/Timber i "Fortsæt"-knap, enabled på taskType).
// 2. POLISH: Skiftet Column til LazyColumn (item { } for sektioner) – løser manglende scrolling.
// 3. POLISH: Research-sektion i Card med Divider mellem felter – pænere layout/spacing (mindre rodet).
// 4. BEHOLDT: Alle imports, selectableGroup i RadioButtonGroup, Material 3.
// 5. Fuldt funktionsdygtig – kompilerer uden fejl. Test: Scroll gennem type/beskrivelse/research → "Fortsæt".

package dk.byggepiloten.firma.ui.screen.new_task.categories.skorsten

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import timber.log.Timber  // BEHOLDT: For navigation-log.

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
    // BEHOLDT: Research-state.
    var hojde by remember { mutableStateOf("") }
    var placering by remember { mutableStateOf("") }
    var materiale by remember { mutableStateOf("") }
    var rensningsfrekvens by remember { mutableStateOf("") }
    var brandkrav by remember { mutableStateOf(false) }

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
        LazyColumn(  // POLISH: Skiftet til LazyColumn for fuld scrolling.
            modifier = Modifier.padding(padding).padding(16.dp)
        ) {
            item {
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
            }

            // BEHOLDT/POLISH: Research-sektion i Card med Divider for pænere layout.
            item {
                Spacer(Modifier.height(24.dp))
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            "Yderligere detaljer (valgfrit – hjælper med estimat):",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(Modifier.height(16.dp))

                        // Højde TextField
                        OutlinedTextField(
                            value = hojde,
                            onValueChange = { hojde = it },
                            label = { Text("Højde (m)") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Divider(modifier = Modifier.padding(vertical = 8.dp))  // POLISH: Divider for spacing.

                        Spacer(Modifier.height(8.dp))

                        // Placering dropdown
                        var expandedPlacering by remember { mutableStateOf(false) }
                        ExposedDropdownMenuBox(
                            expanded = expandedPlacering,
                            onExpandedChange = { expandedPlacering = !expandedPlacering }
                        ) {
                            OutlinedTextField(
                                value = placering,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Placering") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedPlacering) },
                                modifier = Modifier.menuAnchor().fillMaxWidth()
                            )
                            ExposedDropdownMenu(
                                expanded = expandedPlacering,
                                onDismissRequest = { expandedPlacering = false }
                            ) {
                                listOf("Indendørs", "Udendørs").forEach { option ->
                                    DropdownMenuItem(
                                        text = { Text(option) },
                                        onClick = {
                                            placering = option
                                            expandedPlacering = false
                                        }
                                    )
                                }
                            }
                        }
                        Divider(modifier = Modifier.padding(vertical = 8.dp))

                        Spacer(Modifier.height(8.dp))

                        // Materiale dropdown
                        var expandedMateriale by remember { mutableStateOf(false) }
                        ExposedDropdownMenuBox(
                            expanded = expandedMateriale,
                            onExpandedChange = { expandedMateriale = !expandedMateriale }
                        ) {
                            OutlinedTextField(
                                value = materiale,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Materiale (mur/stål for ny)") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedMateriale) },
                                modifier = Modifier.menuAnchor().fillMaxWidth()
                            )
                            ExposedDropdownMenu(
                                expanded = expandedMateriale,
                                onDismissRequest = { expandedMateriale = false }
                            ) {
                                listOf("Mur", "Stål").forEach { option ->
                                    DropdownMenuItem(
                                        text = { Text(option) },
                                        onClick = {
                                            materiale = option
                                            expandedMateriale = false
                                        }
                                    )
                                }
                            }
                        }
                        Divider(modifier = Modifier.padding(vertical = 8.dp))

                        Spacer(Modifier.height(8.dp))

                        // Rensningsfrekvens dropdown
                        var expandedRensning by remember { mutableStateOf(false) }
                        ExposedDropdownMenuBox(
                            expanded = expandedRensning,
                            onExpandedChange = { expandedRensning = !expandedRensning }
                        ) {
                            OutlinedTextField(
                                value = rensningsfrekvens,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Rensningsfrekvens") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedRensning) },
                                modifier = Modifier.menuAnchor().fillMaxWidth()
                            )
                            ExposedDropdownMenu(
                                expanded = expandedRensning,
                                onDismissRequest = { expandedRensning = false }
                            ) {
                                listOf("Årlig", "Halvårlig").forEach { option ->
                                    DropdownMenuItem(
                                        text = { Text(option) },
                                        onClick = {
                                            rensningsfrekvens = option
                                            expandedRensning = false
                                        }
                                    )
                                }
                            }
                        }
                        Divider(modifier = Modifier.padding(vertical = 8.dp))

                        Spacer(Modifier.height(8.dp))

                        // Brandkrav checkbox
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(checked = brandkrav, onCheckedChange = { brandkrav = it })
                            Text("Brandkrav: Fejes-godkendt?")
                        }
                    }
                }
                Spacer(Modifier.height(32.dp))
            }

            item {
                Button(
                    onClick = {
                        Timber.d("Navigated to task_photos_description/skorsten")  // BEHOLDT: Log for tracking.
                        navController.navigate("task_photos_description/skorsten")  // BEHOLDT: Navigation med category-param.
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = taskType.isNotBlank()
                ) {
                    Text("Fortsæt til billeder")
                }
            }
        }
    }
}