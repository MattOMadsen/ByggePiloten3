package dk.byggepiloten.firma.data.repository.impl

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import dk.byggepiloten.firma.data.database.AppDatabase
import dk.byggepiloten.firma.data.model.FirmaUser
import dk.byggepiloten.firma.data.model.Profile
import dk.byggepiloten.firma.data.network.EmailRequest
import dk.byggepiloten.firma.data.network.EmailService
import dk.byggepiloten.firma.data.repository.AuthManager
import dk.byggepiloten.firma.data.repository.UserRepository
import dk.byggepiloten.firma.di.UserDataStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val db: AppDatabase,
    private val firestore: FirebaseFirestore,
    private val gson: Gson,
    private val firebaseAuth: FirebaseAuth,
    private val emailService: EmailService,
    private val authManager: AuthManager,
    @UserDataStore private val dataStore: DataStore<Preferences>
) : UserRepository {

    companion object {
        private val Keys = object {
            val ROLE_KEY = stringPreferencesKey("user_role")
            val EMAIL_KEY = stringPreferencesKey("user_email")
            val GDPR_ACCEPTED_KEY = booleanPreferencesKey("gdpr_accepted")
            val EMAIL_VERIFIED_KEY = booleanPreferencesKey("email_verified")
            val HAS_PASSWORD_KEY = booleanPreferencesKey("has_password")
            val PENDING_RESET_CODE = stringPreferencesKey("pending_reset_code")
            val DARK_MODE_KEY = booleanPreferencesKey("dark_mode")
            val FIRM_ID_KEY = stringPreferencesKey("firm_id")
        }
    }

    override fun getFirmId(): Flow<Int?> {
        return dataStore.data.map { it[Keys.FIRM_ID_KEY]?.toIntOrNull() }
    }

    override suspend fun saveRole(role: String) {
        dataStore.edit { it[Keys.ROLE_KEY] = role }
    }

    override suspend fun getSavedRole(): String? {
        return dataStore.data.map { it[Keys.ROLE_KEY] }.first()
    }

    override suspend fun login(email: String, password: String, gdprAccepted: Boolean?): Boolean {
        return try {
            firebaseAuth.signInWithEmailAndPassword(email, password).await()
            gdprAccepted?.let { saveGdprAccepted(it) }
            true
        } catch (e: Exception) {
            Timber.e(e, "Login failed")
            false
        }
    }

    override suspend fun logout() {
        authManager.signOut()
    }

    override suspend fun sendWelcomeEmail(email: String, role: String) {
        try {
            val body = when (role) {
                "private" -> "Velkommen som privat kunde til ByggePiloten! Opret din første opgave i dag."
                "contractor" -> "Velkommen som håndværkerfirma! Indtast dine priser og begynd at byde på opgaver."
                else -> "Velkommen til ByggePiloten!"
            }
            val request = EmailRequest(
                email = email,
                subject = "Velkommen til ByggePiloten",
                role = role,
                body = body
            )
            emailService.sendEmail(request)
            Timber.d("Velkomst-e-mail sendt til $email for rolle $role")
        } catch (e: Exception) {
            Timber.w(e, "Send velkomst-e-mail fejlede")
            throw e
        }
    }

    override suspend fun validateToken(token: String, action: String): Boolean {
        // Implementer token validering
        return true
    }

    override suspend fun sendMagicLink(email: String, role: String): Boolean {
        // Implementer magic link afsendelse
        return true
    }

    override suspend fun signInWithMagicLink(email: String, emailLink: String): Boolean {
        // Implementer magic link login
        return true
    }

    override suspend fun clearRole() {
        dataStore.edit { it.remove(Keys.ROLE_KEY) }
    }

    override suspend fun createUser(email: String, password: String, role: String, details: Map<String, Any>): String? {
        return try {
            val userCredential = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            userCredential.user?.uid?.also { uid ->
                saveRole(role)
                authManager.syncUserToRoom(userCredential.user!!, role)
                sendWelcomeEmail(email, role)
            }
        } catch (e: Exception) {
            Timber.e(e, "Create user failed")
            null
        }
    }

    override suspend fun sendEmailVerification(uid: String): Boolean {
        return try {
            firebaseAuth.currentUser?.sendEmailVerification()?.await()
            true
        } catch (e: Exception) {
            Timber.e(e, "Send email verification failed")
            false
        }
    }

    override suspend fun isEmailVerified(uid: String): Boolean {
        return firebaseAuth.currentUser?.isEmailVerified ?: false
    }

    override suspend fun sendPasswordResetEmail(email: String): Boolean {
        return try {
            firebaseAuth.sendPasswordResetEmail(email).await()
            true
        } catch (e: Exception) {
            Timber.e(e, "Send password reset email failed")
            false
        }
    }

    override suspend fun sendSignInLinkToEmail(email: String, role: String): Boolean {
        return try {
            true
        } catch (e: Exception) {
            Timber.e(e, "Send sign-in link failed")
            false
        }
    }

    override fun getCurrentUser(): FirebaseUser? {
        return firebaseAuth.currentUser
    }

    override suspend fun savePrivateDetails(name: String, address: String, phone: String, email: String, gdprAccepted: Boolean) {
        val uid = firebaseAuth.currentUser?.uid ?: return
        val user = FirmaUser(id = uid, name = name, email = email, phone = phone, gdprAccepted = gdprAccepted, address = address, role = "private")
        firestore.collection("users").document(uid).set(user).await()
        db.userDao().insertUser(user)
        saveGdprAccepted(gdprAccepted)
    }

    override suspend fun saveFirmaSeekingDetails(firmaName: String, cvr: String, address: String, contactEmail: String, phone: String, profitPct: Float, gdprAccepted: Boolean) {
        saveGdprAccepted(gdprAccepted)
    }

    override suspend fun saveContractorDetails(firmaName: String, cvr: String, address: String, bankAccount: String, profitPct: Float, gdprAccepted: Boolean) {
        saveGdprAccepted(gdprAccepted)
    }

    override suspend fun validateCvr(cvr: String): Boolean {
        return true
    }

    override suspend fun checkCvrExists(cvr: String): Boolean {
        return false
    }

    override suspend fun createFirma(name: String, cvr: String, email: String, bankAccount: String, prices: Map<String, Float>): Boolean {
        return true
    }

    override suspend fun saveUserEmail(email: String) {
        dataStore.edit { it[Keys.EMAIL_KEY] = email }
    }

    override suspend fun saveGdprAccepted(gdpr: Boolean) {
        dataStore.edit { it[Keys.GDPR_ACCEPTED_KEY] = gdpr }
    }

    override suspend fun getGdprAccepted(): Boolean {
        return dataStore.data.map { it[Keys.GDPR_ACCEPTED_KEY] ?: false }.first()
    }

    override suspend fun setEmailVerified(verified: Boolean) {
        dataStore.edit { it[Keys.EMAIL_VERIFIED_KEY] = verified }
    }

    override suspend fun hasPassword(): Boolean {
        return dataStore.data.map { it[Keys.HAS_PASSWORD_KEY] ?: false }.first()
    }

    override suspend fun setPendingResetCode(code: String) {
        dataStore.edit { it[Keys.PENDING_RESET_CODE] = code }
    }

    override suspend fun getCurrentProfile(): Profile? {
        val uid = firebaseAuth.currentUser?.uid ?: return null
        val user = db.userDao().getUserById(uid)
        return user?.let {
            Profile(
                name = it.name ?: "",
                email = it.email ?: "",
                phone = it.phone ?: "",
                address = it.address ?: "",
                darkMode = dataStore.data.map { prefs -> prefs[Keys.DARK_MODE_KEY] ?: false }.first(),
                gdprAccepted = it.gdprAccepted
            )
        }
    }

    override suspend fun saveDarkModePreference(darkMode: Boolean) {
        dataStore.edit { prefs ->
            prefs[Keys.DARK_MODE_KEY] = darkMode
        }
    }

    override suspend fun saveProfile(name: String, email: String, phone: String, address: String, gdprAccepted: Boolean, isDarkMode: Boolean) {
        val uid = firebaseAuth.currentUser?.uid ?: return
        val role = getSavedRole() ?: return
        val updatedUser = FirmaUser(id = uid, name = name, email = email, phone = phone, address = address, gdprAccepted = gdprAccepted, role = role)
        firestore.collection("users").document(uid).set(updatedUser).await()
        db.userDao().updateUser(updatedUser)
        saveDarkModePreference(isDarkMode)
        Timber.d("Profil gemt: $name, GDPR: $gdprAccepted")
    }

    override suspend fun clearUser() {
        dataStore.edit { it.clear() }
        db.userDao().clearAll()
        Timber.d("Lokal user-data ryddet (GDPR)")
    }
}