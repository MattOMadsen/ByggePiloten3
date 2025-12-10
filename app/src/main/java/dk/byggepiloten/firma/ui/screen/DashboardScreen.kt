package dk.byggepiloten.firma.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseUser
import dk.byggepiloten.firma.data.repository.AuthRepository
import dk.byggepiloten.firma.data.model.Request
import dk.byggepiloten.firma.ui.viewmodel.DashboardViewModel
import timber.log.Timber
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(navController: NavController, authRepository: AuthRepository) {
    val viewModel: DashboardViewModel = hiltViewModel()
    val requests by viewModel.requests.collectAsStateWithLifecycle(emptyList())
    val role by viewModel.role.collectAsStateWithLifecycle(null)  // RETTET: Brug viewModel.role – løser unresolved role.
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle(true)
    val isEmailVerified by viewModel.isEmailVerified.collectAsStateWithLifecycle(false)  // RETTET: Brug viewModel.isEmailVerified – løser unresolved isEmailVerified.
    val showVerificationDialog by viewModel.showVerificationDialog.collectAsStateWithLifecycle(false)  // RETTET: Brug viewModel.showVerificationDialog – løser unresolved showVerificationDialog.

    val currentUser = authRepository.getCurrentUser()

    LaunchedEffect(currentUser) {
        val userId = currentUser?.uid ?: return@LaunchedEffect
        viewModel.loadRequests(userId)
        Timber.d("DashboardViewModel: Loader requests for userId: $userId")
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Dashboard") },
                actions = {
                    IconButton(onClick = { navController.navigate("settings") }) {
                        Icon(Icons.Default.Settings, contentDescription = "Indstillinger")
                    }
                    IconButton(onClick = { viewModel.logout { success ->
                        if (success) navController.navigate("login") { popUpTo(0) { inclusive = true } }
                    } }) {
                        Icon(Icons.Default.ExitToApp, contentDescription = "Log ud")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate("new_task_wizard") }) {
                Icon(Icons.Default.Add, contentDescription = "Ny opgave")
            }
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = null) },
                    label = { Text("Forside") },
                    selected = true,
                    onClick = { /* Already on dashboard */ }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Build, contentDescription = null) },
                    label = { Text("Opgaver") },
                    selected = false,
                    onClick = { navController.navigate("tasks") }
                )
            }
        }
    ) { padding ->
        if (isLoading) {
            Box(Modifier.fillMaxSize(), Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            val normalizedRole = role?.lowercase(Locale.ROOT)
            when (normalizedRole) {
                "private" -> PrivateDashboard(padding, requests, navController, isEmailVerified, viewModel::resendVerification)
                "contractor" -> ContractorDashboard(padding, navController)
                else -> Box(Modifier.fillMaxSize(), Alignment.Center) {
                    Text("Ugyldig rolle – log ud og prøv igen")
                }
            }
            Timber.d("DashboardScreen: Role: $role – normalized: $normalizedRole – matcher?")
        }

        if (showVerificationDialog) {
            AlertDialog(
                onDismissRequest = { viewModel.dismissVerificationDialog() },
                title = { Text("Bekræft din e-mail") },
                text = { Text("Du skal bekræfte din e-mail for at fortsætte.") },
                confirmButton = {
                    TextButton(onClick = { viewModel.resendVerification() }) {
                        Text("Send igen")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { viewModel.dismissVerificationDialog() }) {
                        Text("Senere")
                    }
                }
            )
        }
    }
}

@Composable
private fun PrivateDashboard(
    padding: PaddingValues,
    requests: List<Request>,
    navController: NavController,
    isEmailVerified: Boolean,
    resendVerification: () -> Unit
) {
    if (requests.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    "Du har ikke oprettet nogle opgaver endnu",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(8.dp))
                Button(onClick = { navController.navigate("new_task_wizard") }) {
                    Text("Opret ny opgave")
                }
                if (!isEmailVerified) {
                    Spacer(Modifier.height(16.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(0.8f),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                "Bekræft din e-mail",
                                color = MaterialTheme.colorScheme.error,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(Modifier.height(8.dp))
                            Button(onClick = resendVerification, modifier = Modifier.fillMaxWidth()) {
                                Icon(Icons.Default.Email, contentDescription = null)
                                Spacer(Modifier.width(8.dp))
                                Text("Send bekræftelsesmail igen")
                            }
                        }
                    }
                }
            }
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text("Dine opgaver", fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
            }

            items(requests.size) { index ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    ListItem(
                        headlineContent = { Text("Nyt køkken i Valby") },
                        supportingContent = { Text("Sendt d. 19. nov • Afventer tilbud") },
                        trailingContent = {
                            Button(onClick = { navController.navigate("task_detail") }, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary)) {
                                Text("Åben", color = MaterialTheme.colorScheme.onTertiary)
                            }
                        }
                    )
                }
            }
            Timber.d("PrivateDashboard: Viser ${requests.size} requests")
        }
    }
}

@Composable
private fun ContractorDashboard(padding: PaddingValues, navController: NavController) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Icon(Icons.Default.Build, contentDescription = null, modifier = Modifier.size(48.dp))
                    Spacer(Modifier.height(16.dp))
                    Text("Du er klar til at byde!", fontSize = 22.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(8.dp))
                    Text("Se nye opgaver fra kunder i dit område")
                }
            }
        }

        item {
            Text("Nye opgaver i nærheden", fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
        }

        items(5) { index ->
            Card(modifier = Modifier.fillMaxWidth()) {
                ListItem(
                    headlineContent = { Text("Murerarbejde i København") },
                    supportingContent = { Text("50 m² • Badeværelse • Estimeret pris: 85.000 kr") },
                    trailingContent = {
                        Button(onClick = { navController.navigate("bid_detail") }) {
                            Text("Byd")
                        }
                    }
                )
            }
        }
    }
}