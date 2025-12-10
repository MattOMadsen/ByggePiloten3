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
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject

@HiltAndroidApp
class MyApplication : Application() {

    @Inject lateinit var authManager: AuthManager

    private val coroutineScope = CoroutineScope(Dispatchers.IO)  // TILFØJET: IO dispatcher for async init – reducerer main thread belastning (fikser skipped frames).

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        coroutineScope.launch {
            initializeFirebaseAsync()  // OPDATERET: Flyt til async – undgår blokering af main thread under init (fikser Davey! og skipped frames).
            initializeFirestoreAsync()
            performGdprCleanupAsync()  // OPDATERET: Flyt GDPR cleanup til baggrund – undgår main thread overbelastning.
            checkLoggedInAsync()  // TILFØJET: Flyt logged-in check til async.
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
        authManager.cleanupOldUsers()
        Timber.d("GDPR cleanup udført")
    }

    private suspend fun checkLoggedInAsync() {
        val isLoggedIn = authManager.isLoggedIn()
        Timber.d("MyApplication: Bruger logget ind ved start? $isLoggedIn")
    }
}