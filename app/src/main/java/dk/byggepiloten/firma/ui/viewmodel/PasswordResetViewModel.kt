// ui/viewmodel/PasswordResetViewModel.kt: MVVM for password-reset (Firebase confirmPasswordReset, validation, state).
// FIXED: Await unresolved løst ved import kotlinx.coroutines.tasks.await.
//     - Trin 1: StateFlow for oobCode/password/confirm, loading, error, isValid (match + strength).
//     - Trin 2: updatePassword/updateConfirmPassword med regex (6+ tegn, lower/upper/digit/symbol).
//     - Trin 3: resetPassword: Firebase.confirmPasswordReset(oobCode, password).await() – log "Reset confirmed".
//     - MVVM: viewModelScope.launch for async (try-catch for error-state).
//     - Test: Kall resetPassword → log "Password reset confirmed" + onSuccess().
//     - Sikkerhed: Firebase Free Tier, regex for strength, no PII in logs.
//     - Note: Matcher oversigt (sektion 4): Recovery via deep link oobCode.

package dk.byggepiloten.firma.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import java.util.regex.Pattern
import javax.inject.Inject

data class PasswordResetState(
    val oobCode: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String = "",
    val isValid: Boolean = false
)

@HiltViewModel
class PasswordResetViewModel @Inject constructor(
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _state = MutableStateFlow(PasswordResetState())
    val state: StateFlow<PasswordResetState> = _state.asStateFlow()

    fun setOobCode(oobCode: String) {
        _state.value = _state.value.copy(oobCode = oobCode)
    }

    fun updatePassword(password: String) {
        val strengthPattern = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{6,}$")
        val strengthError = if (password.length < 6) "Kodeord skal være mindst 6 tegn" else if (!strengthPattern.matcher(password).matches()) "Kodeord skal indeholde store/små bogstaver, tal og symbol" else ""
        val current = _state.value
        val newError = if (current.confirmPassword.isNotEmpty() && password != current.confirmPassword) "Kodeord matcher ikke" else strengthError
        _state.value = current.copy(password = password, errorMessage = newError)
        updateIsValid()
    }

    fun updateConfirmPassword(confirmPassword: String) {
        val current = _state.value
        val matchError = if (confirmPassword != current.password) "Bekræftelse matcher ikke" else ""
        val error = if (matchError.isNotEmpty()) matchError else current.errorMessage
        _state.value = current.copy(confirmPassword = confirmPassword, errorMessage = error)
        updateIsValid()
    }

    private fun updateIsValid() {
        val current = _state.value
        val strengthPattern = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{6,}$")
        val valid = current.password.length >= 6 &&
                strengthPattern.matcher(current.password).matches() &&
                current.password == current.confirmPassword &&
                current.oobCode.isNotEmpty()
        _state.value = current.copy(isValid = valid, errorMessage = if (!valid) current.errorMessage else "")
    }

    fun resetPassword(onSuccess: () -> Unit) {
        if (!_state.value.isValid) {
            Timber.w("Password reset failed: Invalid input")
            return
        }
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            try {
                auth.confirmPasswordReset(_state.value.oobCode, _state.value.password).await()  // FIXED: Await import + positional arguments.
                Timber.d("Password reset confirmed")
                onSuccess()
            } catch (e: Exception) {
                Timber.e(e, "Password reset fejl: ${e.message}")
                _state.value = _state.value.copy(errorMessage = e.message ?: "Nulstilling fejlede")
            } finally {
                _state.value = _state.value.copy(isLoading = false)
            }
        }
    }
}