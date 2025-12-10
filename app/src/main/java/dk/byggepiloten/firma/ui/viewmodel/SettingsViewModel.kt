// File: app/src/main/java/dk/byggepiloten/firma/ui/viewmodel/SettingsViewModel.kt
// FULD, KOMPLET VERSION – rettet kald til saveProfile med gdprAccepted/isDarkMode (matcher udvidet impl).

package dk.byggepiloten.firma.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dk.byggepiloten.firma.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

data class SettingsState(
    val name: String = "Dummy Navn",
    val email: String = "dummy@email.dk",
    val phone: String = "",
    val gdprAccepted: Boolean = false,
    val isDarkMode: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null
) {
    val isValid: Boolean
        get() = name.isNotBlank() && email.isNotBlank() && gdprAccepted
}

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _state = MutableStateFlow(SettingsState())
    val state: StateFlow<SettingsState> = _state.asStateFlow()

    init {
        loadProfile()
    }

    private fun loadProfile() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            try {
                val profile = userRepository.getCurrentProfile()
                _state.value = SettingsState(
                    name = profile?.name ?: "Dummy Navn",
                    email = profile?.email ?: "",
                    phone = profile?.phone ?: "",
                    gdprAccepted = profile?.gdprAccepted ?: false,
                    isDarkMode = profile?.darkMode ?: false
                )
                Timber.d("Settings: Profil loader: ${profile?.name}")
            } catch (e: Exception) {
                Timber.e(e, "Load profil fejl")
                _state.value = _state.value.copy(error = "Hentning mislykkedes")
            } finally {
                _state.value = _state.value.copy(isLoading = false)
            }
        }
    }

    fun updateName(name: String) {
        _state.value = _state.value.copy(name = name)
    }

    fun updateEmail(email: String) {
        _state.value = _state.value.copy(email = email)
    }

    fun updatePhone(phone: String) {
        _state.value = _state.value.copy(phone = phone)
    }

    fun updateGdprAccepted(accepted: Boolean) {
        _state.value = _state.value.copy(gdprAccepted = accepted)
    }

    fun updateDarkMode(enabled: Boolean) {
        _state.value = _state.value.copy(isDarkMode = enabled)
        viewModelScope.launch {
            userRepository.saveDarkModePreference(enabled)
        }
    }

    // RETTET: Kall med udvidede params – løser no param found (gdprAccepted, isDarkMode)
    // Trin: 1. Valider GDPR/isValid. 2. Kall repo med alle felter. 3. Håndter try-catch for error-state. 4. onComplete for UI-feedback.
    fun saveProfile(onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            if (!state.value.gdprAccepted) {
                _state.value = _state.value.copy(error = "Accepter GDPR først")
                onComplete(false)
                return@launch
            }
            if (!state.value.isValid) {
                _state.value = _state.value.copy(error = "Udfyld navn og email")
                onComplete(false)
                return@launch
            }
            _state.value = _state.value.copy(isLoading = true, error = null)
            try {
                userRepository.saveProfile(
                    name = state.value.name,
                    email = state.value.email,
                    phone = state.value.phone,
                    gdprAccepted = state.value.gdprAccepted,  // Tilføjet – matcher impl
                    isDarkMode = state.value.isDarkMode  // Tilføjet – matcher impl
                )
                _state.value = _state.value.copy(isLoading = false)
                onComplete(true)
                Timber.d("Settings: Profil gemt: ${state.value.name}")
            } catch (e: Exception) {
                Timber.e(e, "Save profil fejl")
                _state.value = _state.value.copy(isLoading = false, error = "Gemning mislykkedes")
                onComplete(false)
            }
        }
    }

    fun logout(onLogout: () -> Unit) {
        viewModelScope.launch {
            try {
                userRepository.clearUser()
                Timber.d("Settings: Logout succes")
                onLogout()
            } catch (e: Exception) {
                Timber.e(e, "Logout fejl")
                _state.value = _state.value.copy(error = "Logout mislykkedes")
            }
        }
    }
}