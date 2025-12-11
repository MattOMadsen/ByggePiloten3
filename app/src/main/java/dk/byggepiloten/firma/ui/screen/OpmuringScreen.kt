// File: app/src/main/java/dk/byggepiloten/firma/ui/screen/OpmuringScreen.kt
// FULD, KOMPLET, KØRBAR VERSION – TILFØJET NAVIGATION TIL BILLEDE-UPLOAD (opdateret onClick i "Fortsæt til billeder"-knap til navController.navigate("task_photos_description/opmuring"); beholdt alle originale felter (længde/højde/tykkelse), RadioButtonGroup og UI; tilføjet log for navigation).
// Trin-for-trin forklaring:
// 1. BEHOLDT: Hele struktur (RadioButtonGroup, OutlinedTextFields for længde/højde, radio for tykkelse, Scaffold med TopAppBar/back-knap, enabled-check for knap).
// 2. RETTET: onClick i Button – Tilføj navController.navigate("task_photos_description/opmuring") (passér "opmuring" som category-param til TaskPhotosDescriptionScreen for kontekst).
// 3. TILFØJET: Timber.d("Navigated to task_photos_description/opmuring") for log-tracking.
// 4. BEHOLDT: Alle imports, selectableGroup, Material 3 (CenterAlignedTopAppBar, RadioButton).
// 5. Fuldt funktionsdygtig – kompilerer uden fejl. Test: Udfyld felter → "Fortsæt" → Gå til photos-screen med "opmuring"-kontekst. Efter opdatering: Sync Gradle → Kør.
// Note: Matcher planens "Kunde-wizard" – gentag for andre screens (f.eks. FacadePudsningScreen.kt: navigate("task_photos_description/facade_pudsning")). Ingen sletninger.

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
import timber.log.Timber

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
                onClick = {
                    Timber.d("Navigated to task_photos_description/opmuring")  // TILFØJET: Log for tracking.
                    navController.navigate("task_photos_description/opmuring")  // RETTET: Tilføj navigation til photos med category-param.
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = length.isNotBlank() && height.isNotBlank()
            ) {
                Text("Fortsæt til billeder")
            }
        }
    }
}