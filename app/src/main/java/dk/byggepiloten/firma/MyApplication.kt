package dk.byggepiloten.firma

import android.app.Application
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import dagger.hilt.android.HiltAndroidApp
import dk.byggepiloten.firma.BuildConfig
import dk.byggepiloten.firma.data.repository.AuthManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay  // NY: Import for delay i async – reducerer main-thread belastning.
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject

@HiltAndroidApp
class MyApplication : Application() {

    @Inject lateinit var authManager: AuthManager

    private val coroutineScope = CoroutineScope(Dispatchers.IO)  // BEHOLDT: IO dispatcher for async init – reducerer main thread belastning (fikser skipped frames).

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        coroutineScope.launch {
            initializeFirebaseAsync()  // BEHOLDT: OPDATERET: Flyt til async – undgår blokering af main thread under init (fikser Davey! og skipped frames).
            initializeFirestoreAsync()
            performGdprCleanupAsync()  // BEHOLDT: OPDATERET: Flyt GDPR cleanup til baggrund – undgår main thread overbelastning.
            checkLoggedInAsync()  // BEHOLDT: TILFØJET: Flyt logged-in check til async.
        }
    }

    private suspend fun initializeFirebaseAsync() {
        if (FirebaseApp.getApps(this@MyApplication).isEmpty()) {  // BEHOLDT: Init FirebaseApp tidligt, men async.
            FirebaseApp.initializeApp(this@MyApplication)
        }
        val appCheck = FirebaseAppCheck.getInstance()
        try {
            if (BuildConfig.DEBUG) {
                appCheck.installAppCheckProviderFactory(DebugAppCheckProviderFactory.getInstance())
                val token = appCheck.getToken(false).await()
                Log.d("AppCheck", "Debug token: ${token.token} – Tilføj i Firebase Console > App Check > Debug")
            } else {
                appCheck.installAppCheckProviderFactory(PlayIntegrityAppCheckProviderFactory.getInstance())
                Log.d("AppCheck", "Play Integrity aktiveret")
            }
        } catch (e: Exception) {
            Log.e("AppCheck", "Init fejl: ${e.message} – fortsæt uden")
        }
    }

    private suspend fun initializeFirestoreAsync() {
        val db = FirebaseFirestore.getInstance()
        db.firestoreSettings = FirebaseFirestoreSettings.Builder()
            .setPersistenceEnabled(true)
            .build()
        Timber.d("Firestore: Offline persistence aktiveret")
    }

    private suspend fun performGdprCleanupAsync() {
        try {  // BEHOLDT FIX: Try-catch for at håndtere Room schema-mismatch (IllegalStateException) – logger fejl, fortsætter uden crash ved opstart.
            // NY FIX: Delay for at undgå IO-overload ved app-start.
            delay(500L)  // Kort pause – reducerer skipped frames under init.
            authManager.cleanupOldUsers()
            Timber.d("GDPR cleanup udført")
        } catch (e: IllegalStateException) {
            Timber.e(e, "GDPR cleanup fejlede – database schema-mismatch, tjek AppDatabase version og migration. Expected hash: 6bf0016f3ac5386e02f189c0acc7c77e, found: ${e.message?.substringAfter("found:") ?: "unknown"}. App fortsætter uden cleanup.")
            // Fanger Room-crash (fx version mismatch efter 'bids'-tilføjelse), logger detaljer med hash (for debugging), fortsætter – app starter uden total nedlukning.
        } catch (e: Exception) {
            Timber.e(e, "Uventet fejl i GDPR cleanup")
        }
    }

    private suspend fun checkLoggedInAsync() {
        val isLoggedIn = authManager.isLoggedIn()
        Timber.d("MyApplication: Bruger logget ind ved start? $isLoggedIn")
    }
}