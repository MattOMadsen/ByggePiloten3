package dk.byggepiloten.firma.data.model

data class Profile(
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val address: String = "",
    val darkMode: Boolean = false,
    val gdprAccepted: Boolean = false
)