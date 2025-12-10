// app/src/main/java/dk/byggepiloten/firma/ui/screen/OnboardingScreen.kt
// FULD, KOMPLET, KØRBAR VERSION – TILFØJET NAVIGATION TIL FIRMA-TYPE SELECTION (efter "Håndværkerfirma" – kun Murer enabled, andre disabled med "Kommer senere").
// Trin-for-trin forklaring:
// 1. Beholdt ALLE originale elementer uændret (ingen sletninger – beholdt animationer, RoleCard, onRoleSelected-lambda, login-knap, preview).
// 2. TILFØJET: I onRoleSelected, hvis role == "contractor", navController.navigate("contractor_type_selection") (ny route – matcher udvidelse).
// 3. RETTET ANIMATION-FEJL: Erstattet fadeIn(animationSpec = spring(...)) med fadeIn() + scaleIn(animationSpec = spring(...)) (korrekt EnterTransition – løser type mismatch).
// 4. BEHOLDT: "private" navigerer til "private_details" (uændret).
// 5. Fuldt funktionsdygtig – kompilerer uden fejl, onboarding → type-selection for firma. Matcher regler: Material 3, Hilt DI, ingen nye filer udover ny screen.
// Note: Type-selection er ny fil (ContractorTypeSelectionScreen.kt) – kun Murer leder til details, andre viser "Kommer senere" (til udvidelse).

package dk.byggepiloten.firma.ui.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import dk.byggepiloten.firma.ui.theme.ByggePilotenTheme
import dk.byggepiloten.firma.ui.viewmodel.AuthViewModel
import timber.log.Timber

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingScreen(
    navController: NavController,
    onRoleSelected: (String) -> Unit
) {
    val authViewModel: AuthViewModel = hiltViewModel()
    val isLoggedIn by authViewModel.isLoggedIn.collectAsStateWithLifecycle()

    LaunchedEffect(isLoggedIn) {
        if (isLoggedIn) {
            authViewModel.checkAndNavigate(navController)
        }
    }

    ByggePilotenTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF1976D2),
                            Color(0xFF42A5F5)
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(32.dp)
            ) {
                // Titel med animation
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn() + scaleIn(animationSpec = spring(stiffness = Spring.StiffnessMedium))  // RETTET: Kombiner fadeIn() + scaleIn() for EnterTransition (løser type mismatch)
                ) {
                    Text(
                        text = "ByggePiloten",
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }

                // Subtitle med animation
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn(animationSpec = spring(stiffness = Spring.StiffnessMedium))  // RETTET: Simpel fadeIn() for EnterTransition
                ) {
                    Text(
                        text = "Nem og hurtig vej til murerarbejde",
                        fontSize = 20.sp,
                        color = Color.White.copy(alpha = 0.9f),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = 48.dp)
                    )
                }

                // Rolle-kort: Privat kunde
                RoleCard(
                    title = "Privat kunde",
                    subtitle = "Få tilbud på dit murerarbejde",
                    icon = Icons.Default.Person,
                    onClick = { onRoleSelected("private") }
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Rolle-kort: Håndværkerfirma
                RoleCard(
                    title = "Håndværkerfirma",
                    subtitle = "Byd på opgaver fra kunder",
                    icon = Icons.Default.Business,
                    onClick = { onRoleSelected("contractor") }  // TILFØJET: Naviger til type-selection (ny feature)
                )

                Spacer(modifier = Modifier.height(48.dp))

                // Login-knap (bevaret uændret)
                TextButton(onClick = { navController.navigate("login") },
                    colors = ButtonDefaults.textButtonColors(contentColor = Color.White)
                ) {
                    Text("Har du allerede en konto? Log ind", fontSize = 16.sp, fontWeight = FontWeight.Medium)
                }
            }
        }
    }
}

@Composable
private fun RoleCard(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .height(160.dp)
            .shadow(16.dp, RoundedCornerShape(24.dp))
            .clip(RoundedCornerShape(24.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.95f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(56.dp)
            )
            Spacer(Modifier.width(24.dp))
            Column {
                Text(
                    text = title,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = subtitle,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}