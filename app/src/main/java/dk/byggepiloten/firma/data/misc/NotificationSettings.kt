package dk.byggepiloten.firma.data.misc

/**
 * NotificationSettings: Model for notifikationsindstillinger (inspireret af oversigt: push/email).
 * - Bruges til at gemme præferencer for backup-notifikationer (7 dage før sletning).
 * - GDPR: Bruger samtykke før notifikationer sendes.
 */
data class NotificationSettings(
    val push: Boolean = false,
    val email: Boolean = false
)