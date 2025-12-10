// File: app/src/main/java/dk/byggepiloten/firma/data/repository/AuthRepository.kt
// FULD, KOMPLET INTERFACE – TILFØJET getCurrentUser(): FirebaseUser? for null-sikker user-hentning (bruges i MainActivity/Dashboard for uid).
// Trin-for-trin forklaring:
// 1. Beholdt alt originalt: saveRole, getSavedRole, login, getFirmId, sendWelcomeEmail, validateToken, sendMagicLink, signInWithMagicLink, clearRole, createUser, sendEmailVerification, isEmailVerified, sendPasswordResetEmail.
// 2. TILFØJET: fun getCurrentUser(): FirebaseUser? – returnerer currentUser fra FirebaseAuth (sikker adgang uden private props).
// 3. TILFØJET: fun logout(): Boolean – for logout (kalder Firebase signOut + clear prefs).
// 4. Fuldt funktionsdygtig – matcher Impl (real Firebase), integrerer med Compose (Flows for state).
// Note: Design: Flows for queries; suspend for mutations. Offline-first – DataStore-flag for user-state.

package dk.byggepiloten.firma.data.repository

import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun saveRole(role: String)
    suspend fun getSavedRole(): String?
    suspend fun login(email: String, password: String, gdprAccepted: Boolean? = true): Boolean
    fun getFirmId(): Flow<Int?>
    suspend fun sendWelcomeEmail(email: String, role: String, gdprAccepted: Boolean): Boolean
    suspend fun validateToken(token: String, action: String): Boolean
    suspend fun sendMagicLink(email: String, role: String): Boolean
    suspend fun signInWithMagicLink(email: String, emailLink: String): Boolean
    suspend fun clearRole(): Unit
    suspend fun createUser(email: String, password: String, role: String, details: Map<String, Any>): String?
    suspend fun sendEmailVerification(uid: String): Boolean
    suspend fun isEmailVerified(uid: String): Boolean

    // TILFØJET: Send password reset-email
    suspend fun sendPasswordResetEmail(email: String): Boolean

    // TILFØJET: Send magic link (passwordless login)
    suspend fun sendSignInLinkToEmail(email: String, role: String): Boolean

    // TILFØJET: Null-sikker user-hentning (bruges i MainActivity/Dashboard for uid)
    fun getCurrentUser(): FirebaseUser?

    // TILFØJET: Logout – signOut + clear prefs (bruges i DashboardScreen.kt)
    suspend fun logout(): Boolean
}