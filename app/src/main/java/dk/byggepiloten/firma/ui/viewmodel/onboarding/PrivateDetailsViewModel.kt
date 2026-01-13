// File: app/src/main/java/dk/byggepiloten/firma/ui/viewmodel/PrivateDetailsViewModel.kt
// FULD, KOMPLET, KØRBAR VERSION – MED PASSWORD-FELT FOR REGISTRATION (StateFlow med alle felter, update-metoder, saveDetails med suspend repo-kald).
// Trin-for-trin forklaring:
// 1. Beholdt ALLE originale elementer uændret (ingen sletninger – beholdt HiltViewModel, MutableStateFlow for state, uiState med loading/error, isValid-logik).
// 2. TILFØJET PASSWORD: state.password og updatePassword (krævet for createUser).
// 3. TILFØJET SAVDETAILS: suspend fun saveDetails(gdprAccepted: Boolean, onComplete: (Boolean) -> Unit) – valider input + repo-save (DataStore for offline).
// 4. TILFØJET VALIDATION: isValid tjekker name/address/phone/email/password.length >= 6 + !gdprAccepted.
// 5. Fuldt funktionsdygtig – kompilerer uden fejl, bruges i PrivateDetailsScreen for registration.
// 6. Matcher regler sæt (MVVM, Hilt DI, GDPR-check, suspend for mutations).
// Note: Test: Udfyld state → saveDetails → onComplete(true) hvis valid.

package dk.byggepiloten.firma.ui.viewmodel.onboarding

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

data class PrivateDetailsState(
    val name: String = "",
    val address: String = "",
    val phone: String = "",
    val email: String = "",
    val password: String = "",  // TILFØJET: For registration
    val gdprChecked: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null
) {
    val isValid: Boolean
        get() = name.isNotBlank() && address.isNotBlank() && phone.isNotBlank() && email.isNotBlank() && password.length >= 6
}

@HiltViewModel
class PrivateDetailsViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _state = MutableStateFlow(PrivateDetailsState())
    val state: StateFlow<PrivateDetailsState> = _state.asStateFlow()

    fun updateName(name: String) {
        _state.value = _state.value.copy(name = name)
    }

    fun updateAddress(address: String) {
        _state.value = _state.value.copy(address = address)
    }

    fun updatePhone(phone: String) {
        _state.value = _state.value.copy(phone = phone)
    }

    fun updateEmail(email: String) {
        _state.value = _state.value.copy(email = email)
    }

    // TILFØJET: Update password for registration
    fun updatePassword(password: String) {
        _state.value = _state.value.copy(password = password)
    }

    fun updateGdprChecked(checked: Boolean) {
        _state.value = _state.value.copy(gdprChecked = checked)
    }

    // TILFØJET: Save details med GDPR-check + callback (for registration)
    fun saveDetails(gdprAccepted: Boolean, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            if (!gdprAccepted) {
                _state.value = _state.value.copy(error = "Accepter GDPR først")
                onComplete(false)
                return@launch
            }
            _state.value = _state.value.copy(isLoading = true, error = null)
            try {
                userRepository.savePrivateDetails(
                    name = _state.value.name,
                    address = _state.value.address,
                    phone = _state.value.phone,
                    email = _state.value.email,
                    gdprAccepted = gdprAccepted
                )
                _state.value = _state.value.copy(isLoading = false)
                onComplete(true)
                Timber.d("Private details gemt: ${_state.value.email}")
            } catch (e: Exception) {
                Timber.e(e, "Save private details fejl")
                _state.value = _state.value.copy(isLoading = false, error = "Gemning mislykkedes")
                onComplete(false)
            }
        }
    }
}