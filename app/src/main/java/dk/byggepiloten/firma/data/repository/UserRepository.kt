// File: app/src/main/java/dk/byggepiloten/firma/data/repository/UserRepository.kt
// FULD INTERFACE – udvidet saveProfile med gdprAccepted/isDarkMode for at matche impl/SettingsViewModel.

package dk.byggepiloten.firma.data.repository

import com.google.firebase.auth.FirebaseUser
import dk.byggepiloten.firma.data.model.FirmaUser
import dk.byggepiloten.firma.data.model.Profile
import kotlinx.coroutines.flow.Flow

/**
 * UserRepository.kt: Interface for user-repo med Unit-returns på suspend funs (eksplicit for subtype-match).
 * FIXED: Udvidet saveProfile med gdprAccepted/isDarkMode – matcher kald fra SettingsViewModel.
 *     - Trin 1: Tilføj params i signature. 2. Behold alle originale metoder (sendWelcomeEmail osv.).
 * Beholdt GDPR-persistence for synkronisering mellem ViewModels.
 */
interface UserRepository {

    suspend fun saveRole(role: String): Unit

    suspend fun getSavedRole(): String?

    suspend fun login(email: String, password: String, gdprAccepted: Boolean? = true): Boolean

    fun getFirmId(): Flow<Int?>

    suspend fun logout(): Unit

    suspend fun sendWelcomeEmail(email: String, role: String): Unit

    suspend fun validateToken(token: String, action: String): Boolean

    suspend fun sendMagicLink(email: String, role: String): Boolean

    suspend fun signInWithMagicLink(email: String, emailLink: String): Boolean

    suspend fun clearRole(): Unit

    suspend fun createUser(email: String, password: String, role: String, details: Map<String, Any>): String?

    suspend fun sendEmailVerification(uid: String): Boolean

    suspend fun isEmailVerified(uid: String): Boolean

    suspend fun sendPasswordResetEmail(email: String): Boolean

    suspend fun sendSignInLinkToEmail(email: String, role: String): Boolean

    fun getCurrentUser(): FirebaseUser?

    suspend fun savePrivateDetails(
        name: String, address: String, phone: String, email: String, gdprAccepted: Boolean
    ): Unit

    suspend fun saveFirmaSeekingDetails(
        firmaName: String, cvr: String, address: String, contactEmail: String, phone: String,
        profitPct: Float, gdprAccepted: Boolean
    ): Unit

    suspend fun saveContractorDetails(
        firmaName: String, cvr: String, address: String, bankAccount: String, profitPct: Float, gdprAccepted: Boolean
    ): Unit

    suspend fun validateCvr(cvr: String): Boolean

    suspend fun checkCvrExists(cvr: String): Boolean

    suspend fun createFirma(
        name: String, cvr: String, email: String, bankAccount: String, prices: Map<String, Float>
    ): Boolean

    // Eksisterende: Gem email generisk (f.eks. for alle roller) – kaldes før sendWelcomeEmail i onboarding
    suspend fun saveUserEmail(email: String): Unit

    // FIXED: GDPR-persistence for synkronisering mellem ViewModels (checkbox → repo → completeOnboarding-check).
    suspend fun saveGdprAccepted(gdpr: Boolean): Unit

    suspend fun getGdprAccepted(): Boolean

    suspend fun setEmailVerified(verified: Boolean): Unit

    suspend fun hasPassword(): Boolean

    suspend fun setPendingResetCode(code: String): Unit

    // Manglende metoder for SettingsViewModel
    suspend fun getCurrentProfile(): Profile?  // Hent profil fra DataStore/Room

    suspend fun saveDarkModePreference(darkMode: Boolean): Unit  // Gem dark mode i DataStore

    // RETTET: Udvidet med gdprAccepted/isDarkMode – matcher impl og SettingsViewModel-kald
    suspend fun saveProfile(name: String, email: String, phone: String, gdprAccepted: Boolean, isDarkMode: Boolean): Unit  // Gem profil i Firestore/Room

    suspend fun clearUser(): Unit  // Slet lokal user-data ved logout
}