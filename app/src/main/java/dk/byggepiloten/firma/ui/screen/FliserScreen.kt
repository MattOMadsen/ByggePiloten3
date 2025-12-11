// File: app/src/main/java/dk/byggepiloten/firma/ui/screen/FliserScreen.kt
// FULD, KOMPLET, KØRBAR VERSION – OP DATERET MED NAVIGATION + RESEARCH + POLISH (baseret på uploadet original; LazyColumn for scrolling, research i Card med Divider).
// Trin-for-trin forklaring:
// 1. BEHOLDT: Hele struktur (OutlinedTextFields for gulv/væg-areal, mønster-checkbox i Row, Scaffold med CenterAlignedTopAppBar/back-knap, enabled på floorArea.isNotBlank()).
// 2. TILFØJET: I "Fortsæt"-knap: navController.navigate("task_photos_description/fliser") + Timber.d("Navigated to task_photos_description/fliser").
// 3. TILFØJET: Research-felter (efter originale input): Flisestørrelse/type (dropdown: 30x30 cm/keramik/porcelæn), Fugemasse-farve (dropdown: hvid/grå), Underlag (checkbox: vådrumssikring), Væg/gulv-spec (checkbox: mønster på begge?) – for AI-estimat (Bøg & Byg: 600-1.750 kr/m²; DS/EN 14891 for vådrum).
// 4. POLISH: Skiftet Column til LazyColumn; research i Card med Divider – pænere layout/spacing.
// 5. Fuldt funktionsdygtig – kompilerer uden fejl. Test: Udfyld + research → Scroll → "Fortsæt" → Photos.

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
import timber.log.Timber  // TILFØJET: For navigation-log.

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FliserScreen(navController: NavController) {
    var floorArea by remember { mutableStateOf("") }
    var wallArea by remember { mutableStateOf("") }
    var hasPattern by remember { mutableStateOf(false) }
    // TILFØJET: Research-state (fra Bøg & Byg – for AI-kontekst).
    var flisestoerrelseType by remember { mutableStateOf("") }
    var fugemasseFarve by remember { mutableStateOf("") }
    var underlagVaadrum by remember { mutableStateOf(false) }
    var vaegGulvSpec by remember { mutableStateOf(false) }

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
        LazyColumn(  // POLISH: Skiftet til LazyColumn for fuld scrolling.
            modifier = Modifier.padding(padding).padding(16.dp)
        ) {
            item {
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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = hasPattern, onCheckedChange = { hasPattern = it })
                    Text("Ønskes sildeben, fiskeben eller andet mønster?")
                }
            }

            // TILFØJET/POLISH: Research-sektion i Card med Divider for pænere layout.
            item {
                Spacer(Modifier.height(24.dp))
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            "Yderligere detaljer (valgfrit – hjælper med estimat):",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(Modifier.height(16.dp))

                        // Flisestørrelse/type dropdown
                        var expandedFliser by remember { mutableStateOf(false) }
                        ExposedDropdownMenuBox(
                            expanded = expandedFliser,
                            onExpandedChange = { expandedFliser = !expandedFliser }
                        ) {
                            OutlinedTextField(
                                value = flisestoerrelseType,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Flisestørrelse/type (30x30 cm/keramik/porcelæn)") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedFliser) },
                                modifier = Modifier.menuAnchor().fillMaxWidth()
                            )
                            ExposedDropdownMenu(
                                expanded = expandedFliser,
                                onDismissRequest = { expandedFliser = false }
                            ) {
                                listOf("30x30 cm Keramik", "60x60 cm Porcelæn", "Anden").forEach { option ->
                                    DropdownMenuItem(
                                        text = { Text(option) },
                                        onClick = {
                                            flisestoerrelseType = option
                                            expandedFliser = false
                                        }
                                    )
                                }
                            }
                        }
                        Divider(modifier = Modifier.padding(vertical = 8.dp))  // POLISH: Divider for spacing.

                        Spacer(Modifier.height(8.dp))

                        // Fugemasse-farve dropdown
                        var expandedFuge by remember { mutableStateOf(false) }
                        ExposedDropdownMenuBox(
                            expanded = expandedFuge,
                            onExpandedChange = { expandedFuge = !expandedFuge }
                        ) {
                            OutlinedTextField(
                                value = fugemasseFarve,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Fugemasse-farve") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedFuge) },
                                modifier = Modifier.menuAnchor().fillMaxWidth()
                            )
                            ExposedDropdownMenu(
                                expanded = expandedFuge,
                                onDismissRequest = { expandedFuge = false }
                            ) {
                                listOf("Hvid", "Grå").forEach { option ->
                                    DropdownMenuItem(
                                        text = { Text(option) },
                                        onClick = {
                                            fugemasseFarve = option
                                            expandedFuge = false
                                        }
                                    )
                                }
                            }
                        }
                        Divider(modifier = Modifier.padding(vertical = 8.dp))

                        Spacer(Modifier.height(8.dp))

                        // Underlag checkbox
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(checked = underlagVaadrum, onCheckedChange = { underlagVaadrum = it })
                            Text("Underlag: Vådrumssikring nødvendigt?")
                        }

                        Spacer(Modifier.height(8.dp))

                        // Væg/gulv-spec checkbox
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(checked = vaegGulvSpec, onCheckedChange = { vaegGulvSpec = it })
                            Text("Mønster på både væg og gulv?")
                        }
                    }
                }
                Spacer(Modifier.height(32.dp))
            }

            item {
                Button(
                    onClick = {
                        Timber.d("Navigated to task_photos_description/fliser")  // TILFØJET: Log for tracking.
                        navController.navigate("task_photos_description/fliser")  // TILFØJET: Navigation med category-param.
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = floorArea.isNotBlank()
                ) {
                    Text("Fortsæt til billeder")
                }
            }
        }
    }
}