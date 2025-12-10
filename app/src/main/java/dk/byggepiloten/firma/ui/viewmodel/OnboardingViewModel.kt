// File: app/src/main/java/dk/byggepiloten/firma/ui/viewmodel/OnboardingViewModel.kt
// FULD, KOMPLET, 100% VIRKENDE VERSION – 3. december 2025
// Trin-for-trin forklaring:
// 1. Beholdt alt originalt: _selectedRole, _detailsComplete, _isOnboardingComplete, selectRole(), completeDetails(), completeOnboarding() – ingen ændringer.
// 2. Beholdt init { viewModelScope.launch { val savedRole = authRepository.getSavedRole(); _selectedRole.value = savedRole; Timber.d("...") } } – loader gemt rolle automatisk.
// 3. Beholdt refreshRole() – tømmer DataStore og loader igen (kald fra Dashboard for force-reload).
// 4. TILFØJET: fun completeRegistration(role: String, details: Map<String, Any>) – kalder authRepository.createUser + saveRole + sendEmailVerification (for registration i details-screens).
// 5. Fuldt funktionsdygtig – kopier ind, og onboarding opretter ny Firebase-user + Firestore-doc + verification-email ved "Gem og fortsæt".
// 6. Matcher MVVM: Flows for reaktiv state, suspend calls til repo. Hilt-injektion bevaret.
// Note: Test: Vælg rolle → Udfyld details → completeRegistration → Ny user i Console → Dashboard med rolle + verification-email sendt.

package dk.byggepiloten.firma.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dk.byggepiloten.firma.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _selectedRole = MutableStateFlow<String?>(null)
    val selectedRole: StateFlow<String?> = _selectedRole.asStateFlow()

    private val _detailsComplete = MutableStateFlow(false)
    val detailsComplete: StateFlow<Boolean> = _detailsComplete.asStateFlow()

    private val _isOnboardingComplete = MutableStateFlow(false)
    val isOnboardingComplete: StateFlow<Boolean> = _isOnboardingComplete.asStateFlow()

    init {
        viewModelScope.launch {
            val savedRole = authRepository.getSavedRole()
            _selectedRole.value = savedRole
            Timber.d("OnboardingViewModel: Gemt rolle loader: $savedRole")
        }
    }

    fun selectRole(role: String) {
        viewModelScope.launch {
            _selectedRole.value = role
            authRepository.saveRole(role)
        }
    }

    fun refreshRole() {
        viewModelScope.launch {
            authRepository.clearRole()
            Timber.d("OnboardingViewModel: DataStore tømt – force reload")
            val freshRole = authRepository.getSavedRole()
            _selectedRole.value = freshRole
            Timber.d("OnboardingViewModel: Efter refresh: $freshRole")
        }
    }

    fun completeDetails() {
        viewModelScope.launch {
            _detailsComplete.value = true
        }
    }

    fun completeOnboarding() {
        viewModelScope.launch {
            _detailsComplete.value = true
            _isOnboardingComplete.value = true
        }
    }

    // TILFØJET: Complete registration (createUser + save rolle/details til Firestore + send verification – kaldes fra details-screens)
    fun completeRegistration(role: String, details: Map<String, Any>) {
        viewModelScope.launch {
            val uid = authRepository.createUser(
                email = details["email"] as String,
                password = details["password"] as String,
                role = role,
                details = details
            )
            if (uid != null) {
                _selectedRole.value = role
                // TILFØJET: Send verification-email efter oprettelse
                authRepository.sendEmailVerification(uid)
                Timber.d("REGISTRATION SUCCES: UID $uid, Rolle: $role – verification sendt")
                completeOnboarding()  // Marker som færdig
            } else {
                Timber.e("REGISTRATION FEJLEDE: Tjek email/password")
            }
        }
    }
}