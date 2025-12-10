// File: app/src/main/java/dk/byggepiloten/firma/data/database/FirmaMaterialDao.kt
// FULD VERSION – RETTET: Tilføjet insertAll() for at løse unresolved 'insertAll' i FirmaPriceRepositoryImpl.
// Trin-for-trin forklaring:
// 1. Tilføjet @Insert suspend fun insertAll(prices: List<FirmaMaterialPrice>) – nu kan repo kalde dao.insertAll() direkte.
// 2. Behold alle eksisterende metoder uændret (ingen sletninger – kun tilføjelse for kompatibilitet).
// 3. Behold @Dao-annotation og interface uændret.
// 4. Efter opdatering: Sync Gradle – nu resolved i repo.
// 5. Fremtid: Tilføj onConflict = REPLACE hvis nødvendigt for bulk-inserts.

package dk.byggepiloten.firma.data.database

import androidx.room.*
import dk.byggepiloten.firma.data.model.FirmaMaterialPrice

/**
 * FirmaMaterialDao: Room DAO for firma_materials.
 * - Insert, update, delete, query priser.
 * - Understøtter import/export via bulk ops.
 * - GDPR: Delete after retention.
 */
@Dao
interface FirmaMaterialDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(price: FirmaMaterialPrice)

    @Insert(onConflict = OnConflictStrategy.REPLACE)  // TILFØJET: Ny metode for bulk-insert – løser unresolved 'insertAll' i repo.
    suspend fun insertAll(prices: List<FirmaMaterialPrice>)

    @Update
    suspend fun update(price: FirmaMaterialPrice)

    @Query("DELETE FROM firma_materials")
    suspend fun deleteAll()

    @Query("SELECT * FROM firma_materials")
    suspend fun getAll(): List<FirmaMaterialPrice>
}