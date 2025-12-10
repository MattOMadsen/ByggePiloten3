// File: app/src/main/java/dk/byggepiloten/firma/worker/SyncWorker.kt
// FULD VERSION – rettet FirebaseUser-import, suspension og type-infer.

package dk.byggepiloten.firma.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.firebase.auth.FirebaseUser  // RETTET: Tilføjet import – løser unresolved FirebaseUser
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dk.byggepiloten.firma.data.database.AppDatabase
import dk.byggepiloten.firma.data.repository.FirmaPriceRepository
import dk.byggepiloten.firma.data.repository.RequestRepository
import dk.byggepiloten.firma.data.repository.UserRepository
import timber.log.Timber
import java.util.concurrent.TimeUnit

@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val firmaPriceRepository: FirmaPriceRepository,
    private val requestRepository: RequestRepository,
    private val userRepository: UserRepository,
    private val appDatabase: AppDatabase
) : CoroutineWorker(context, params) {

    // DAO for GDPR-slet (fås fra injiceret AppDatabase)
    private val userDao = appDatabase.userDao()

    override suspend fun doWork(): Result {
        return try {
            Timber.d("SyncWorker started – GDPR + full sync")

            // Step 1: GDPR-slet gamle users (lokal Room – created_at < now - 24h)
            val twentyFourHoursAgo = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(1)
            val deletedCount = userDao.deleteOldUsers(twentyFourHoursAgo)
            Timber.d("GDPR: Deleted $deletedCount old users")

            // Step 2: Sync firma-priser / backups
            firmaPriceRepository.syncBackups()
            Timber.d("Synced firma price backups")

            // Step 3: Sync requests (Room → Firestore)
            requestRepository.syncRequests()
            Timber.d("Synced requests")

            // Step 4: Sync current user (hvis logget ind)
            // RETTET: Direkte kald (suspend doWork tillader det). Eksplicit type for FirebaseUser?. Hent role separat.
            // Trin: 1. Kall getCurrentUser(). 2. Hent role via getSavedRole() (suspend – OK i coroutine). 3. Log med fallback.
            val currentUser: FirebaseUser? = userRepository.getCurrentUser()
            if (currentUser != null) {
                val role: String? = userRepository.getSavedRole()  // RETTET: Flyttet ud af let – løser suspension
                Timber.d("Synced user: ${currentUser.email} (role: ${role ?: "unknown"})")  // RETTET: Brug currentUser.email – resolved
                // Her kan du tilføje Firestore-sync hvis nødvendigt
            }

            Timber.d("SyncWorker completed successfully")
            Result.success()
        } catch (e: Exception) {
            Timber.e(e, "SyncWorker failed – will retry")
            Result.retry()
        }
    }
}