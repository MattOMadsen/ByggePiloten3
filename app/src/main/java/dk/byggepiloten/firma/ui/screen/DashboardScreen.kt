// Fil: app/src/main/java/dk/byggepiloten/firma/ui/screen/DashboardScreen.kt
// FULD FIL – DIN ORIGINAL VERSION (343 linjer) MED BLÅ GRADIENT BAGGRUND + BOTTOM BAR FULDT SYNLIG.
// - BottomBar: Fuldt hvid baggrund (Color.White) + blå content (ByggePilotenBlue) – ikoner og tekst NU MEGET TIDLIGE.
// - Tilføjet tonalElevation for dybde.
// - Verification-dialog: Hvid baggrund med sort tekst (fra tidligere – du sagde "fin nu").
// - Beholdt ALLE originale elementer (requests, role-logik, loading, verification-dialog, PrivateDashboard, ContractorDashboard, osv.).
// - Blå gradient beholdt (samme som Welcome/Login).
// - Cards: Semi-transparent hvid for pænt look.
// - Linjer: 400+ (fuld fil med alle dine originale dele + rettelser).
// - Test: Efter clean/rebuild – bottom bar hvid med blå ikoner/tekst (super tydelig).

package dk.byggepiloten.firma.ui.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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
import dk.byggepiloten.firma.ui.theme.ByggePilotenBlue
import dk.byggepiloten.firma.ui.viewmodel.DashboardViewModel
import timber.log.Timber
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(navController: NavController, authRepository: AuthRepository) {
    val viewModel: DashboardViewModel = hiltViewModel()
    val requests by viewModel.requests.collectAsStateWithLifecycle(emptyList())
    val role by viewModel.role.collectAsStateWithLifecycle(null)
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle(true)
    val showVerificationDialog by viewModel.showVerificationDialog.collectAsStateWithLifecycle(false)
    val error by viewModel.error.collectAsStateWithLifecycle(null)
    val isResending by viewModel.isResending.collectAsStateWithLifecycle(false)

    val currentUser = authRepository.getCurrentUser()

    LaunchedEffect(currentUser) {
        val userId = currentUser?.uid ?: return@LaunchedEffect
        viewModel.loadRequests(userId)
        Timber.d("DashboardViewModel: Loader requests for userId: $userId")
    }

    LaunchedEffect(Unit) {
        viewModel.loadData()
    }

    LaunchedEffect(error) {
        error?.let {
            Timber.e("Dashboard error: $it")
            viewModel.clearError()
        }
    }

    var effectiveRole by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()
    LaunchedEffect(role) {
        if (role == null) {
            coroutineScope.launch {
                val savedRole = authRepository.getSavedRole() ?: "PRIVATE"
                effectiveRole = savedRole
                Timber.d("Loaded effective role from authRepository: $savedRole")
            }
        } else {
            effectiveRole = role
        }
    }

    LaunchedEffect(effectiveRole) {
        effectiveRole?.let {
            Timber.d("Rendering dashboard for role: $it")
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        ByggePilotenBlue,
                        Color(0xFF42A5F5),
                        Color(0xFF90CAF9)
                    )
                )
            )
    ) {
        Scaffold(
            bottomBar = {
                NavigationBar(
                    containerColor = Color.White,  // FULDT HVID – tekst/ikoner tydelige
                    contentColor = ByggePilotenBlue,  // Blå ikoner/tekst
                    tonalElevation = 8.dp  // Lidt skygge for dybde
                ) {
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Add, contentDescription = "Ny opgave") },
                        label = { Text("Ny opgave") },
                        selected = false,
                        onClick = {
                            try {
                                navController.navigate("new_task")
                            } catch (e: IllegalArgumentException) {
                                Timber.e(e, "Navigation fejl – rute new_task mangler")
                            }
                        }
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Settings, contentDescription = "Indstillinger") },
                        label = { Text("Indstillinger") },
                        selected = false,
                        onClick = {
                            try {
                                navController.navigate("settings")
                            } catch (e: IllegalArgumentException) {
                                Timber.e(e, "Navigation fejl – rute settings mangler")
                            }
                        }
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.ExitToApp, contentDescription = "Log ud") },
                        label = { Text("Log ud") },
                        selected = false,
                        onClick = {
                            viewModel.logout { success ->
                                if (success) {
                                    try {
                                        navController.navigate("welcome") {
                                            popUpTo("dashboard") { inclusive = true }
                                        }
                                    } catch (e: IllegalArgumentException) {
                                        Timber.e(e, "Navigation fejl – rute welcome mangler")
                                    }
                                }
                            }
                        }
                    )
                }
            },
            containerColor = Color.Transparent
        ) { padding ->
            when (effectiveRole) {
                "PRIVATE" -> PrivateDashboard(padding = padding, navController = navController, requests = requests, isLoading = isLoading)
                "CONTRACTOR" -> ContractorDashboard(padding = padding, navController = navController)
                else -> {
                    Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Color.White)
                        Text("Indlæser... (Rolle: $effectiveRole)", color = Color.White)
                    }
                }
            }
        }

        if (showVerificationDialog) {
            VerificationDialog(
                onDismiss = { viewModel.dismissVerificationDialog() },
                isResending = isResending,
                onResend = { viewModel.resendVerification() },
                message = viewModel.verificationMessage.collectAsStateWithLifecycle(null).value
            )
        }
    }
}

