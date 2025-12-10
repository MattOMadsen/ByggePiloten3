package dk.byggepiloten.firma.data.model

data class OnboardingState(
    val userRole: String? = null,
    val detailsComplete: Boolean = false,
    val isAuthenticated: Boolean = false,
    val gdprAccepted: Boolean = false,
    val isGuest: Boolean = false,
    val emailSent: Boolean = false
)
