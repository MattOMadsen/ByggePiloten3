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
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject
import timber.log.Timber

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val requestRepository: RequestRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _requests = MutableStateFlow<List<Request>>(emptyList())
    val requests: StateFlow<List<Request>> = _requests.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _role = MutableStateFlow<String?>(null)
    val role: StateFlow<String?> = _role.asStateFlow()

    private val _isEmailVerified = MutableStateFlow(false)
    val isEmailVerified: StateFlow<Boolean> = _isEmailVerified.asStateFlow()

    private val _showVerificationDialog = MutableStateFlow(false)
    val showVerificationDialog: StateFlow<Boolean> = _showVerificationDialog.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        val currentUser = authRepository.getCurrentUser()
        if (currentUser == null) {
            Timber.w("DashboardViewModel: Ingen currentUser – viser ingen requests")
            _requests.value = emptyList()
            _isLoading.value = false
            return
        }
        val userId = currentUser.uid
        loadRequests(userId)
        loadRole(userId)
        checkEmailVerified(userId)
    }

    fun loadRequests(userId: String) {
        Timber.d("DashboardViewModel: Loader requests for userId: $userId")

        viewModelScope.launch {
            requestRepository.getAllRequests()
                .map { requests -> requests.filter { it.userId == userId } }
                .catch { e ->
                    val errorMsg = when {
                        e.message?.contains("network") == true -> "Netværksfejl – tjek forbindelse"
                        else -> e.message ?: "Uventet fejl ved load"
                    }
                    _error.value = errorMsg
                    _isLoading.value = false
                    Timber.e(e, "Load requests fejl: $errorMsg")
                }
                .collect { data ->
                    _requests.value = data
                    _isLoading.value = false
                    Timber.d("Loaded requests for user $userId: ${data.size}")
                }
        }
    }

    fun loadRole(userId: String) {
        viewModelScope.launch {
            _role.value = authRepository.getSavedRole()  // TILFØJET: Load role fra repo – tilpas hvis async.
            Timber.d("Loaded role: ${_role.value}")
        }
    }

    fun checkEmailVerified(userId: String) {
        viewModelScope.launch {
            _isEmailVerified.value = authRepository.isEmailVerified(userId)
            _showVerificationDialog.value = !_isEmailVerified.value
            Timber.d("Email verified: ${_isEmailVerified.value}")
        }
    }

    fun resendVerification() {
        viewModelScope.launch {
            val success = authRepository.sendEmailVerification(authRepository.getCurrentUser()?.uid ?: return@launch)
            if (success) Timber.d("Resent verification email")
            checkEmailVerified(authRepository.getCurrentUser()?.uid ?: return@launch)
        }
    }

    fun dismissVerificationDialog() {
        _showVerificationDialog.value = false
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
        _isLoading.value = true
        loadData()
        Timber.d("Dashboard refresh initiated")
    }
}