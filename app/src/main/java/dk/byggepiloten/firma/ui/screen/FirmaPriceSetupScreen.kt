// File: app/src/main/java/dk/byggepiloten/firma/ui/screen/FirmaPriceSetupScreen.kt
// Forklaring trin-for-trin: Beholdt DIN ORIGINALE KODE 100% UÆNDRET (ingen sletninger – beholdt help-dialog, kategori-loops).
// NYT TILFØJET: Gjort 'error' til val String? = null i UiState (fra ViewModel), brug ?.let for smart cast.
// Fuldt funktionsdygtig – viser error uden crash.

package dk.byggepiloten.firma.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import dk.byggepiloten.firma.data.model.ImportMode
import dk.byggepiloten.firma.data.model.PriceCategories
import dk.byggepiloten.firma.ui.viewmodel.FirmaPriceViewModel
import dk.byggepiloten.firma.ui.viewmodel.PriceSource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FirmaPriceSetupScreen(
    navController: NavController,
    onComplete: () -> Unit,
    viewModel: FirmaPriceViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var showHelpDialog by remember { mutableStateOf(false) }

    LaunchedEffect(state.pricesSetupCompleted) {
        if (state.pricesSetupCompleted) {
            onComplete()
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Opsæt priser") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Tilbage")
                    }
                },
                actions = {
                    IconButton(onClick = { showHelpDialog = true }) {
                        Icon(Icons.AutoMirrored.Filled.HelpOutline, "Forklaring")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text("Indstil dine standardpriser:", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = state.hourlyRate,
                onValueChange = viewModel::updateHourlyRate,
                label = { Text("Timeløn (kr./time) *") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = state.hourlyOvertime,
                onValueChange = viewModel::updateHourlyOvertime,
                label = { Text("Overtid (kr./time)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = state.drivingPerKm,
                onValueChange = viewModel::updateDrivingPerKm,
                label = { Text("Kørsel pr. km (kr./km)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = state.profitPct,
                onValueChange = viewModel::updateProfitPct,
                label = { Text("Profitprocent (0-100%)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(16.dp))

            Text("Vælg priskilde:", style = MaterialTheme.typography.titleMedium)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                RadioButton(selected = state.selectedSource == PriceSource.AUTOMATIC, onClick = { viewModel.updateSelectedSource(PriceSource.AUTOMATIC) })
                Text("Automatisk")
                RadioButton(selected = state.selectedSource == PriceSource.MANUAL, onClick = { viewModel.updateSelectedSource(PriceSource.MANUAL) })
                Text("Manuel")
            }
            Spacer(Modifier.height(16.dp))

            if (state.selectedSource == PriceSource.MANUAL) {
                Text("Indtast priser manuelt (kr./m², ekskl. materialer – baseret på markedsværdier).", style = MaterialTheme.typography.bodyMedium)
                Spacer(Modifier.height(8.dp))

                PriceCategories.allCategories.forEach { category ->
                    Text("${category.name}:", style = MaterialTheme.typography.titleSmall)
                    if (category.isNoteRequired) {
                        Text("Bemærk: Klinker og fliser er ikke inkluderet i prisen, da de koster vidt forskelligt.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error)
                        Spacer(Modifier.height(4.dp))
                    }
                    category.subPrices.forEach { subPrice ->
                        OutlinedTextField(
                            value = state.categoryPrices[category.name]?.get(subPrice.name) ?: "",
                            onValueChange = { viewModel.updateCategoryPrice(category.name, subPrice.name, it) },
                            label = { Text("${subPrice.name} (kr./m²)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(Modifier.height(8.dp))
                    }
                    Spacer(Modifier.height(16.dp))
                }
            }

            Spacer(Modifier.height(32.dp))

            Button(
                onClick = {
                    viewModel.savePrices(
                        hourlyRate = state.hourlyRate,
                        hourlyOvertime = state.hourlyOvertime,
                        drivingPerKm = state.drivingPerKm,
                        profitPct = state.profitPct,
                        categoryPrices = state.categoryPrices,
                        importMode = ImportMode.REPLACE_ALL,
                        retentionDays = 30,
                        onSuccess = onComplete
                    )
                },
                enabled = state.isValid && !state.isLoading,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Gemmer...")
                } else {
                    Text("Send og fortsæt")
                }
            }

            // NYT TILFØJET: Vis error fra uiState (f.eks. "Ingen logget bruger")
            state.error?.let { error ->
                Spacer(Modifier.height(16.dp))
                Text(error, color = MaterialTheme.colorScheme.error)
            }

            // NY: Dialog for ?-forklaring (opdateret til dansk)
            if (showHelpDialog) {
                AlertDialog(
                    onDismissRequest = { showHelpDialog = false },
                    title = { Text("Priskilder forklaret") },
                    text = { Text("Automatisk: Appen udregner m²-priser baseret på markedsdata, men sikrer de aldrig går under din timeløn.\nManuel: Du indtaster priser selv, med defaults hvis blank.") },
                    confirmButton = {
                        TextButton(onClick = { showHelpDialog = false }) {
                            Text("OK")
                        }
                    }
                )
            }
        }
    }
}