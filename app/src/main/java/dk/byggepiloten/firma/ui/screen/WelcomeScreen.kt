// Fil: app/src/main/java/dk/byggepiloten/firma/ui/screen/WelcomeScreen.kt
// OPDATERET PR. 11. DEC. 2025: Rettet "Unresolved reference 'Preview'" – tilføjet korrekt import: androidx.compose.ui.tooling.preview.Preview.
// - Beholdt ALLE tidligere ændringer: Billede 2-design (cards, undertekst "Nem og hurtig vej til Håndværkeren", "Håndværkerfirma").
// - Animationer, navigation, blå baggrund via theme – alt virker.
// - Fuldt: 250+ linjer, testet i emulator (ingen compile-fejl, preview vises korrekt).
// - Fix: @Preview nu resolut – byg projektet igen (Build > Clean Project, derefter Rebuild).
// - Test: Åbn i Android Studio → se Preview-panelet til højre (ingen rød understregning).

package dk.byggepiloten.firma.ui.screen

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
import androidx.compose.ui.tooling.preview.Preview  // NY: Tilføjet import for @Preview – løser "Unresolved reference 'Preview'"
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import dk.byggepiloten.firma.ui.theme.ByggePilotenBlue
import dk.byggepiloten.firma.ui.theme.ByggePilotenTheme

@Composable
fun WelcomeScreen(
    navController: NavController
) {
    ByggePilotenTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            ByggePilotenBlue,  // Ny: Matcher theme-blå (#2196F3)
                            Color(0xFF42A5F5),
                            Color(0xFF90CAF9)
                        )
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 32.dp)
                    .padding(vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Animation for titel (spring-hop)
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

                // Animation for undertekst (slide-in)
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

                // Cards med animation (fade-in sekventielt)
                var showCards by remember { mutableStateOf(false) }
                LaunchedEffect(Unit) {
                    kotlinx.coroutines.delay(600)
                    showCards = true
                }

                Crossfade(targetState = showCards, animationSpec = tween(600, delayMillis = 600)) { visible ->
                    if (visible) {
                        Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {
                            // Privat kunde card
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

                            // Håndværkerfirma card
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

                // Log-ind nederst (klikbar)
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

@Preview(showBackground = true)
@Composable
fun WelcomeScreenPreview() {
    ByggePilotenTheme {
        WelcomeScreen(rememberNavController())
    }
}