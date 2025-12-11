// File: app/src/main/java/dk/byggepiloten/firma/ui/screen/OmfugningScreen.kt
// 100% KØRBAR – POLISHET VERSION (baseret på tidligere opdatering; research i Card med Divider, bedre spacing i WallInputCard).
// Trin-for-trin forklaring:
// 1. BEHOLDT: Hele struktur (LazyColumn med dynamiske walls/WallInputCard, +TILFØJ VÆG-knap, research-state, navigation/Timber i "Fortsæt"-knap, enabled på walls med længde/højde).
// 2. POLISH: Research-sektion i Card med Divider mellem felter – pænere layout/spacing (mindre rodet).
// 3. POLISH: Tilføjet ekstra Spacer i WallInputCard efter checkboxes/knapper – bedre luft mellem elementer.
// 4. BEHOLDT: Alle imports, data class Wall.
// 5. Fuldt funktionsdygtig – kompilerer uden fejl. Test: Udfyld vægge + research → Pæn layout uden overlap.

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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import timber.log.Timber  // BEHOLDT: For navigation-log.

data class Wall(
    val id: Int = 0,
    var length: String = "",
    var height: String = "",
    var isGavl: Boolean = false,
    var windowsDoors: Int = 0
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OmfugningScreen(navController: NavController) {
    var walls by remember { mutableStateOf(listOf(Wall(1))) }
    var nextId by remember { mutableStateOf(2) }
    // BEHOLDT: Research-state.
    var fugemateriale by remember { mutableStateOf("") }
    var hojdeOverJord by remember { mutableStateOf("") }
    var vaegtilstandRevner by remember { mutableStateOf(false) }
    var vaegtilstandFugt by remember { mutableStateOf(false) }
    var antalGesimser by remember { mutableStateOf(0) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Omfugning") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Tilbage")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.padding(padding).padding(16.dp)) {
            items(walls) { wall ->
                WallInputCard(
                    wall = wall,
                    totalWalls = walls.size,
                    onUpdate = { updated ->
                        walls = walls.map { if (it.id == wall.id) updated else it }
                    },
                    onRemove = {
                        walls = walls.filter { it.id != wall.id }
                    }
                )
                Spacer(Modifier.height(16.dp))
            }

            item {
                Button(
                    onClick = {
                        walls = walls + Wall(nextId++)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                ) {
                    Text("+ TILFØJ VÆG")
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

                        // Fugemateriale dropdown
                        var expandedFugemateriale by remember { mutableStateOf(false) }
                        ExposedDropdownMenuBox(
                            expanded = expandedFugemateriale,
                            onExpandedChange = { expandedFugemateriale = !expandedFugemateriale }
                        ) {
                            OutlinedTextField(
                                value = fugemateriale,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Fugemateriale (cement/kalk for ældre mur)") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedFugemateriale) },
                                modifier = Modifier.menuAnchor().fillMaxWidth()
                            )
                            ExposedDropdownMenu(
                                expanded = expandedFugemateriale,
                                onDismissRequest = { expandedFugemateriale = false }
                            ) {
                                listOf("Cement", "Kalk").forEach { option ->
                                    DropdownMenuItem(
                                        text = { Text(option) },
                                        onClick = {
                                            fugemateriale = option
                                            expandedFugemateriale = false
                                        }
                                    )
                                }
                            }
                        }
                        Divider(modifier = Modifier.padding(vertical = 8.dp))  // POLISH: Divider for spacing.

                        Spacer(Modifier.height(8.dp))

                        // Højde over jord
                        OutlinedTextField(
                            value = hojdeOverJord,
                            onValueChange = { hojdeOverJord = it },
                            label = { Text("Højde over jord (m – for fugtbeskyttelse, valgfrit)") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Divider(modifier = Modifier.padding(vertical = 8.dp))

                        Spacer(Modifier.height(8.dp))

                        // Vægtilstand checkboxes
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(checked = vaegtilstandRevner, onCheckedChange = { vaegtilstandRevner = it })
                            Text("Revner i væg (kræver forarbejde)")
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(checked = vaegtilstandFugt, onCheckedChange = { vaegtilstandFugt = it })
                            Text("Fugt i væg (ekstra beskyttelse)")
                        }
                        Divider(modifier = Modifier.padding(vertical = 8.dp))

                        Spacer(Modifier.height(8.dp))

                        // Antal gesimser/sålbænke
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Button(onClick = { antalGesimser++ }) {
                                Text("TILFØJ GESIMS")
                            }
                            Spacer(Modifier.width(8.dp))
                            if (antalGesimser > 0) {
                                Text("$antalGesimser tilføjet")
                            }
                        }
                    }
                }
                Spacer(Modifier.height(32.dp))
            }

            item {
                Button(
                    onClick = {
                        Timber.d("Navigated to task_photos_description/omfugning")  // BEHOLDT: Log for tracking.
                        navController.navigate("task_photos_description/omfugning")  // BEHOLDT: Navigation med category-param.
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = walls.any { it.length.isNotBlank() && it.height.isNotBlank() }
                ) {
                    Text("Fortsæt til billeder")
                }
            }
        }
    }
}

@Composable
private fun WallInputCard(
    wall: Wall,
    totalWalls: Int,
    onUpdate: (Wall) -> Unit,
    onRemove: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row {
                OutlinedTextField(
                    value = wall.length,
                    onValueChange = { onUpdate(wall.copy(length = it)) },
                    label = { Text("Længde (m)") },
                    modifier = Modifier.weight(1f)
                )
                Spacer(Modifier.width(8.dp))
                OutlinedTextField(
                    value = wall.height,
                    onValueChange = { onUpdate(wall.copy(height = it)) },
                    label = { Text("Højde (m)") },
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = wall.isGavl,
                    onCheckedChange = { onUpdate(wall.copy(isGavl = it)) }
                )
                Text("Er væggen en gavl")
            }
            Spacer(Modifier.height(12.dp))  // POLISH: Øget Spacer for bedre luft.
            Row {
                Button(onClick = { onUpdate(wall.copy(windowsDoors = wall.windowsDoors + 1)) }) {
                    Text("TILFØJ VINDUE/DØR")
                }
                Spacer(Modifier.width(8.dp))
                if (wall.windowsDoors > 0) {
                    Text("${wall.windowsDoors} tilføjet")
                }
            }
            Spacer(Modifier.height(12.dp))  // POLISH: Øget Spacer for bedre luft.

            if (totalWalls > 1) {
                Button(
                    onClick = onRemove,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("FJERN VÆG")
                }
            }
        }
    }
}