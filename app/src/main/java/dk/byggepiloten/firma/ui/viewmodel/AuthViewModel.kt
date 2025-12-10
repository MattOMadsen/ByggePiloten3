package dk.byggepiloten.firma.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import dagger.hilt.android.lifecycle.HiltViewModel
import dk.byggepiloten.firma.data.repository.AuthManager
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authManager: AuthManager
) : ViewModel() {

    val isLoggedIn = authManager.currentUid
        .map { it != null }
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    val currentRole = authManager.currentRole
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    fun checkAndNavigate(navController: NavController) {
        viewModelScope.launch {
            if (authManager.isLoggedIn()) {
                navController.navigate("dashboard") {
                    popUpTo("onboarding") { inclusive = true }
                }
            }
        }
    }

    fun logout(navController: NavController) {
        viewModelScope.launch {
            authManager.signOut()
            navController.navigate("onboarding") { popUpTo(0) { inclusive = true } }
        }
    }
}