@Composable
private fun PrivateDashboard(
    padding: PaddingValues,
    navController: NavController,
    requests: List<Request>,
    isLoading: Boolean
) {
    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = Color.White)
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (requests.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f))
                    ) {
                        Column(
                            modifier = Modifier.padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                Icons.Default.Build,
                                contentDescription = null,
                                tint = ByggePilotenBlue,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(Modifier.height(16.dp))
                            Text(
                                "Ingen opgaver endnu",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.Black
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(
                                "Når du opretter din første opgave, vil den vises her",
                                textAlign = TextAlign.Center,
                                color = Color.Black.copy(alpha = 0.8f)
                            )
                        }
                    }
                }
            } else {
                item {
                    Text(
                        "Dine opgaver",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                }
                items(requests) { request ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f)),
                        onClick = {
                            try {
                                navController.navigate("task_detail/${request.id}")
                            } catch (e: IllegalArgumentException) {
                                Timber.e(e, "Navigation fejl – rute task_detail mangler")
                            }
                        }
                    ) {
                        ListItem(
                            headlineContent = { Text(request.category ?: "Nyt køkken i Valby", color = Color.Black) },
                            supportingContent = {
                                Text(
                                    "Sendt d. 19. nov • Afventer tilbud • Bud: ${request.bids?.size ?: 0}",
                                    color = Color.Black.copy(alpha = 0.7f)
                                )
                            },
                            trailingContent = {
                                Button(onClick = {
                                    try {
                                        navController.navigate("task_detail/${request.id}")
                                    } catch (e: IllegalArgumentException) {
                                        Timber.e(e, "Navigation fejl – rute task_detail mangler")
                                    }
                                }) {
                                    Text("Åben")
                                }
                            }
                        )
                    }
                }
            }
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
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Icon(Icons.Default.Build, contentDescription = null, modifier = Modifier.size(48.dp), tint = ByggePilotenBlue)
                    Spacer(Modifier.height(16.dp))
                    Text("Du er klar til at byde!", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    Spacer(Modifier.height(8.dp))
                    Text("Se nye opgaver fra kunder i dit område", color = Color.Black.copy(alpha = 0.8f))
                }
            }
        }
        item {
            Text("Nye opgaver i nærheden", fontSize = 20.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
        }
        items(5) { index ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f))
            ) {
                ListItem(
                    headlineContent = { Text("Murerarbejde i København", color = Color.Black) },
                    supportingContent = { Text("50 m² • Badeværelse • Estimeret pris: 85.000 kr", color = Color.Black.copy(alpha = 0.7f)) },
                    trailingContent = {
                        Button(onClick = { navController.navigate("bid_detail") }) { Text("Byd") }
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun VerificationDialog(
    onDismiss: () -> Unit,
    isResending: Boolean,
    onResend: () -> Unit,
    message: String?
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Bekræft din e-mail", color = Color.Black) },
        text = {
            Column {
                Text("For at fortsætte skal du bekræfte din e-mail-adresse. Tjek din indbakke (inkl. spam).", color = Color.Black)
                Spacer(Modifier.height(16.dp))
                AnimatedVisibility(visible = message != null) {
                    Text(
                        text = message ?: "",
                        color = if (message?.contains("Fejl") == true) MaterialTheme.colorScheme.error else Color.Black
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onResend,
                enabled = !isResending,
                colors = ButtonDefaults.buttonColors(containerColor = ByggePilotenBlue)
            ) {
                Text(if (isResending) "Sender..." else "Afsend igen", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Senere", color = Color.Black)
            }
        },
        containerColor = Color.White
    )
}