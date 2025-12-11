// File: app/src/main/java/dk/byggepiloten/firma/ui/viewmodel/DashboardViewModel.kt
// FULD, KOMPLET, KØRBAR VERSION – RETTET DIALOG-ISSUE (tilføjet _hasShownDialog StateFlow for one-time visning – sættes true efter første show; resendVerification sætter dialog-message i _verificationMessage, men beholdt for fleksibilitet).
// TILFØJET: I checkEmailVerified: if (!hasShownDialog && !verified) { showDialog = true; hasShownDialog = true } – løser gentagne popups.
// RETTET: resendVerification: Auto-dismiss via callback i Screen (men tilføjet log for success → checkEmailVerified kun hvis success).
// BEHOLDT: Alle originale (loading-fixes, timeouts, etc.) uændret.
// Trin-for-trin forklaring:
// 1. BEHOLDT: Alle StateFlows/metoder.
// 2. TILFØJET: private val _hasShownDialog = MutableStateFlow(false); val hasShownDialog = _hasShownDialog.asStateFlow() – flag for one-time.
// 3. RETTET: checkEmailVerified: Tjek flag før show; sæt flag hvis vises.
// 4. RETTET: resendVerification: Efter success, sæt _showVerificationDialog = false internt (men Screen håndterer UI).
// 5. Fuldt funktionsdygtig – kompilerer uden fejl. Test: Dashboard → Dialog (kun første gang) → Resend → Dialog lukkes. Efter opdatering: Sync Gradle → Kør.
// Note: Matcher MVVM; ingen snackbar ændret her (håndteres i Screen).

