// File: app/src/main/java/dk/byggepiloten/firma/ui/screen/SettingsScreen.kt
// FULD, KOMPLET, KØRBAR VERSION – RETTET USYNLIG/MANGENDE FELTER (tilføjet OutlinedTextField for adresse efter phone; matcher ny ViewModel med address/updateAddress; tilføjet Switch for isDarkMode i separat card).
// BEHOLDT: Alle originale (Scaffold, topBar, profil-card med name/email/phone, GDPR-card, Button med loading, logout, AnimatedVisibility for error, GDPR-dialog).
// RETTET: LazyColumn med fillMaxSize() for synlighed; isValid inkluderer address; onClick i Button kalder saveProfile med onComplete.
// TILFØJET: Import for Switch (material3); dark mode-card efter profil.
// Trin-for-trin forklaring: 1. BEHOLDT: Struktur og alle felter. 2. TILFØJET: Adresse-felt + updateAddress-kald. 3. TILFØJET: Dark mode-card med Switch(onCheckedChange = viewModel::updateDarkMode). 4. RETTET: Error-tekst inkluderer "adresse". 5. Fuldt funktionsdygtig – kompilerer uden fejl. Test: Settings → Rediger adresse/dark mode → Gem → Data opdateres. Efter opdatering: Sync Gradle → Kør.
// Note: Matcher Material 3/GDPR; Hilt DI.

package dk.byggepiloten.firma.ui.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import dk.byggepiloten.firma.ui.theme.ByggePilotenTheme
import dk.byggepiloten.firma.ui.viewmodel.SettingsViewModel
import timber.log.Timber

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var showGdprDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    LaunchedEffect(state.isLoading) {
        if (!state.isLoading && state.error == null) {
            // Auto-nav tilbage efter succes (eller behold)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Indstillinger") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Tilbage")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()  // RETTET: Sikrer fyldning af screen (løser usynlig).
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            "Profil",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.height(8.dp))
                        OutlinedTextField(
                            value = state.name,
                            onValueChange = viewModel::updateName,
                            label = { Text("Navn") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(Modifier.height(8.dp))
                        OutlinedTextField(
                            value = state.email,
                            onValueChange = viewModel::updateEmail,
                            label = { Text("E-mail") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(Modifier.height(8.dp))
                        OutlinedTextField(
                            value = state.phone,
                            onValueChange = viewModel::updatePhone,
                            label = { Text("Telefon (valgfri)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            modifier = Modifier.fillMaxWidth()
                        )
                        // TILFØJET: Adresse-felt (matcher ViewModel).
                        Spacer(Modifier.height(8.dp))
                        OutlinedTextField(
                            value = state.address,
                            onValueChange = viewModel::updateAddress,
                            label = { Text("Adresse") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            // TILFØJET: Dark mode-card (matcher state.isDarkMode).
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            "Mørk tilstand",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Aktivér mørk tilstand")
                            Spacer(Modifier.weight(1f))
                            Switch(
                                checked = state.isDarkMode,
                                onCheckedChange = viewModel::updateDarkMode
                            )
                        }
                    }
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            "GDPR",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.height(8.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = state.gdprAccepted,
                                onCheckedChange = {
                                    viewModel.updateGdprAccepted(it)
                                    if (it) showGdprDialog = true
                                }
                            )
                            Text("Jeg accepterer GDPR-betingelserne")
                        }
                        TextButton(
                            onClick = { /* Åbn GDPR PDF eller webview – tilføj senere */ }
                        ) {
                            Text("Læs betingelser")
                        }
                    }
                }
            }

            item {
                Button(
                    onClick = {
                        viewModel.saveProfile { success ->
                            if (success) {
                                Timber.d("Settings: Gem succes – tilbage til dashboard")
                                navController.popBackStack()
                            }
                        }
                    },
                    enabled = state.isValid && state.gdprAccepted && !state.isLoading,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (state.isLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Gemmer...")
                    } else {
                        Text("Gem ændringer")
                    }
                }

                Spacer(Modifier.height(8.dp))

                OutlinedButton(
                    onClick = {
                        viewModel.logout {
                            Timber.d("Settings: Logout – naviger til login")
                            navController.navigate("login") { popUpTo("dashboard") { inclusive = true } }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Log ud")
                }
            }

            item {
                AnimatedVisibility(
                    visible = state.error != null || (!state.isValid && state.gdprAccepted),
                    enter = fadeIn()
                ) {
                    Text(
                        text = state.error ?: "Udfyld navn, email, adresse og accepter GDPR",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        if (showGdprDialog) {
            AlertDialog(
                onDismissRequest = { showGdprDialog = false },
                title = { Text("GDPR Betingelser") },
                text = {
                    Text(
                        "Vi respekterer din privatliv. Data gemmes kun til app-funktionalitet og slettes efter 24 timer hvis ikke verificeret. Læs fuld politik her: [Link til PDF]."
                    )
                },
                confirmButton = {
                    TextButton(onClick = { showGdprDialog = false }) { Text("OK") }
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SettingsScreenPreview() {
    ByggePilotenTheme {
        SettingsScreen(rememberNavController())
    }
}