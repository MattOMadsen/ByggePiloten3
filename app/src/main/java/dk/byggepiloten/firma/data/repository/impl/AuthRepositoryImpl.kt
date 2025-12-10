package dk.byggepiloten.firma.data.repository.impl

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.google.firebase.FirebaseApp
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory
import com.google.firebase.auth.ActionCodeSettings
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.qualifiers.ApplicationContext
import dk.byggepiloten.firma.BuildConfig  // TILFØJET: Import af BuildConfig for at løse unresolved reference (genereret af Gradle for debug/release-check).
import dk.byggepiloten.firma.data.repository.AuthRepository
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
class AuthRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    @UserDataStore private val dataStore: DataStore<Preferences>,
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : AuthRepository {

    companion object {
        private val ROLE_KEY = stringPreferencesKey("user_role")
        private val FIRM_ID_KEY = stringPreferencesKey("firm_id")
    }

    // TILFØJET: Init App Check (suspend – kaldes i login for sikkerhed)
    // Trin-for-trin forklaring:
    // 1. Initialiser FirebaseApp hvis ikke allerede gjort (sikrer alt Firebase virker).
    // 2. Hent AppCheck-instans.
    // 3. Tjek BuildConfig.DEBUG (nu importeret) for at vælge provider: Debug til udvikling (undgår Play Integrity-fejl i emulator), Play Integrity til produktion (sikrer app-integritet mod tampering).
    // 4. Log succes/failure med Timber (matcher logging-krav).
    // 5. Kører på IO-dispatcher for asynkronitet uden at blokere UI (Coroutines + WorkManager-kompatibelt).
    private suspend fun initAppCheck() = withContext(Dispatchers.IO) {
        try {
            FirebaseApp.initializeApp(context)  // Sikrer Firebase init
            val appCheck = FirebaseAppCheck.getInstance()
            if (BuildConfig.DEBUG) {
                appCheck.installAppCheckProviderFactory(DebugAppCheckProviderFactory.getInstance())
            } else {
                appCheck.installAppCheckProviderFactory(PlayIntegrityAppCheckProviderFactory.getInstance())
            }
            Timber.d("App Check initialized")
        } catch (e: Exception) {
            Timber.e(e, "App Check init failed")
        }
    }

    override suspend fun saveRole(role: String) {
        dataStore.edit { prefs ->
            prefs[ROLE_KEY] = role
        }
    }

    override suspend fun getSavedRole(): String? = dataStore.data.first()[ROLE_KEY]

    override suspend fun login(email: String, password: String, gdprAccepted: Boolean?): Boolean = withContext(Dispatchers.IO) {
        initAppCheck()
        try {
            val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            if (result.user != null) {
                val uid = result.user!!.uid
                // TILFØJET FIX: Efter succesfuld login, hent user-doc fra Firestore, udtræk rolle, og gem i DataStore (løser bug hvor rolle ikke opdateres ved login for eksisterende brugere).
                // Trin-for-trin:
                // 1. Hent doc (await for suspend).
                // 2. Get "role" (fallback til "UNKNOWN" hvis mangler – log fejl).
                // 3. Kall saveRole for at opdatere lokal session.
                // Matcher offline-first: Hvis offline, firestore.get() fejler – fallback til getSavedRole (hvis tidligere gemt).
                try {
                    val doc = firestore.collection("users").document(uid).get().await()
                    val role = doc.getString("role") ?: "UNKNOWN"
                    if (role == "UNKNOWN") Timber.e("Role mangler i Firestore for uid: $uid")
                    saveRole(role)
                } catch (e: Exception) {
                    Timber.e(e, "Fejl ved hentning af rolle fra Firestore – brug cached hvis tilgængelig")
                }
                Timber.d("Login success: $email")
                true
            } else {
                Timber.w("Login failed: No user")
                false
            }
        } catch (e: Exception) {
            Timber.e(e, "Login failed for $email")
            false
        }
    }

    override fun getFirmId(): Flow<Int?> = dataStore.data.map { prefs ->
        prefs[FIRM_ID_KEY]?.toIntOrNull()
    }

    override suspend fun sendWelcomeEmail(email: String, role: String, gdprAccepted: Boolean): Boolean = withContext(Dispatchers.IO) {
        // Implement send welcome email – f.eks. via EmailService.
        true
    }

    override suspend fun validateToken(token: String, action: String): Boolean = withContext(Dispatchers.IO) {
        // Implement token validation.
        true
    }

    override suspend fun sendMagicLink(email: String, role: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val actionCodeSettings = ActionCodeSettings.newBuilder()
                .setAndroidPackageName(context.packageName, true, null)
                .setUrl("https://byggepiloten.dk/verify")
                .build()
            firebaseAuth.sendSignInLinkToEmail(email, actionCodeSettings).await()
            true
        } catch (e: Exception) {
            Timber.e(e, "Send magic link failed")
            false
        }
    }

    override suspend fun signInWithMagicLink(email: String, emailLink: String): Boolean = withContext(Dispatchers.IO) {
        try {
            firebaseAuth.signInWithEmailLink(email, emailLink).await()
            true
        } catch (e: Exception) {
            Timber.e(e, "Sign in with magic link failed")
            false
        }
    }

    override suspend fun clearRole(): Unit = dataStore.edit { prefs ->
        prefs.remove(ROLE_KEY)
    } as Unit  // RETTET: Ignore return value med as Unit – løser return type mismatch (edit returnerer Preferences, men vi vil have Unit).

    override suspend fun createUser(email: String, password: String, role: String, details: Map<String, Any>): String? = withContext(Dispatchers.IO) {
        try {
            val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            val uid = result.user?.uid ?: return@withContext null
            firestore.collection("users").document(uid).set(details).await()
            uid
        } catch (e: Exception) {
            Timber.e(e, "Create user failed")
            null
        }
    }

    override suspend fun sendEmailVerification(uid: String): Boolean = withContext(Dispatchers.IO) {
        try {
            firebaseAuth.currentUser?.sendEmailVerification()?.await()
            true
        } catch (e: Exception) {
            Timber.e(e, "Send verification failed")
            false
        }
    }

    override suspend fun isEmailVerified(uid: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val user = firebaseAuth.currentUser
            user?.reload()?.await()
            user?.isEmailVerified ?: false
        } catch (e: Exception) {
            Timber.e(e, "Is verified failed")
            false
        }
    }

    override suspend fun sendPasswordResetEmail(email: String): Boolean = withContext(Dispatchers.IO) {
        try {
            firebaseAuth.sendPasswordResetEmail(email).await()
            true
        } catch (e: Exception) {
            Timber.e(e, "Send reset failed")
            false
        }
    }

    override suspend fun sendSignInLinkToEmail(email: String, role: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val actionCodeSettings = ActionCodeSettings.newBuilder()
                .setAndroidPackageName(context.packageName, true, null)
                .setUrl("https://byggepiloten.dk/verify")
                .build()
            firebaseAuth.sendSignInLinkToEmail(email, actionCodeSettings).await()
            true
        } catch (e: Exception) {
            Timber.e(e, "Send sign in link failed")
            false
        }
    }

    override fun getCurrentUser(): FirebaseUser? = firebaseAuth.currentUser

    override suspend fun logout(): Boolean = withContext(Dispatchers.IO) {
        try {
            firebaseAuth.signOut()
            clearRole()
            true
        } catch (e: Exception) {
            Timber.e(e, "Logout failed")
            false
        }
    }
}