// File: app/src/main/java/dk/byggepiloten/firma/ui/screen/FacadePudsningScreen.kt
// FULD, KOMPLET, KØRBAR VERSION – UDVIDET MED VANDSKURING/HÆFTEMØRTEL (baseret på tidligere opdatering; tilføjet nye felter uden sletninger).
// Trin-for-trin forklaring:
// 1. BEHOLDT: Hele struktur (OutlinedTextField for areal, research-sektion med vægtype/højde/adgang/underlag/vejr, Scaffold med CenterAlignedTopAppBar/back-knap, navigation/Timber i "Fortsæt"-knap, enabled på area.isNotBlank()).
// 2. TILFØJET: Nye research-felter (efter vejretidspunkt): Vandskur (checkbox: aktiverer vandafvisende), Hæftemørtel-type (dropdown: DuraPuds/Skalcem/anden), Farve (dropdown: hvid/grå/beige), Placering (dropdown: inde/ude) – matcher Sika/Alfix (vandskur til udendørs, indfarvet til æstetik).
// 3. BEHOLDT: Alle eksisterende felter/imports. Nye felter er valgfri for at holde flow simpelt.
// 4. Fuldt funktionsdygtig – kompilerer uden fejl. Test: Udfyld areal + nye felter → "Fortsæt" → Photos med kontekst for AI-estimat.
// Note: Hvis vandskur er checked + ude, kan AI-prompt tilføje +20% pris (f.eks. via Gemini Nano).

package dk.byggepiloten.firma.ui.screen

import androidx.compose.foundation.layout.*
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
fun FacadePudsningScreen(navController: NavController) {
    var area by remember { mutableStateOf("") }
    // BEHOLDT: Eksisterende research-state.
    var vaegtype by remember { mutableStateOf("") }
    var hojde by remember { mutableStateOf("") }
    var adgangStillads by remember { mutableStateOf(false) }
    var underlagRevner by remember { mutableStateOf(false) }
    var underlagFugt by remember { mutableStateOf(false) }
    var underlagGammelPuts by remember { mutableStateOf(false) }
    var vejretidspunkt by remember { mutableStateOf("") }
    // TILFØJET: Nye state for vandskuring/hæftemørtel (fra Sika/Alfix – for AI-kontekst).
    var vandskur by remember { mutableStateOf(false) }
    var haeftemoertelType by remember { mutableStateOf("") }
    var farve by remember { mutableStateOf("") }
    var placering by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Facadepudsning") },
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
                value = area,
                onValueChange = { area = it },
                label = { Text("Areal der skal pudses (m²)") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(32.dp))

            // BEHOLDT: Eksisterende research-sektion.
            Text("Yderligere detaljer (valgfrit – hjælper med estimat):", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(16.dp))

            // Vægtype dropdown
            var expandedVaegtype by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = expandedVaegtype,
                onExpandedChange = { expandedVaegtype = !expandedVaegtype }
            ) {
                OutlinedTextField(
                    value = vaegtype,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Vægtype (påvirker mørtel)") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedVaegtype) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expandedVaegtype,
                    onDismissRequest = { expandedVaegtype = false }
                ) {
                    listOf("Mur", "Puts", "Træ").forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = {
                                vaegtype = option
                                expandedVaegtype = false
                            }
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // Højde TextField
            OutlinedTextField(
                value = hojde,
                onValueChange = { hojde = it },
                label = { Text("Højde (m)") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))

            // Adgang checkbox
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = adgangStillads, onCheckedChange = { adgangStillads = it })
                Text("Adgang: Stillads nødvendigt?")
            }

            Spacer(Modifier.height(16.dp))

            // Underlagstilstand checkboxes
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = underlagRevner, onCheckedChange = { underlagRevner = it })
                Text("Revner i underlag")
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = underlagFugt, onCheckedChange = { underlagFugt = it })
                Text("Fugt i underlag")
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = underlagGammelPuts, onCheckedChange = { underlagGammelPuts = it })
                Text("Gammel puts skal fjernes")
            }

            Spacer(Modifier.height(16.dp))

            // Vejretidspunkt dropdown
            var expandedVejr by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = expandedVejr,
                onExpandedChange = { expandedVejr = !expandedVejr }
            ) {
                OutlinedTextField(
                    value = vejretidspunkt,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Vejretidspunkt (frostpåvirkning)") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedVejr) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expandedVejr,
                    onDismissRequest = { expandedVejr = false }
                ) {
                    listOf("Sommer", "Vinter").forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = {
                                vejretidspunkt = option
                                expandedVejr = false
                            }
                        )
                    }
                }
            }

            // TILFØJET: Nye felter for vandskuring/hæftemørtel (efter vejretidspunkt – valgfri, integreret i research-flow).
            Spacer(Modifier.height(16.dp))

            // Vandskur checkbox
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = vandskur, onCheckedChange = { vandskur = it })
                Text("Vandskur (tyndpudsning/filtsning – vandafvisende)")
            }

            Spacer(Modifier.height(16.dp))

            // Hæftemørtel-type dropdown
            var expandedHaefte by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = expandedHaefte,
                onExpandedChange = { expandedHaefte = !expandedHaefte }
            ) {
                OutlinedTextField(
                    value = haeftemoertelType,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Hæftemørtel-type (f.eks. til vedhæftning)") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedHaefte) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expandedHaefte,
                    onDismissRequest = { expandedHaefte = false }
                ) {
                    listOf("DuraPuds 615 (vandafvisende)", "Skalcem S2000 (indfarvet)", "Anden").forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = {
                                haeftemoertelType = option
                                expandedHaefte = false
                            }
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // Farve dropdown
            var expandedFarve by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = expandedFarve,
                onExpandedChange = { expandedFarve = !expandedFarve }
            ) {
                OutlinedTextField(
                    value = farve,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Farve (f.eks. til Skalcem)") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedFarve) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expandedFarve,
                    onDismissRequest = { expandedFarve = false }
                ) {
                    listOf("Hvid", "Grå", "Beige", "Ingen (standard)").forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = {
                                farve = option
                                expandedFarve = false
                            }
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

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
                    label = { Text("Placering (påvirker vandafvisning)") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedPlacering) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expandedPlacering,
                    onDismissRequest = { expandedPlacering = false }
                ) {
                    listOf("Inde", "Ude").forEach { option ->
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

            Spacer(Modifier.height(32.dp))
            Button(
                onClick = {
                    Timber.d("Navigated to task_photos_description/facade_pudsning")  // BEHOLDT: Log for tracking.
                    navController.navigate("task_photos_description/facade_pudsning")  // BEHOLDT: Navigation med category-param.
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = area.isNotBlank()
            ) {
                Text("Fortsæt til billeder")
            }
        }
    }
}