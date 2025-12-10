// File: app/src/main/java/dk/byggepiloten/firma/ui/screen/FirmaSeekingDetailsScreen.kt
// OPDATERET VERSION – rettet import for KeyboardOptions til androidx.compose.foundation.text.KeyboardOptions (som per reglerne).
// Trin-for-trin forklaring:
// 1. Ændret import: Fra androidx.compose.ui.text.input.KeyboardOptions til androidx.compose.foundation.text.KeyboardOptions – løser regelfejlen.
// 2. Beholdt alt andet identisk fra tidligere version: State, ViewModel-binding, validering, UI-elementer.
// 3. Ingen andre ændringer – fuld, funktionsdygtig kode.
// 4. Offline-first: Antager ViewModel håndterer sync.
// 5. Preview beholdt.
// 6. Nu matcher reglerne 100 %.

package dk.byggepiloten.firma.ui.screen

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import dk.byggepiloten.firma.ui.theme.ByggePilotenTheme
import dk.byggepiloten.firma.ui.viewmodel.FirmaSeekingDetailsViewModel
import dk.byggepiloten.firma.ui.viewmodel.OnboardingViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FirmaSeekingDetailsScreen(
    navController: NavController,
    onboardingViewModel: OnboardingViewModel = hiltViewModel(),
    onComplete: () -> Unit = {}
) {
    val viewModel: FirmaSeekingDetailsViewModel = hiltViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var showSuccessDialog by remember { mutableStateOf(false) }

    ByggePilotenTheme {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("Firma søger håndværkere") },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Tilbage")
                        }
                    }
                )
            }
        ) { innerPadding ->
            LazyColumn(
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    OutlinedTextField(
                        value = state.firmaName,
                        onValueChange = { viewModel.updateFirmaName(it) },
                        label = { Text("Firma-navn *") },
                        leadingIcon = { Icon(Icons.Default.Business, null) },
                        isError = state.firmaNameError,
                        supportingText = if (state.firmaNameError) { { Text("Firma-navn er påkrævet") } } else null,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    OutlinedTextField(
                        value = state.cvr,
                        onValueChange = { if (it.length <= 8 && it.all { it.isDigit() }) viewModel.updateCvr(it) },
                        label = { Text("CVR *") },
                        leadingIcon = { Icon(Icons.Default.BusinessCenter, null) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        isError = state.cvrError,
                        supportingText = if (state.cvrError) { { Text("Ugyldigt CVR (skal være 8 cifre)") } } else null,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    OutlinedTextField(
                        value = state.address,
                        onValueChange = { viewModel.updateAddress(it) },
                        label = { Text("Adresse *") },
                        leadingIcon = { Icon(Icons.Default.Home, null) },
                        isError = state.addressError,
                        supportingText = if (state.addressError) { { Text("Adresse er påkrævet") } } else null,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    OutlinedTextField(
                        value = state.email,
                        onValueChange = { viewModel.updateEmail(it) },
                        label = { Text("E-mail *") },
                        leadingIcon = { Icon(Icons.Default.Email, null) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        isError = state.emailError,
                        supportingText = if (state.emailError) { { Text("Ugyldig e-mail") } } else null,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    OutlinedTextField(
                        value = state.phone,
                        onValueChange = { viewModel.updatePhone(it) },
                        label = { Text("Telefon (valgfri)") },
                        leadingIcon = { Icon(Icons.Default.Phone, null) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    OutlinedTextField(
                        value = state.bankAccount,
                        onValueChange = { viewModel.updateBankAccount(it) },
                        label = { Text("Bankkonto *") },
                        leadingIcon = { Icon(Icons.Default.AccountBalance, null) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        isError = state.bankAccountError,
                        supportingText = if (state.bankAccountError) { { Text("Bankkonto er påkrævet (f.eks. 1234 5678 90)") } } else null,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = state.gdprChecked,
                            onCheckedChange = { viewModel.updateGdprChecked(it) }
                        )
                        Text("Jeg accepterer GDPR-betingelserne")
                    }
                }

                item {
                    Button(
                        onClick = {
                            viewModel.saveDetails {
                                onboardingViewModel.completeOnboarding()
                                showSuccessDialog = true
                                onComplete()
                            }
                        },
                        enabled = state.isValid && state.gdprChecked && !state.isLoading,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        if (state.isLoading) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Gemmer...")
                        } else {
                            Text("Gem og fortsæt")
                        }
                    }
                }

                item {
                    AnimatedVisibility(visible = !state.isValid || !state.gdprChecked, enter = fadeIn()) {
                        Text(
                            "Udfyld alle påkrævede felter og accepter GDPR",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }

            if (showSuccessDialog) {
                AlertDialog(
                    onDismissRequest = { showSuccessDialog = false },
                    title = { Text("Profil oprettet!") },
                    text = { Text("Du kan nu poste opgaver.") },
                    confirmButton = {
                        TextButton(onClick = { showSuccessDialog = false }) { Text("Super!") }
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FirmaSeekingDetailsScreenPreview() {
    ByggePilotenTheme {
        FirmaSeekingDetailsScreen(rememberNavController())
    }
}