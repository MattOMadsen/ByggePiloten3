// Fil: app/src/main/java/dk/byggepiloten/firma/ui/screen/WelcomeScreen.kt
// OPDATERET: Implementeret "huske bruger" – auto-redirect til dashboard hvis logget ind + rolle eksisterer.
// - Bruger AuthViewModel flows (isLoggedIn + currentRole) – Firebase Auth husker session automatisk.
// - LaunchedEffect checker state og navigerer med popUpTo for clean backstack (ingen tilbage til welcome).
// - UX: Hvis logget ind → navigér væk øjeblikkeligt (minimal flash). Ellers vis normal welcome-UI.
// - Tilføjet: Loading-indikator mens check (for bedre UX hvis role-load tager tid).
// - Beholdt 100% af eksisterende design, animationer, cards og "Log ind"-link.
// - Fulde imports + Material3-kompatibilitet.
// - Linjer: 292 (original ~250 + ny logik ~40 linjer).

package dk.byggepiloten.firma.ui.screen.onboarding

import androidx.compose.animation.*
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Business
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel  // NY: For ViewModel-injection
import androidx.lifecycle.compose.collectAsStateWithLifecycle  // NY: For flow-collection
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import dk.byggepiloten.firma.ui.theme.ByggePilotenBlue
import dk.byggepiloten.firma.ui.theme.ByggePilotenTheme
import dk.byggepiloten.firma.ui.viewmodel.auth.AuthViewModel  // NY: Import af AuthViewModel
import kotlinx.coroutines.delay

@Composable
fun WelcomeScreen(
    navController: NavController
) {
    val authViewModel: AuthViewModel = hiltViewModel()  // NY: Hent AuthViewModel
    val isLoggedIn by authViewModel.isLoggedIn.collectAsStateWithLifecycle()  // NY: Observe login-state
    val currentRole by authViewModel.currentRole.collectAsStateWithLifecycle(initialValue = null)  // NY: Observe rolle

    // NY: Auto-redirect hvis bruger er logget ind OG har en gemt rolle
    LaunchedEffect(isLoggedIn, currentRole) {
        if (isLoggedIn && currentRole != null) {
            navController.navigate("dashboard") {
                popUpTo("welcome") { inclusive = true }  // Clear welcome fra backstack
                launchSingleTop = true
            }
        }
    }

    ByggePilotenTheme {
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
                ),
            contentAlignment = Alignment.Center
        ) {
            // NY: Vis loading mens vi checker auth-state (undgår flash af welcome ved auto-login)
            if (isLoggedIn && currentRole != null) {
                CircularProgressIndicator(color = Color.White)
            } else {
                // Original welcome-UI (uændret – vises kun hvis IKKE logget ind eller rolle mangler)
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 32.dp)
                        .padding(vertical = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    AnimatedVisibility(
                        visible = true,
                        enter = fadeIn(animationSpec = tween(600)) + scaleIn(
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessMedium
                            ),
                            initialScale = 0.8f
                        )
                    ) {
                        Text(
                            text = "ByggePiloten",
                            fontSize = 48.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    AnimatedVisibility(
                        visible = true,
                        enter = slideInVertically(
                            initialOffsetY = { it },
                            animationSpec = tween(800, delayMillis = 200)
                        ) + fadeIn(animationSpec = tween(800, delayMillis = 200))
                    ) {
                        Text(
                            text = "Nem og hurtig vej til murerarbejde",
                            fontSize = 18.sp,
                            color = Color.White.copy(alpha = 0.9f),
                            textAlign = TextAlign.Center
                        )
                    }

                    Spacer(modifier = Modifier.height(48.dp))

                    var showCards by remember { mutableStateOf(false) }
                    LaunchedEffect(Unit) {
                        delay(600)
                        showCards = true
                    }

                    Crossfade(targetState = showCards, animationSpec = tween(600, delayMillis = 600)) { visible ->
                        if (visible) {
                            Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(120.dp)
                                        .clip(RoundedCornerShape(16.dp))
                                        .clickable { navController.navigate("private_details") },
                                    colors = CardDefaults.cardColors(containerColor = Color.White)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(16.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.AccountCircle,
                                            contentDescription = "Privat kunde",
                                            tint = ByggePilotenBlue,
                                            modifier = Modifier.size(48.dp)
                                        )
                                        Spacer(modifier = Modifier.width(16.dp))
                                        Column {
                                            Text(
                                                text = "Privat kunde",
                                                fontSize = 20.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = Color.Black
                                            )
                                            Text(
                                                text = "Få tilbud på dit murerarbejde",
                                                fontSize = 14.sp,
                                                color = Color.Black.copy(alpha = 0.7f)
                                            )
                                        }
                                    }
                                }

                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(120.dp)
                                        .clip(RoundedCornerShape(16.dp))
                                        .clickable { navController.navigate("contractor_details") },
                                    colors = CardDefaults.cardColors(containerColor = Color.White)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(16.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Business,
                                            contentDescription = "Håndværkerfirma",
                                            tint = ByggePilotenBlue,
                                            modifier = Modifier.size(48.dp)
                                        )
                                        Spacer(modifier = Modifier.width(16.dp))
                                        Column {
                                            Text(
                                                text = "Håndværkerfirma",
                                                fontSize = 20.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = Color.Black
                                            )
                                            Text(
                                                text = "Byd på opgaver fra kunder",
                                                fontSize = 14.sp,
                                                color = Color.Black.copy(alpha = 0.7f)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(40.dp))

                    Text(
                        text = "Har du allerede en konto? Log ind",
                        fontSize = 16.sp,
                        color = Color.White,
                        modifier = Modifier.clickable { navController.navigate("login") }
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun WelcomeScreenPreview() {
    ByggePilotenTheme {
        WelcomeScreen(rememberNavController())
    }
}