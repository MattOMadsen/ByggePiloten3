package dk.byggepiloten.firma.ui.viewmodel.onboarding

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
            Timber.Forest.d("OnboardingViewModel: Gemt rolle loader: $savedRole")
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
            Timber.Forest.d("OnboardingViewModel: DataStore tømt – force reload")
            val freshRole = authRepository.getSavedRole()
            _selectedRole.value = freshRole
            Timber.Forest.d("OnboardingViewModel: Efter refresh: $freshRole")
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
                authRepository.sendEmailVerification(uid)
                Timber.Forest.d("REGISTRATION SUCCES: UID $uid, Rolle: $role – verification sendt")
                completeOnboarding()
            } else {
                Timber.Forest.e("REGISTRATION FEJLEDE: Tjek email/password")
            }
        }
    }
}