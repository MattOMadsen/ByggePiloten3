// File: app/src/main/java/dk/byggepiloten/firma/ui/viewmodel/FirmaSeekingDetailsViewModel.kt
// FULD, FUNKTIONSYGTIG VERSION – oprettet for at løse Unresolved reference 'FirmaSeekingDetailsViewModel'.
// Trin-for-trin forklaring:
// 1. Importér nødvendige komponenter: ViewModel, Hilt, flows, coroutines.
// 2. Definer data class FirmaSeekingState med alle felter fra screen: firmaName, errors, etc. Inkluder isValid og isLoading.
// 3. @HiltViewModel class FirmaSeekingDetailsViewModel: Bruger MutableStateFlow til state.
// 4. Update-funktioner: Opdater state og kald validate() for at tjekke errors og isValid.
// 5. validate(): Privat funktion der tjekker alle felter, sætter errors og opdaterer isValid.
// 6. saveDetails(): Valider først, sæt isLoading, simuler save (kan udvides med repo/Firebase), kald onSuccess, reset loading.
// 7. Validering: FirmaName ikke blank, CVR præcis 8 cifre, adresse/email/bank ikke blank, email simpel check, phone optional.
// 8. Offline-first: Kan udvides med repository-injektion via Hilt for Room/Firestore sync.
// 9. Brugt regex for email-validering (simpel).
// 10. Denne ViewModel matcher screen-koden og løser alle state-relaterede errors.

package dk.byggepiloten.firma.ui.viewmodel.firma

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class FirmaSeekingState(
    val firmaName: String = "",
    val firmaNameError: Boolean = false,
    val cvr: String = "",
    val cvrError: Boolean = false,
    val address: String = "",
    val addressError: Boolean = false,
    val email: String = "",
    val emailError: Boolean = false,
    val phone: String = "",
    val bankAccount: String = "",
    val bankAccountError: Boolean = false,
    val gdprChecked: Boolean = false,
    val isValid: Boolean = false,
    val isLoading: Boolean = false
)

@HiltViewModel
class FirmaSeekingDetailsViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow(FirmaSeekingState())
    val state: StateFlow<FirmaSeekingState> = _state.asStateFlow()

    fun updateFirmaName(name: String) {
        _state.update { it.copy(firmaName = name) }
        validate()
    }

    fun updateCvr(cvr: String) {
        _state.update { it.copy(cvr = cvr) }
        validate()
    }

    fun updateAddress(address: String) {
        _state.update { it.copy(address = address) }
        validate()
    }

    fun updateEmail(email: String) {
        _state.update { it.copy(email = email) }
        validate()
    }

    fun updatePhone(phone: String) {
        _state.update { it.copy(phone = phone) }
        validate()
    }

    fun updateBankAccount(bank: String) {
        _state.update { it.copy(bankAccount = bank) }
        validate()
    }

    fun updateGdprChecked(checked: Boolean) {
        _state.update { it.copy(gdprChecked = checked) }
        validate()
    }

    private fun validate(): Boolean {
        _state.update { current ->
            val firmaError = current.firmaName.isBlank()
            val cvrError = current.cvr.length != 8 || !current.cvr.all { it.isDigit() }
            val addressError = current.address.isBlank()
            val emailError = current.email.isBlank() || !Patterns.EMAIL_ADDRESS.matcher(current.email).matches()
            val bankError = current.bankAccount.isBlank()
            val valid = !firmaError && !cvrError && !addressError && !emailError && !bankError && current.gdprChecked

            current.copy(
                firmaNameError = firmaError,
                cvrError = cvrError,
                addressError = addressError,
                emailError = emailError,
                bankAccountError = bankError,
                isValid = valid
            )
        }
        return _state.value.isValid
    }

    fun saveDetails(onSuccess: () -> Unit) {
        if (validate()) {
            viewModelScope.launch {
                _state.update { it.copy(isLoading = true) }
                // Simuler save: Udvid med repository eller Firebase her (f.eks. repo.saveFirmaSeekingDetails(state.value))
                // For nu: Antag succes efter delay
                delay(1000) // Simuler netværk
                onSuccess()
                _state.update { it.copy(isLoading = false) }
            }
        }
    }
}