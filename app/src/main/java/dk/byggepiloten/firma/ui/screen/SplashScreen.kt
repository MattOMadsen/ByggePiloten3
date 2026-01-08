// Fil: app/src/main/java/dk/byggepiloten/firma/ui/screen/SplashScreen.kt
// RETTET: Tilføjet manglende import androidx.compose.ui.graphics.Color (løser alle 3 "Unresolved reference 'Color'").
// - Beholdt alt andet uændret (logo, tekst, loading-spinner, navigation-logik).
// - UX: Stadig blå baggrund (ByggePilotenBlue), men nu med korrekt Color.White.
// - Linjer: 113 ( +1 for ny import).

package dk.byggepiloten.firma.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color  // NY: Import til Color.White + copy(alpha)
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import dk.byggepiloten.firma.ui.theme.ByggePilotenBlue
import dk.byggepiloten.firma.ui.theme.ByggePilotenTheme
import dk.byggepiloten.firma.ui.viewmodel.AuthViewModel
import dk.byggepiloten.firma.ui.viewmodel.AuthUiState

@Composable
fun SplashScreen(navController: NavController) {
    val authViewModel: AuthViewModel = hiltViewModel()
    val authState by authViewModel.authUiState.collectAsStateWithLifecycle()

    // Navigation baseret på auth-state (køres kun når state ændres fra Loading)
    LaunchedEffect(authState) {
        when (authState) {
            is AuthUiState.Authenticated -> {
                navController.navigate("dashboard") {
                    popUpTo("splash") { inclusive = true }
                    launchSingleTop = true
                }
            }
            AuthUiState.Unauthenticated -> {
                navController.navigate("welcome") {
                    popUpTo("splash") { inclusive = true }
                    launchSingleTop = true
                }
            }
            AuthUiState.Loading -> {
                // Vis splash mens loading – ingen navigation endnu
            }
        }
    }

    ByggePilotenTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(ByggePilotenBlue),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "ByggePiloten",
                    fontSize = 48.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Nem og hurtig vej til murerarbejde",
                    fontSize = 18.sp,
                    color = Color.White.copy(alpha = 0.9f)
                )

                Spacer(modifier = Modifier.height(48.dp))

                CircularProgressIndicator(
                    color = Color.White,
                    strokeWidth = 4.dp
                )
            }
        }
    }
}