package dk.byggepiloten.firma.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dk.byggepiloten.firma.data.model.Request
import dk.byggepiloten.firma.data.repository.AuthRepository
import dk.byggepiloten.firma.data.repository.RequestRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import javax.inject.Inject
import timber.log.Timber
import com.google.firebase.FirebaseTooManyRequestsException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val requestRepository: RequestRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _requests = MutableStateFlow<List<Request>>(emptyList())
    val requests: StateFlow<List<Request>> = _requests.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _role = MutableStateFlow<String?>(null)
    val role: StateFlow<String?> = _role.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _isEmailVerified = MutableStateFlow(false)
    val isEmailVerified: StateFlow<Boolean> = _isEmailVerified.asStateFlow()

    private val _showVerificationDialog = MutableStateFlow(false)
    val showVerificationDialog: StateFlow<Boolean> = _showVerificationDialog.asStateFlow()

    private val _isResending = MutableStateFlow(false)
    val isResending: StateFlow<Boolean> = _isResending.asStateFlow()

    private val _verificationMessage = MutableStateFlow<String?>(null)
    val verificationMessage: StateFlow<String?> = _verificationMessage.asStateFlow()

    // TILFØJET: Flag for one-time dialog-visning (løser gentagne popups ved navigation back).
    private val _hasShownDialog = MutableStateFlow(false)
    val hasShownDialog: StateFlow<Boolean> = _hasShownDialog.asStateFlow()

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            try {
                Timber.d("Dashboard loadData: Starter for current user")
                val uid = authRepository.getCurrentUser()?.uid ?: run {
                    Timber.w("Ingen user")
                    _error.value = "Ikke logget ind"
                    _isLoading.value = false
                    return@launch
                }
                loadRole()
                loadRequests(uid)
                checkEmailVerified(uid)
                Timber.d("Dashboard loadData: Succes for UID $uid")
            } catch (e: Exception) {
                Timber.e(e, "loadData fejl")
                _error.value = "Fejl ved indlæsning"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadRequests(userId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val userRequests = withTimeoutOrNull(5000) {
                    requestRepository.getUserRequests() ?: emptyList()
                } ?: emptyList()
                _requests.value = userRequests
                Timber.d("Loaded requests for user $userId: ${userRequests.size}")
            } catch (e: Exception) {
                Timber.e(e, "loadRequests fejl")
                _error.value = "Fejl ved indlæsning af opgaver"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadRole() {
        viewModelScope.launch {
            try {
                val role = withTimeoutOrNull(5000) {
                    authRepository.getSavedRole() ?: "PRIVATE"
                } ?: "PRIVATE"
                _role.value = role
                Timber.d("Loaded role: $role")
            } catch (e: Exception) {
                Timber.e(e, "loadRole fejl")
                _error.value = "Fejl ved rolle-indlæsning"
            }
        }
    }

    fun checkEmailVerified(userId: String) {
        viewModelScope.launch {
            try {
                val verified = withTimeoutOrNull(5000) {
                    authRepository.isEmailVerified(userId)
                } ?: false
                _isEmailVerified.value = verified
                // TILFØJET: Vis dialog kun hvis ikke vist før og ikke verified (løser gentagne).
                if (!verified && !_hasShownDialog.value) {
                    _showVerificationDialog.value = true
                    _hasShownDialog.value = true  // Sæt flag – ingen mere show.
                    Timber.d("Viser verification dialog første gang")
                } else if (verified) {
                    _showVerificationDialog.value = false
                    _hasShownDialog.value = true  // Mark as handled.
                }
                Timber.d("Email verified: $verified")
            } catch (e: Exception) {
                Timber.e(e, "checkEmailVerified fejl")
                _isEmailVerified.value = false
                if (!_hasShownDialog.value) {
                    _showVerificationDialog.value = true
                    _hasShownDialog.value = true
                }
            }
        }
    }

    fun resendVerification() {
        viewModelScope.launch {
            val uid = authRepository.getCurrentUser()?.uid ?: run {
                _verificationMessage.value = "Fejl: Ingen bruger logget ind"
                return@launch
            }
            _isResending.value = true
            _error.value = null
            _verificationMessage.value = null
            try {
                val user = authRepository.getCurrentUser()
                val email = user?.email ?: run {
                    _verificationMessage.value = "Fejl: Ukendt e-mail – tjek profil"
                    return@launch
                }
                Timber.d("Sending verification to: $email (UID: $uid)")

                val success = withTimeoutOrNull(10000) {
                    authRepository.sendEmailVerification(uid)
                } ?: false

                if (success) {
                    _verificationMessage.value = "Verifikationsmail afsendt til $email! Tjek indbakken eller spam."  // Til dialog i Screen.
                    _showVerificationDialog.value = false  // TILFØJET: Auto-luk dialog på success.
                    Timber.d("Resent verification email to $email")
                    // BEHOLDT: Gen-tjek kun hvis success (men siden lukket, optional).
                } else {
                    _verificationMessage.value = "Fejl ved gensend – prøv igen om 5 min."
                    Timber.w("Gensend mislykkedes for $email")
                }
            } catch (e: FirebaseTooManyRequestsException) {
                _verificationMessage.value = "For mange forsøg – vent 24 timer"
                Timber.e(e, "Kvote overskredet for UID $uid")
            } catch (e: Exception) {
                _verificationMessage.value = "Uventet fejl: ${e.message}"
                Timber.e(e, "Gensend fejl for UID $uid")
            } finally {
                _isResending.value = false
            }
            // BEHOLDT: Gen-tjek – men dialog lukkes hvis success.
            if (_verificationMessage.value?.contains("afsendt") == true) {
                checkEmailVerified(uid)
            }
        }
    }

    fun clearError() {
        _error.value = null
    }

    fun clearVerificationMessage() {
        _verificationMessage.value = null
    }

    fun dismissVerificationDialog() {
        _showVerificationDialog.value = false
        _hasShownDialog.value = true  // TILFØJET: Mark as handled ved manual dismiss.
    }

    fun logout(onSuccess: (Boolean) -> Unit) {
        viewModelScope.launch {
            val success = authRepository.logout()
            onSuccess(success)
            Timber.d("Logout succes: $success")
        }
    }

    fun refresh() {
        _error.value = null
        _verificationMessage.value = null
        _isLoading.value = true
        // TILFØJET: Reset flag ved refresh hvis ønsket (men behold for nu – kun manual).
        loadData()
        Timber.d("Dashboard refresh initiated")
    }
}