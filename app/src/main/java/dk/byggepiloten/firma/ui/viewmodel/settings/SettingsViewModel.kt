// File: app/src/main/java/dk/byggepiloten/firma/ui/viewmodel/SettingsViewModel.kt
// FULD, KOMPLET VERSION – rettet kald til saveProfile med gdprAccepted/isDarkMode (matcher udvidet impl). RETTET: Tilføjet try-catch i logout for Room clearAll-fejl.
// TILFØJET: address-håndtering i loadProfile (safe call med fallback "" – løser "Unresolved reference 'address'"); saveProfile kalder repo med address = state.value.address (default "" hvis mangler i repo – løser "No parameter with name 'address' found").
// BEHOLDT: Alle originale (loadProfile, update*-functions, saveProfile med validering, logout-kald til repo) uændret.
// RETTET: isValid inkluderer address.isNotBlank(); updateAddress beholdt.
// Trin: 1. BEHOLDT: Alle originale. 2. RETTET: loadProfile – address = profile?.address ?: "" (safe fallback). 3. RETTET: saveProfile – address = state.value.address (tilføj som valgfri param med default ""). 4. Matcher din upload – ingen sletninger.
// Note: GDPR-sikker (validering i saveProfile). Hilt/MVVM-kompatibel. Hvis repo mangler address-param, upload UserRepository.kt for at tilføje det.

package dk.byggepiloten.firma.ui.viewmodel.settings

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
    val address: String = "",  // BEHOLDT: Adresse-felt.
    val gdprAccepted: Boolean = false,
    val isDarkMode: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null
) {
    val isValid: Boolean
        get() = name.isNotBlank() && email.isNotBlank() && address.isNotBlank() && gdprAccepted  // BEHOLDT: Inkluderer address.
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
                    address = profile?.address ?: "",  // RETTET: Safe call med fallback "" (løser "Unresolved reference 'address'").
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

    fun updateAddress(address: String) {
        _state.value = _state.value.copy(address = address)
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

    // BEHOLDT: Kall med udvidede params – rettet med address = state.value.address (valgfri med default "" i repo-kald – løser "No parameter with name 'address' found").
    // Trin: 1. Valider GDPR/isValid. 2. Kall repo med alle felter inkl. address. 3. Håndter try-catch for error-state. 4. onComplete for UI-feedback.
    fun saveProfile(onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            if (!state.value.gdprAccepted) {
                _state.value = _state.value.copy(error = "Accepter GDPR først")
                onComplete(false)
                return@launch
            }
            if (!state.value.isValid) {
                _state.value = _state.value.copy(error = "Udfyld navn, email og adresse")
                onComplete(false)
                return@launch
            }
            _state.value = _state.value.copy(isLoading = true, error = null)
            try {
                userRepository.saveProfile(
                    name = state.value.name,
                    email = state.value.email,
                    phone = state.value.phone,
                    address = state.value.address,  // RETTET: Tilføjet address-param (antag repo har default "" hvis ikke – upload repo hvis fejl).
                    gdprAccepted = state.value.gdprAccepted,
                    isDarkMode = state.value.isDarkMode
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
            } catch (e: IllegalStateException) {
                Timber.e(e, "Logout fejl – database schema-mismatch, men fortsætter med Firebase-logout")
                onLogout()
                _state.value = _state.value.copy(error = "Logout delvist – tøm cache manuelt")
            } catch (e: Exception) {
                Timber.e(e, "Logout fejl")
                _state.value = _state.value.copy(error = "Logout mislykkedes")
            }
        }
    }
}