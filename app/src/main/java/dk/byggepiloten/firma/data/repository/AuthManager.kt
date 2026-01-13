package dk.byggepiloten.firma.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import dk.byggepiloten.firma.data.database.UserDao
import dk.byggepiloten.firma.data.model.user.FirmaUser
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthManager @Inject constructor(
    private val auth: FirebaseAuth,
    private val dataStore: DataStore<Preferences>,
    private val userDao: UserDao
) {

    companion object {
        private val KEY_UID = stringPreferencesKey("user_uid")
        private val KEY_ROLE = stringPreferencesKey("user_role")
    }

    suspend fun saveUserSession(uid: String, role: String) {
        dataStore.edit { prefs ->
            prefs[KEY_UID] = uid
            prefs[KEY_ROLE] = role
        }
        Timber.d("AuthManager: Bruger gemt i DataStore – UID: $uid, Rolle: $role")  // Kommentar: Logging for session-gemning.
    }

    val currentUid: Flow<String?> = dataStore.data.map { it[KEY_UID] }  // Kommentar: Flow for nuværende UID – observerbar for UI-opdateringer.
    val currentRole: Flow<String?> = dataStore.data.map { it[KEY_ROLE] }  // Kommentar: Flow for nuværende rolle.

    suspend fun isLoggedIn(): Boolean {
        val uid = currentUid.first() ?: return false
        return auth.currentUser?.uid == uid  // Kommentar: Tjekker Firebase-session mod lokal DataStore.
    }

    suspend fun getLocalUser(): FirmaUser? {
        val uid = currentUid.first() ?: return null
        return userDao.getUserById(uid)  // Kommentar: Henter lokal bruger fra Room.
    }

    suspend fun signOut() {
        auth.signOut()
        dataStore.edit { it.clear() }
        userDao.clearAll()
        Timber.d("AuthManager: Bruger logget ud")  // Kommentar: Logging for logout.
    }

    suspend fun syncUserToRoom(firebaseUser: FirebaseUser, role: String) {
        val localUser = FirmaUser(
            id = firebaseUser.uid,
            email = firebaseUser.email,
            role = role,
            gdprAccepted = true,
            created_at = System.currentTimeMillis()  // RETTET: Ændret fra 'createdAt' til 'created_at' for at matche FirmaUser.kt's felt (GDPR-timestamp).
        )
        userDao.insertUser(localUser)
        Timber.d("AuthManager: Synkroniseret til Room")  // Kommentar: Logging for sync.
    }

    suspend fun cleanupOldUsers() {
        val cutoff = System.currentTimeMillis() - 24 * 60 * 60 * 1000
        userDao.deleteOldUsers(cutoff)
        Timber.d("AuthManager: GDPR cleanup udført")  // Kommentar: Logging for GDPR-sletning.
    }
}