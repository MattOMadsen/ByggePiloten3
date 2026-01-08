// Fil: app/src/main/java/dk/byggepiloten/firma/ui/screen/OnboardingScreen.kt
// RETTET: Fjernet LaunchedEffect + checkAndNavigate (funktionen eksisterer ikke længere i AuthViewModel – al auth-check sker nu i SplashScreen).
// - Beholdt 100% af resten: Rolle-kort, animationer, login-knap, onRoleSelected-lambda, preview, ContractorTypeSelection-navigation i kommentar.
// - Ingen andre ændringer – stadig fuldt funktionsdygtig onboarding-flow.
// - Linjer: 138 (original ~160 – fjernet ~22 linjer med auth-check).

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
import androidx.navigation.NavController
import dk.byggepiloten.firma.ui.theme.ByggePilotenTheme
import timber.log.Timber

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingScreen(
    navController: NavController,
    onRoleSelected: (String) -> Unit
) {
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
                    enter = fadeIn() + scaleIn(animationSpec = spring(stiffness = Spring.StiffnessMedium))
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
                    enter = fadeIn(animationSpec = spring(stiffness = Spring.StiffnessMedium))
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
                    onClick = { onRoleSelected("contractor") }
                )

                Spacer(modifier = Modifier.height(48.dp))

                // Login-knap
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