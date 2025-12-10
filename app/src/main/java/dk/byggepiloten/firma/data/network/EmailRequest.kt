package dk.byggepiloten.firma.data.network

/**
 * EmailRequest: Data class for Retrofit-request til WP-backend (/send-email).
 * FIXED: Tilføjet val confirmation_url: String? = null for at understøtte deep links (f.eks. byggepiloten://confirm).
 *     - Bruges i AuthRepositoryImpl_WP.sendWelcomeEmail() for clickable bekræftelseslink i mail.
 *     - Default: null (fallback til WP-endpoint i backend).
 *     - Serialiseres til JSON: {"email":"...", "subject":"...", "role":"...", "body":"...", "confirmation_url":"byggepiloten://..."}
 *     - GDPR: Inkluder kun nødvendige felter – ingen sensitive data.
 */
data class EmailRequest(
    val email: String,
    val subject: String = "Velkommen til ByggePiloten",  // FIXED: Default subject – løser "No value passed".
    val role: String,
    val body: String,
    val confirmation_url: String? = null  // NY: For deep link (f.eks. "byggepiloten://confirm?token=XYZ") – optional for bagudkompatibilitet
)