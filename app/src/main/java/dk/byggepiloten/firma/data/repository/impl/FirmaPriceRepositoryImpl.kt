package dk.byggepiloten.firma.data.repository.impl

import com.google.firebase.firestore.FirebaseFirestore
import dk.byggepiloten.firma.data.database.BackupDao
import dk.byggepiloten.firma.data.database.FirmaMaterialDao
import dk.byggepiloten.firma.data.model.BackupInfo
import dk.byggepiloten.firma.data.model.FirmaMaterialPrice
import dk.byggepiloten.firma.data.model.ImportMode
import dk.byggepiloten.firma.data.model.NotificationSettings
import dk.byggepiloten.firma.data.repository.FirmaPriceRepository
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirmaPriceRepositoryImpl @Inject constructor(
    private val dao: FirmaMaterialDao,
    private val backupDao: BackupDao,
    private val firestore: FirebaseFirestore
) : FirmaPriceRepository {

    override suspend fun savePrice(price: FirmaMaterialPrice) {
        dao.insert(price)
        syncToFirestore(price)
    }

    override suspend fun getAllPrices(): List<FirmaMaterialPrice> {
        return dao.getAll()
    }

    override suspend fun importCsv(csvData: String, mode: ImportMode, retentionDays: Int) {
        val lines = csvData.lines().drop(1)
        val prices: List<FirmaMaterialPrice> = lines.mapNotNull { line ->
            val parts = line.split(",")
            if (parts.size >= 4) {
                FirmaMaterialPrice(
                    material = parts[0],
                    customPrice = parts[1].toFloatOrNull() ?: 0f,
                    unit = parts[2],
                    profitPct = parts[3].toFloatOrNull() ?: 0f
                )
            } else null
        }
        when (mode) {
            ImportMode.ADD_ONLY -> prices.forEach { dao.insert(it) }
            ImportMode.OVERWRITE -> prices.forEach { dao.update(it) }
            ImportMode.REPLACE_ALL -> {
                dao.deleteAll()
                dao.insertAll(prices)
            }
        }
        createBackup(retentionDays)
    }

    override suspend fun exportCsv(): String {
        val prices = getAllPrices()
        val builder = StringBuilder("Material,Pris,Enhed,Fortjeneste%\n")
        prices.forEach {
            builder.append("${it.material},${it.customPrice},${it.unit},${it.profitPct}\n")
        }
        return builder.toString()
    }

    override suspend fun getBackups(): List<BackupInfo> {
        return backupDao.getAll()
    }

    override suspend fun restoreBackup(backupId: Int) {
        val backup = backupDao.getById(backupId)
        backup?.prices?.let { priceStrings ->
            val prices = priceStrings.mapNotNull { line ->
                val parts = line.split(",")
                if (parts.size >= 4) {
                    FirmaMaterialPrice(
                        material = parts[0],
                        customPrice = parts[1].toFloatOrNull() ?: 0f,
                        unit = parts[2],
                        profitPct = parts[3].toFloatOrNull() ?: 0f
                    )
                } else null
            }
            dao.insertAll(prices)
        }
    }

    override suspend fun savePrices(
        profitPct: Float,
        hourlyRate: Float,
        hourlyOvertime: Float,
        drivingPerKm: Float,
        importMode: ImportMode,
        prices: Map<String, Float?>,
        retentionDays: Int
    ) {
        savePrice(
            FirmaMaterialPrice(
                material = "global_profit_pct",
                customPrice = profitPct,
                unit = "%",
                profitPct = 0f
            )
        )
        savePrice(
            FirmaMaterialPrice(
                material = "hourly_rate_normal",
                customPrice = hourlyRate,
                unit = "kr/time",
                profitPct = 0f
            )
        )
        savePrice(
            FirmaMaterialPrice(
                material = "hourly_rate_overtime",
                customPrice = hourlyOvertime,
                unit = "kr/time",
                profitPct = 0f
            )
        )
        savePrice(
            FirmaMaterialPrice(
                material = "driving_per_km",
                customPrice = drivingPerKm,
                unit = "kr/km",
                profitPct = 0f
            )
        )
        savePrice(
            FirmaMaterialPrice(
                material = "import_mode",
                customPrice = 0f,
                unit = importMode.name,
                profitPct = 0f
            )
        )
        prices.forEach { (category, price) ->
            if (price != null) {
                val unit = if (category.contains("(m²)")) "kr/m²" else if (category.contains("(stk)")) "kr/stk" else if (category.contains("(time)")) "kr/time" else if (category.contains("(lm)")) "kr/lm" else "kr/m²"
                savePrice(
                    FirmaMaterialPrice(
                        material = category,
                        customPrice = price,
                        unit = unit,
                        profitPct = profitPct
                    )
                )
            }
        }
        createBackup(retentionDays)
    }

    override suspend fun getNotificationPrefs(): NotificationSettings? {
        val doc = firestore.collection("notification_prefs").document("global").get().await()
        return if (doc.exists()) {
            doc.toObject(NotificationSettings::class.java)
        } else null
    }

    override suspend fun setNotificationPrefs(settings: NotificationSettings) {
        firestore.collection("notification_prefs").document("global").set(settings).await()
        Timber.d("Notification prefs sat: $settings")
    }

    override suspend fun syncBackups() {
        val backups = getBackups()
        backups.forEach { backup ->
            firestore.collection("backups").document(backup.id.toString()).set(backup).await()
        }
        Timber.d("Synced ${backups.size} backups to Firestore")
    }

    private suspend fun syncToFirestore(price: FirmaMaterialPrice) {
        firestore.collection("firma_prices").document(price.material)
            .set(price).await()
    }

    private suspend fun createBackup(retentionDays: Int) {
        val prices = getAllPrices()
        val priceStrings = prices.map { "${it.material},${it.customPrice},${it.unit},${it.profitPct}" }
        val backup = BackupInfo(
            id = System.currentTimeMillis().toInt(),
            firmaId = 1,
            version = "1.0",
            backupTimestamp = System.currentTimeMillis().toString(),
            retentionDays = retentionDays,
            prices = priceStrings
        )
        backupDao.insert(backup)
    }
}