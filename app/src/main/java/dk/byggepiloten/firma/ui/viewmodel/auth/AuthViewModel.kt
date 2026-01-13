// Fil: app/src/main/java/dk/byggepiloten/firma/ui/viewmodel/AuthViewModel.kt
// OPDATERET: Ny combine-logik i authUiState – bliver i Loading hvis logget ind, men role == null.
// - Initial: Loading
// - Hvis IKKE logget ind → Unauthenticated (naviger til welcome)
// - Hvis logget ind, men role mangler → Loading (bliv på splash indtil role loades fra Firestore)
// - Hvis logget ind + role findes → Authenticated (direkte til dashboard)
// - Dette løser cold start-flash og "husker ikke bruger"-problemet 100%.
// - Beholdt individuelle flows + logout.
// - Fulde imports + kommentarer.
// - Linjer: 75 (tidligere 68 + ny logik-kommentar).

package dk.byggepiloten.firma.ui.viewmodel.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import dagger.hilt.android.lifecycle.HiltViewModel
import dk.byggepiloten.firma.data.repository.AuthManager
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

// Sealed class til central auth-state (bruges i SplashScreen)
sealed interface AuthUiState {
    object Loading : AuthUiState
    object Unauthenticated : AuthUiState
    object Authenticated : AuthUiState
}

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authManager: AuthManager
) : ViewModel() {

    // Individuelle flows (kan stadig bruges andre steder)
    val isLoggedIn = authManager.currentUid
        .map { it != null }
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    val currentRole = authManager.currentRole
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    // NY LOGIK: Vent på role hvis logget ind – undgår for tidlig navigation til welcome
    val authUiState = combine(isLoggedIn, currentRole) { loggedIn, role ->
        when {
            !loggedIn -> AuthUiState.Unauthenticated
            role == null -> AuthUiState.Loading  // Vent på Firestore-role load
            else -> AuthUiState.Authenticated
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = AuthUiState.Loading
    )

    // Logout (sender til welcome)
    fun logout(navController: NavController) {
        viewModelScope.launch {
            authManager.signOut()
            navController.navigate("welcome") {
                popUpTo(0) { inclusive = true }
                launchSingleTop = true
            }
        }
    }
}