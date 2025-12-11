// File: app/src/main/java/dk/byggepiloten/firma/ui/screen/FundamentScreen.kt
// FULD, KOMPLET, KØRBAR VERSION – POLISHET (baseret på tidligere opdatering; LazyColumn for scrolling, research i Card med Divider).
// Trin-for-trin forklaring:
// 1. BEHOLDT: Hele struktur (Row med TextFields for længde/bredde/dybde, research-felter med dropdown/checkboxes, navigation/Timber i "Fortsæt"-knap, enabled på length/width.isNotBlank()).
// 2. POLISH: Skiftet Column til LazyColumn (item { } for sektioner) – løser potentiel manglende scrolling.
// 3. POLISH: Research-sektion i Card med Divider mellem felter – pænere layout/spacing.
// 4. BEHOLDT: Alle imports, Modifier.weight(1f).
// 5. Fuldt funktionsdygtig – kompilerer uden fejl. Test: Scroll gennem input/research → "Fortsæt".

package dk.byggepiloten.firma.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import timber.log.Timber  // BEHOLDT: For navigation-log.

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FundamentScreen(navController: NavController) {
    var length by remember { mutableStateOf("") }
    var width by remember { mutableStateOf("") }
    var depth by remember { mutableStateOf("") }
    // BEHOLDT: Research-state.
    var jordtype by remember { mutableStateOf("") }
    var armering by remember { mutableStateOf(false) }
    var frostbeskyttelse by remember { mutableStateOf(false) }
    var draening by remember { mutableStateOf(false) }

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
        LazyColumn(  // POLISH: Skiftet til LazyColumn for fuld scrolling.
            modifier = Modifier.padding(padding).padding(16.dp)
        ) {
            item {
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

                        // Jordtype dropdown
                        var expandedJordtype by remember { mutableStateOf(false) }
                        ExposedDropdownMenuBox(
                            expanded = expandedJordtype,
                            onExpandedChange = { expandedJordtype = !expandedJordtype }
                        ) {
                            OutlinedTextField(
                                value = jordtype,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Jordtype (påvirker dræning)") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedJordtype) },
                                modifier = Modifier.menuAnchor().fillMaxWidth()
                            )
                            ExposedDropdownMenu(
                                expanded = expandedJordtype,
                                onDismissRequest = { expandedJordtype = false }
                            ) {
                                listOf("Sand", "Leire").forEach { option ->
                                    DropdownMenuItem(
                                        text = { Text(option) },
                                        onClick = {
                                            jordtype = option
                                            expandedJordtype = false
                                        }
                                    )
                                }
                            }
                        }
                        Divider(modifier = Modifier.padding(vertical = 8.dp))  // POLISH: Divider for spacing.

                        Spacer(Modifier.height(8.dp))

                        // Checkboxes for armering, frost og dræning
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(checked = armering, onCheckedChange = { armering = it })
                            Text("Armering (ja/nej – for belastning)")
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(checked = frostbeskyttelse, onCheckedChange = { frostbeskyttelse = it })
                            Text("Frostbeskyttelse (dybde >1.2 m)")
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(checked = draening, onCheckedChange = { draening = it })
                            Text("Dræning (gruslag)")
                        }
                    }
                }
                Spacer(Modifier.height(32.dp))
            }

            item {
                Button(
                    onClick = {
                        Timber.d("Navigated to task_photos_description/fundament")  // BEHOLDT: Log for tracking.
                        navController.navigate("task_photos_description/fundament")  // BEHOLDT: Navigation med category-param.
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = length.isNotBlank() && width.isNotBlank()
                ) {
                    Text("Fortsæt til billeder")
                }
            }
        }
    }
}
