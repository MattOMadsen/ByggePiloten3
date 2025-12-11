// File: app/src/main/java/dk/byggepiloten/firma/ui/screen/NedbrydningScreen.kt
// FULD, KOMPLET, KØRBAR VERSION – POLISHET (baseret på tidligere opdatering; LazyColumn for scrolling, research i Card med Divider).
// Trin-for-trin forklaring:
// 1. BEHOLDT: Hele struktur (OutlinedTextField for areal, asbest-checkbox, research-felter med dropdown/checkboxes, navigation/Timber i "Fortsæt"-knap, enabled på area.isNotBlank()).
// 2. POLISH: Skiftet Column til LazyColumn (item { } for sektioner) – løser potentiel manglende scrolling.
// 3. POLISH: Research-sektion i Card med Divider mellem felter – pænere layout/spacing (mindre rodet).
// 4. BEHOLDT: Alle imports, Alignment.CenterVertically.
// 5. Fuldt funktionsdygtig – kompilerer uden fejl. Test: Scroll gennem areal/asbest/research → "Fortsæt".

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
fun NedbrydningScreen(navController: NavController) {
    var area by remember { mutableStateOf("") }
    var hasAsbestos by remember { mutableStateOf(false) }
    // BEHOLDT: Research-state.
    var vaegType by remember { mutableStateOf("") }
    var materialerVaeg by remember { mutableStateOf(false) }
    var materialerFliser by remember { mutableStateOf(false) }
    var materialerGips by remember { mutableStateOf(false) }
    var stoeyAffaldContainer by remember { mutableStateOf(false) }
    var sikkerhedStoev by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Nedbrydning") },
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
                OutlinedTextField(
                    value = area,
                    onValueChange = { area = it },
                    label = { Text("Areal der skal rives ned (m²)") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(16.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = hasAsbestos, onCheckedChange = { hasAsbestos = it })
                    Text("Mulighed for asbest (ældre hus)?")
                }
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

                        // Væg-type dropdown
                        var expandedVaegType by remember { mutableStateOf(false) }
                        ExposedDropdownMenuBox(
                            expanded = expandedVaegType,
                            onExpandedChange = { expandedVaegType = !expandedVaegType }
                        ) {
                            OutlinedTextField(
                                value = vaegType,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Væg-type (bærende kræver statiker/tilladelse)") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedVaegType) },
                                modifier = Modifier.menuAnchor().fillMaxWidth()
                            )
                            ExposedDropdownMenu(
                                expanded = expandedVaegType,
                                onDismissRequest = { expandedVaegType = false }
                            ) {
                                listOf("Bærende", "Ikke-bærende").forEach { option ->
                                    DropdownMenuItem(
                                        text = { Text(option) },
                                        onClick = {
                                            vaegType = option
                                            expandedVaegType = false
                                        }
                                    )
                                }
                            }
                        }
                        Divider(modifier = Modifier.padding(vertical = 8.dp))  // POLISH: Divider for spacing.

                        Spacer(Modifier.height(8.dp))

                        // Materialer checkboxes
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(checked = materialerVaeg, onCheckedChange = { materialerVaeg = it })
                            Text("Væg (mur/gips)")
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(checked = materialerFliser, onCheckedChange = { materialerFliser = it })
                            Text("Fliser/klinker")
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(checked = materialerGips, onCheckedChange = { materialerGips = it })
                            Text("Gipsplader")
                        }
                        Divider(modifier = Modifier.padding(vertical = 8.dp))

                        Spacer(Modifier.height(8.dp))

                        // Støv/affald og sikkerhed
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(checked = stoeyAffaldContainer, onCheckedChange = { stoeyAffaldContainer = it })
                            Text("Container til affald nødvendigt")
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(checked = sikkerhedStoev, onCheckedChange = { sikkerhedStoev = it })
                            Text("Støvbeskyttelse (sikkerhed)")
                        }
                    }
                }
                Spacer(Modifier.height(32.dp))
            }

            item {
                Button(
                    onClick = {
                        Timber.d("Navigated to task_photos_description/nedbrydning")  // BEHOLDT: Log for tracking.
                        navController.navigate("task_photos_description/nedbrydning")  // BEHOLDT: Navigation med category-param.
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = area.isNotBlank()
                ) {
                    Text("Fortsæt til billeder")
                }
            }
        }
    }
}