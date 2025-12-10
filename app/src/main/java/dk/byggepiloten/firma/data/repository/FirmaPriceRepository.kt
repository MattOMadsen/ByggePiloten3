// File: app/src/main/java/dk/byggepiloten/firma/data/repository/FirmaPriceRepository.kt
// FULD VERSION – RETTET: Tilføjet getNotificationPrefs, setNotificationPrefs, syncBackups + fjernet default fra retentionDays i savePrices (løser overriding default fejl).
// Trin-for-trin forklaring:
// 1. Tilføjet suspend fun getNotificationPrefs(): NotificationSettings? – henter settings eller null (brug i VM for load).
// 2. Tilføjet suspend fun setNotificationPrefs(settings: NotificationSettings) – gemmer settings (brug i VM for save).
// 3. Tilføjet suspend fun syncBackups() – synkroniserer backups (brug i VM og SyncWorker).
// 4. Fjernet default retentionDays: Int = 7 fra savePrices – nu matcher impl uden default (løser overriding fejl).
// 5. Behold alle eksisterende metoder uændret (ingen sletninger – kun tilføjelser/fixes for at gøre interface komplet).
// 6. Efter opdatering: Sync Gradle – nu ingen unresolved i VM/SyncWorker.
// 7. Fremtid: Implementer i impl med Firestore/DataStore-logik for prefs og sync.

package dk.byggepiloten.firma.data.repository

import dk.byggepiloten.firma.data.model.BackupInfo
import dk.byggepiloten.firma.data.model.FirmaMaterialPrice
import dk.byggepiloten.firma.data.model.ImportMode
import dk.byggepiloten.firma.data.model.NotificationSettings  // TILFØJET: Import for NotificationSettings – nødvendigt for prefs-metoder.

interface FirmaPriceRepository {

    suspend fun savePrice(price: FirmaMaterialPrice)

    suspend fun getAllPrices(): List<FirmaMaterialPrice>

    suspend fun importCsv(csvData: String, mode: ImportMode, retentionDays: Int)  // Behold: Med retentionDays for at matche VM-kald.

    suspend fun exportCsv(): String

    suspend fun getBackups(): List<BackupInfo>

    suspend fun restoreBackup(backupId: Int)

    suspend fun savePrices(
        profitPct: Float,
        hourlyRate: Float,
        hourlyOvertime: Float,
        drivingPerKm: Float,
        importMode: ImportMode,
        prices: Map<String, Float?>,
        retentionDays: Int  // RETTET: Fjernet default = 7 – nu matcher impl uden default.
    )

    suspend fun getNotificationPrefs(): NotificationSettings?  // TILFØJET: Hent prefs – return null hvis ingen.

    suspend fun setNotificationPrefs(settings: NotificationSettings)  // TILFØJET: Sæt prefs.

    suspend fun syncBackups()  // TILFØJET: Synk backups – brug i VM og SyncWorker.
}