// File: app/src/main/java/dk/byggepiloten/firma/ui/screen/ConfirmScreen.kt
package dk.byggepiloten.firma.ui.screen.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import dk.byggepiloten.firma.ui.viewmodel.onboarding.OnboardingViewModel
import kotlinx.coroutines.delay
import timber.log.Timber

@Composable
fun ConfirmScreen(
    navController: NavController,
    token: String
) {
    val viewModel: OnboardingViewModel = hiltViewModel()

    LaunchedEffect(token) {
        if (token.isNotBlank()) {
            Timber.d("E-mail bekræftet – token modtaget")
            viewModel.completeOnboarding()
        }
    }

    LaunchedEffect(Unit) {
        delay(2500)
        navController.navigate("dashboard") {
            popUpTo(0) { inclusive = true }
            launchSingleTop = true
        }
    }

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(40.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator(strokeWidth = 6.dp)
            Spacer(Modifier.height(32.dp))
            Text(
                text = "Bekræfter din e-mail...",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(16.dp))
            Text(
                text = "Vent venligst – du bliver sendt videre om et øjeblik",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(40.dp))
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth(0.6f))
        }
    }
}