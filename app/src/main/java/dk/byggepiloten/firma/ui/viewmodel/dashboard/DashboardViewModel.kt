package dk.byggepiloten.firma.ui.viewmodel.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.FirebaseTooManyRequestsException
import dagger.hilt.android.lifecycle.HiltViewModel
import dk.byggepiloten.firma.data.model.task.Request
import dk.byggepiloten.firma.data.repository.AuthRepository
import dk.byggepiloten.firma.data.repository.RequestRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val requestRepository: RequestRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _requests = MutableStateFlow<List<Request>>(emptyList()) // Private opgaver
    val requests: StateFlow<List<Request>> = _requests.asStateFlow()

    private val _newRequests =
        MutableStateFlow<List<Request>>(emptyList()) // Nye opgaver (status="new")
    val newRequests: StateFlow<List<Request>> = _newRequests.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false) // Bruges nu af "Opdater"-knappen
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private val _role = MutableStateFlow<String?>(null)
    val role: StateFlow<String?> = _role.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _showVerificationDialog = MutableStateFlow(false)
    val showVerificationDialog: StateFlow<Boolean> = _showVerificationDialog.asStateFlow()

    private val _isResending = MutableStateFlow(false)
    val isResending: StateFlow<Boolean> = _isResending.asStateFlow()

    private val _verificationMessage = MutableStateFlow<String?>(null)
    val verificationMessage: StateFlow<String?> = _verificationMessage.asStateFlow()

    private val _hasShownDialog = MutableStateFlow(false) // One-time dialog

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                loadRole()
                loadPrivateRequests()
                loadNewRequests()
                checkEmailVerification()
            } catch (e: Exception) {
                Timber.Forest.e(e, "loadData fejl")
                _error.value = "Fejl ved indlæsning af data"
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun loadRole() {
        viewModelScope.launch {
            try {
                val savedRole = authRepository.getSavedRole() ?: "PRIVATE"
                _role.value = savedRole
                Timber.Forest.d("Loaded role: $savedRole")
            } catch (e: Exception) {
                Timber.Forest.e(e, "loadRole fejl")
            }
        }
    }

    private fun loadPrivateRequests() {
        viewModelScope.launch {
            try {
                val userReqs = requestRepository.getUserRequests() ?: emptyList()
                _requests.value = userReqs
                Timber.Forest.d("Loaded private requests: ${userReqs.size}")
            } catch (e: Exception) {
                Timber.Forest.e(e, "loadPrivateRequests fejl")
                _error.value = "Fejl ved private opgaver"
            }
        }
    }

    private fun loadNewRequests() {
        viewModelScope.launch {
            try {
                requestRepository.getAllRequests().collect { list ->
                    _newRequests.value = list.filter { it.status == "new" }
                }
            } catch (e: Exception) {
                Timber.Forest.e(e, "loadNewRequests fejl")
                _error.value = "Fejl ved nye opgaver"
            }
        }
    }

    private fun checkEmailVerification() {
        viewModelScope.launch {
            try {
                val uid = authRepository.getCurrentUser()?.uid
                if (uid != null) {
                    val verified = authRepository.isEmailVerified(uid)
                    if (!verified && !_hasShownDialog.value) {
                        _showVerificationDialog.value = true
                        _hasShownDialog.value = true
                    } else if (verified) {
                        _showVerificationDialog.value = false
                    }
                } else {
                    Timber.Forest.d("Ingen bruger logget ind, kan ikke tjekke e-mail-bekræftelse.")
                }
            } catch (e: Exception) {
                Timber.Forest.e(e, "checkEmailVerification fejl")
                if (!_hasShownDialog.value) {
                    _showVerificationDialog.value = true
                    _hasShownDialog.value = true
                }
            }
        }
    }

    fun resendVerification() {
        viewModelScope.launch {
            _isResending.value = true
            try {
                val uid = authRepository.getCurrentUser()?.uid
                if (uid != null) {
                    val success = authRepository.sendEmailVerification(uid)
                    if (success) {
                        _verificationMessage.value = "Ny verifikationsmail sendt!"
                    } else {
                        _verificationMessage.value = "Fejl ved afsendelse"
                    }
                } else {
                    _verificationMessage.value = "Ingen bruger logget ind."
                }
            } catch (e: FirebaseTooManyRequestsException) {
                _verificationMessage.value = "For mange forsøg – vent lidt"
            } catch (e: Exception) {
                _verificationMessage.value = "Uventet fejl"
                Timber.Forest.e(e)
            } finally {
                _isResending.value = false
            }
        }
    }

    fun dismissVerificationDialog() {
        _showVerificationDialog.value = false
        _hasShownDialog.value = true
    }

    fun clearError() {
        _error.value = null
    }

    fun clearVerificationMessage() {
        _verificationMessage.value = null
    }

    fun logout(onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            val success = authRepository.logout()
            onComplete(success)
        }
    }

    fun refresh() {
        viewModelScope.launch {
            _isRefreshing.value = true
            try {
                loadData()
            } catch (e: Exception) {
                Timber.Forest.e(e, "refresh fejl")
                _error.value = "Opdatering mislykkedes"
            } finally {
                _isRefreshing.value = false
            }
        }
    }
}